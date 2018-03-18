package com.crazypig.rpc.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.crazypig.rpc.netty.service.HelloService;

public class RpcClientMain {
	
	private static Logger logger = LoggerFactory.getLogger(RpcClientMain.class);
	
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {
			"classpath:applicationContext-rpcClient.xml"
		});
		
		ctx.start();
		
		RpcClient rpcClient = ctx.getBean(RpcClient.class);
		// 初始化rpcClient
		rpcClient.init();
		
		HelloService helloService = rpcClient.createServiceProxy(HelloService.class);
		logger.info(helloService.sayHelloAgain("CrazyPig", 1314));
		
		// 销毁rpcClient
		rpcClient.stop();
		
		ctx.close();
		
	}

}
