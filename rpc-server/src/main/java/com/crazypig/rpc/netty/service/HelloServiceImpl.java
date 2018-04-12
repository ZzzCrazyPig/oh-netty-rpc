package com.crazypig.rpc.netty.service;

import org.springframework.stereotype.Service;

/**
 * 
 * 测试服务接口实现类
 * 
 * @author CrazyPig
 *
 */
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public void sayHello(String name) {
        System.out.println("hello : " + name);
    }

    @Override
    public String sayHelloAgain(String name, Integer num) {
        return "hello : " + name + num;
    }

}
