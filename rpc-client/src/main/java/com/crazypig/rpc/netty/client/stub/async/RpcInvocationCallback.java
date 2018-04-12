package com.crazypig.rpc.netty.client.stub.async;

import com.google.common.util.concurrent.FutureCallback;

/**
 * 定义异步rpc callback方式请求回调接口
 * @author CrazyPig
 *
 * @param <T>
 */
public interface RpcInvocationCallback<T> extends FutureCallback<T> {
    
}
