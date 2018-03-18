package com.crazypig.rpc.netty.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.crazypig.rpc.netty.protocol.RpcResponse;

/**
 * RPC调用返回结果future
 * @author CrazyPig
 *
 */
public final class RpcResponseFuture implements Future<RpcResponse> {
    
    private RpcResponse response = null;
    private final CountDownLatch cdl = new CountDownLatch(1);
    private boolean done = false;
    private boolean cancel = false;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        cdl.countDown();
        cancel = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        cdl.await();
        return response;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        cdl.await(timeout, unit);
        return response;
    }
    
    public void setDone(RpcResponse response) {
        this.response = response;
        cdl.countDown();
        done = true;
    }
    
}
