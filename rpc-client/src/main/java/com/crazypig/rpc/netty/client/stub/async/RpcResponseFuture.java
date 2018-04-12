package com.crazypig.rpc.netty.client.stub.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.crazypig.rpc.netty.client.stub.RpcConnection;
import com.crazypig.rpc.netty.protocol.RpcResponse;
import com.google.common.util.concurrent.AbstractFuture;

/**
 * RPC调用返回结果future
 * @author CrazyPig
 *
 */
public final class RpcResponseFuture extends AbstractFuture<RpcResponse> {
    
    private RpcConnection rpcConn;
    
    public RpcResponseFuture(RpcConnection rpcConn) {
        this.rpcConn = rpcConn;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            return super.cancel(mayInterruptIfRunning);
        } finally {
            rpcConn.close();
        }
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled();
    }

    @Override
    public boolean isDone() {
        return super.isDone();
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        return super.get();
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return super.get(timeout, unit);
    }
    
    public void setDone(RpcResponse response) {
        try {
            set(response);
        } finally {
            rpcConn.close();
        }
    }
    
}
