package com.crazypig.rpc.netty.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;
    
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        SpringUtil.ctx = ctx;
    }

    public static ApplicationContext getSpringContext() {
        return ctx;
    }
    
}
