package com.crazypig.rpc.netty;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcServerMain {
	
	public static void main(String[] args) {
		
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {
			"classpath:applicationContext-rpcServer.xml"
		});
		
		ctx.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {
				ctx.close();
			}
		}));
		
	}

}
