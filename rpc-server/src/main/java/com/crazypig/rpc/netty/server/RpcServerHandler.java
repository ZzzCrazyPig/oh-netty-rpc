package com.crazypig.rpc.netty.server;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.crazypig.rpc.netty.protocol.RpcRequest;
import com.crazypig.rpc.netty.protocol.RpcResponse;
import com.crazypig.rpc.netty.utils.SpringUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * netty server handler 处理rpc请求的实际调用, 并回写响应
 * 
 * @author CrazyPig
 *
 */
@Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        
        ListeningExecutorService guavaExecutor =
                ctx.channel().attr(NettyServer.ATTR_BUSINESS_EXECUTOR).get();
        
        // 在业务线程池中执行service调用
        ListenableFuture<RpcResponse> future = guavaExecutor.submit(new Callable<RpcResponse>() {

            @Override
            public RpcResponse call() throws Exception {

                RpcResponse response = new RpcResponse();
                // 调用真正的service
                Object result = null;
                try {
                    result = invoke(request);
                    response.setCode(RpcResponse.SUCCESS_CODE);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    response.setCode(RpcResponse.ERROR_CODE);
                }
                response.setResult(result);
                response.setRequestId(request.getRequestId());

                return response;
            }

        });
        
        // service执行结束后回调处理
        Futures.addCallback(future, new FutureCallback<RpcResponse>() {

            @Override
            public void onSuccess(RpcResponse response) {
                // 回写response
                ctx.channel().writeAndFlush(response);
            }

            @Override
            public void onFailure(Throwable t) {
                // 异常也回写response
                RpcResponse response = new RpcResponse();
                response.setCode(RpcResponse.ERROR_CODE);
                response.setRequestId(request.getRequestId());
                ctx.channel().writeAndFlush(response);
            }
        }, guavaExecutor);
        
    }
    
    private Object invoke(RpcRequest request) throws Exception {
        String serviceClassName = request.getServiceName();
        Class<?> serviceClass = Class.forName(serviceClassName);
        Object bean = SpringUtil.getSpringContext().getBean(serviceClass);
        Method method = serviceClass.getMethod(request.getMethodName(), getParamTypes(request.getParamTypes()));
        return method.invoke(bean, request.getParamValues());
    }
    
    private Class<?>[] getParamTypes(String[] paramTypes) throws ClassNotFoundException {
        Class<?>[] classes = new Class<?>[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            classes[i] = getParamType(paramTypes[i]);
        }
        return classes;
    }
    
    private Class<?> getParamType(String paramType) throws ClassNotFoundException {
        // 处理内置基本数据类型
        if ("int".equals(paramType)) {
            return Integer.TYPE;
        } else if ("double".equals(paramType)) {
            return Double.TYPE;
        } else if ("float".equals(paramType)) {
            return Float.TYPE;
        } else if ("boolean".equals(paramType)) {
            return Boolean.TYPE;
        } else if ("long".equals(paramType)) {
            return Long.TYPE;
        } else if ("byte".equals(paramType)) {
            return Byte.TYPE;
        } else if ("char".equals(paramType)) {
            return Character.TYPE;
        }
        return Class.forName(paramType);
    }
    
}
