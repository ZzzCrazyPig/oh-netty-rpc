package com.crazypig.rpc.netty.client.stub.async;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解标识的接口类将被当成异步rpc接口, 
 * 协助{@link AsyncRpcMethod}支持异步rpc调用
 * @author CrazyPig
 *
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncRpc {

}
