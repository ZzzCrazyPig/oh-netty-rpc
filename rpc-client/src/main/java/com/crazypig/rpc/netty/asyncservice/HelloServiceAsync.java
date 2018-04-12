package com.crazypig.rpc.netty.asyncservice;

import java.util.concurrent.Future;

import com.crazypig.rpc.netty.client.stub.async.AsyncRpc;
import com.crazypig.rpc.netty.client.stub.async.AsyncRpcMethod;
import com.crazypig.rpc.netty.client.stub.async.AsyncRpcMethodType;
import com.crazypig.rpc.netty.client.stub.async.RpcInvocationCallback;
import com.crazypig.rpc.netty.service.HelloService;

/**
 * 扩展helloService, 增加异步调用方法的定义, 仅作示例用
 * @author CrazyPig
 *
 */
@AsyncRpc
public interface HelloServiceAsync extends HelloService {
    
    @AsyncRpcMethod(origin = "sayHelloAgain", type = AsyncRpcMethodType.CALLBACK)
    void sayHelloAgainAsync(String name, Integer num, RpcInvocationCallback<String> callback);
    
    @AsyncRpcMethod(origin = "sayHelloAgain", type = AsyncRpcMethodType.FUTURE)
    Future<String> sayHelloAgainAsync(String name, Integer num);

}
