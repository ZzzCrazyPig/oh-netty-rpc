package com.crazypig.rpc.netty.client.stub.async;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于指定一个接口方法为异步rpc方法, 
 * 注意该接口方法所在接口必须使用{@link AsyncRpc}注解
 * @author CrazyPig
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncRpcMethod {
    
    /**
     * 指向原始接口
     * @return
     */
    String origin();
    
    /**
     * 异步rpc类型
     * @return
     */
    AsyncRpcMethodType type();
    
    

}
