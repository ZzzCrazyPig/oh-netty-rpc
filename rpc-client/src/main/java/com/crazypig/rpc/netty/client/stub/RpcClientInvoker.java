package com.crazypig.rpc.netty.client.stub;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crazypig.rpc.netty.client.RpcClient;
import com.crazypig.rpc.netty.client.stub.async.AsyncRpc;
import com.crazypig.rpc.netty.client.stub.async.AsyncRpcMethod;
import com.crazypig.rpc.netty.client.stub.async.AsyncRpcMethodType;
import com.crazypig.rpc.netty.client.stub.async.DelegateRpcResponseFuture;
import com.crazypig.rpc.netty.client.stub.async.RpcInvocationCallback;
import com.crazypig.rpc.netty.client.stub.async.RpcResponseFuture;
import com.crazypig.rpc.netty.client.stub.lb.DefaultServerAddressLoadBalancer;
import com.crazypig.rpc.netty.client.stub.lb.ServerAddressLoadBalancer;
import com.crazypig.rpc.netty.protocol.RpcRequest;
import com.crazypig.rpc.netty.protocol.RpcRequestBuilder;
import com.crazypig.rpc.netty.protocol.RpcResponse;
import com.crazypig.rpc.netty.registry.ServerAddress;
import com.crazypig.rpc.netty.registry.ServiceDiscovery;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

/**
 * RPC client实现, 采用动态代理实现, 将服务接口调用的元数据封装成{@link RpcRequest}然后传送到RPC server端执行
 * @author CrazyPig
 *
 */
public class RpcClientInvoker implements InvocationHandler {
    
    private static Logger logger = LoggerFactory.getLogger(RpcClientInvoker.class);
    
    private NettyClient nettyClient;
    private ServiceDiscovery serviceDiscovery;
    private ServerAddressLoadBalancer serverAddressLoadBalancer;
    private Class<?> targetServiceClass;
    private ExecutorService executor;
    
    public RpcClientInvoker(Class<?> targetServiceClass, RpcClient context) {
        if (targetServiceClass.isAnnotationPresent(AsyncRpc.class)) {
            // TODO 需要强化
            this.targetServiceClass = (targetServiceClass.getInterfaces() != null ? 
                    targetServiceClass.getInterfaces()[0] : targetServiceClass);
        } else {
            this.targetServiceClass = targetServiceClass;
        }
        this.nettyClient = context.getNettyClient();
        this.serviceDiscovery = context.getServiceDiscovery();
        this.serverAddressLoadBalancer = new DefaultServerAddressLoadBalancer();
        this.executor = context.getBusinessExecutor();
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        if (method.getDeclaringClass() == Object.class) {
            return processObjectClassMethodInvoke(proxy, method, args);
        }
        
        // 注册中心查找所在的服务ip和端口
        ServerAddress serverAddress = serverAddressLoadBalancer.getServerAddress(serviceDiscovery.getAllServerAddress());
        // 从netty client中获取rpc连接  
        RpcConnection clientStub = nettyClient.getRpcConnection(serverAddress.getHost(), serverAddress.getPort());
        // 构建rpc request
        RpcRequest request = null;

        // 判断是同步接口调用还是异步接口调用
        boolean isAsyncRpcMethod = method.isAnnotationPresent(AsyncRpcMethod.class);
        AsyncRpcMethod anno = null;
        if (isAsyncRpcMethod) {
            anno = method.getDeclaredAnnotation(AsyncRpcMethod.class);
            AsyncRpcMethodType asyncType = anno.type();
            Method realMethod = null;
            Object[] realArgs = null;
            switch (asyncType) {
                case CALLBACK:
                    realArgs = getRealArgsOfAsyncRpcMethod(args, AsyncRpcMethodType.CALLBACK);
                    realMethod = getRealMethodOfAsyncRpcMethod(anno.origin(), getParameterTypesOfArgs(realArgs));
                    break;
                case FUTURE:
                    realArgs = args;
                    realMethod = getRealMethodOfAsyncRpcMethod(anno.origin(), getParameterTypesOfArgs(args));
                    break;
                default:
                    throw new IllegalAccessException("unknown asyncType : " + asyncType);
            }
            request = buildRpcRequest(targetServiceClass, realMethod, realArgs);
        } else {
            request = buildRpcRequest(targetServiceClass, method, args);
        }
        
        
        // 请求远程RPC调用
        RpcResponseFuture rpcRespFuture = clientStub.sendRpcRequest(request);
        
        // 异步调用
        if (isAsyncRpcMethod) {
            switch (anno.type()) {
                case CALLBACK:
                    // 获取client定义的回调函数
                    final RpcInvocationCallback<Object> clientCallback = getClientDefinitionCallback(args);
                    // 通过Google Guava异步框架添加callback, 在callback里面回调client定义的回调函数
                    Futures.addCallback(rpcRespFuture, new FutureCallback<RpcResponse>() {

                        @Override
                        public void onSuccess(RpcResponse result) {
                            clientCallback.onSuccess(result.getResult());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            clientCallback.onFailure(t);
                        }
                        
                    }, executor);
                    return null;
                case FUTURE:
                    return new DelegateRpcResponseFuture(rpcRespFuture);
                default:
                    throw new IllegalAccessException("unknown asyncType : " + anno.type());
            }
        }
        
        RpcResponse rpcResponse = null;
        try {
            // 同步阻塞获取
            rpcResponse = rpcRespFuture.get();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (method.getReturnType() == Void.class) {
            return null;
        }
        return rpcRespFuture == null ? null : rpcResponse.getResult();
    }
    
    private RpcRequest buildRpcRequest(Class<?> targetServiceClass, Method method, Object[] args) {
        RpcRequest request = RpcRequestBuilder.newBuilder()
            .setRequestId(UUID.randomUUID().toString())
            .setServiceName(targetServiceClass.getName())
            .setMethodName(method.getName())
            .setParamTypes(getParamTypeNames(method.getParameterTypes()))
            .setParamValues(args)
            .build();
        return request;
    }
    
    private String[] getParamTypeNames(Class<?>[] paramTypes) {
        if (paramTypes == null || paramTypes.length == 0) {
            return new String[] {};
        }
        String[] paramTypeNames = new String[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypeNames[i] = paramTypes[i].getName();
        }
        return paramTypeNames;
    }
    
    private Object processObjectClassMethodInvoke(Object proxy, Method method, Object[] args) throws IllegalAccessException {
        String objectMethodName = method.getName();
        if ("equals".equals(objectMethodName)) {
            return proxy == args[0];
        } else if ("hashCode".equals(objectMethodName)) {
            return System.identityHashCode(proxy);
        } else if ("toString".equals(objectMethodName)) {
            return proxy.getClass().getName() + "@" +
                    Integer.toHexString(System.identityHashCode(proxy)) +
                    ", with InvocationHandler " + this;
        } else {
            logger.warn("can't invoke method : {}", objectMethodName);
            throw new IllegalAccessException("can't invoke method : " + objectMethodName);
        }
    }
    
    private Method getRealMethodOfAsyncRpcMethod(String originMethodName, Class<?>[] parameterTypes) throws NoSuchMethodException, SecurityException {
        Method targetMethod = targetServiceClass.getDeclaredMethod(originMethodName, parameterTypes);
        return targetMethod;
    }
    
    private Object[] getRealArgsOfAsyncRpcMethod(Object[] args, AsyncRpcMethodType asyncRpcMethodType) {
        if (asyncRpcMethodType == AsyncRpcMethodType.CALLBACK) {
            Object[] realArgs = new Object[args.length - 1];
            System.arraycopy(args, 0, realArgs, 0, realArgs.length);
            return realArgs;
        }
        return args;
    }
    
    private Class<?>[] getParameterTypesOfArgs(Object[] args) {
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        return paramTypes;
    }
    
    @SuppressWarnings("unchecked")
    private RpcInvocationCallback<Object> getClientDefinitionCallback(Object[] args) {
        RpcInvocationCallback<Object> callback = (RpcInvocationCallback<Object>) args[args.length - 1];
        return callback;
    }
    
}
