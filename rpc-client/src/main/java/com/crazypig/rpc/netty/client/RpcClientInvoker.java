package com.crazypig.rpc.netty.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crazypig.rpc.netty.protocol.RpcRequest;
import com.crazypig.rpc.netty.protocol.RpcRequestBuilder;
import com.crazypig.rpc.netty.protocol.RpcResponse;
import com.crazypig.rpc.netty.registry.ServerAddress;
import com.crazypig.rpc.netty.registry.ServiceDiscovery;

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
    
    public RpcClientInvoker(Class<?> targetServiceClass, RpcClient context) {
        this.targetServiceClass = targetServiceClass;
        this.nettyClient = context.getNettyClient();
        this.serviceDiscovery = context.getServiceDiscovery();
        this.serverAddressLoadBalancer = new DefaultServerAddressLoadBalancer();
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
        RpcRequest request = buildRpcRequest(targetServiceClass, method, args);
        // 请求远程RPC调用
        RpcResponseFuture rpcRespFuture = clientStub.sendRpcRequest(request);
        
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = rpcRespFuture.get();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // 归还RpcConnection
            clientStub.close();
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

}
