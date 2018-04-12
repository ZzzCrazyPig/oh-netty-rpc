package com.crazypig.rpc.netty.service;

/**
 * 模拟测试用的服务接口
 * @author CrazyPig
 *
 */
public interface HelloService {
    
    public void sayHello(String name);
    
    public String sayHelloAgain(String name, Integer num);

}
