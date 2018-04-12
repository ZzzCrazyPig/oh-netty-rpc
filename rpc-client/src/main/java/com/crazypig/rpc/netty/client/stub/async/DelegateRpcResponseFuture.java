package com.crazypig.rpc.netty.client.stub.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.crazypig.rpc.netty.protocol.RpcResponse;

/**
 * 对RpcResponseFuture进行包装, 用于支持异步RPC future方式返回
 * @author CrazyPig
 *
 */
public class DelegateRpcResponseFuture implements Future<Object> {
    
    private RpcResponseFuture future;
    
    public DelegateRpcResponseFuture(RpcResponseFuture future) {
        this.future = future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        RpcResponse rpcResponse =  future.get();
        return rpcResponse == null ? null : rpcResponse.getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        RpcResponse rpcResponse =  future.get(timeout, unit);
        return rpcResponse == null ? null : rpcResponse.getResult(); 
    }

}
