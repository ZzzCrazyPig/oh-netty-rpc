package com.crazypig.rpc.netty.client.stub.async;

/**
 * 定义异步rpc实现类型
 * @author CrazyPig
 *
 */
public enum AsyncRpcMethodType {
    
    /** 客户端提供callback方式 **/
    CALLBACK,
    /** 返回future方式 **/
    FUTURE

}
