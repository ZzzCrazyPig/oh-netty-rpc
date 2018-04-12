package com.crazypig.rpc.netty.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.crazypig.rpc.netty.asyncservice.HelloServiceAsync;
import com.crazypig.rpc.netty.client.stub.async.RpcInvocationCallback;
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
		
		HelloServiceAsync helloServiceAsync = rpcClient.createServiceProxy(HelloServiceAsync.class);
		helloServiceAsync.sayHelloAgainAsync("CrazyPig asyncByCallback ", 1314, new RpcInvocationCallback<String>() {
            
            @Override
            public void onSuccess(String result) {
                logger.info("on sayHelloAgainAsync callback, get result : {}", result);
            }
            
            @Override
            public void onFailure(Throwable t) {
                logger.info("on sayHelloAgainAsync callback, get error : {}", t.getMessage());
                logger.error(t.getMessage(), t);
            }
        });
		
		Future<String> future = helloServiceAsync.sayHelloAgainAsync("CrazyPig asyncByFuture ", 1314);
		
		try {
		    String asyncFutureResult = future.get();
		    logger.info("get async future result : {}", asyncFutureResult);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
		
		// 销毁rpcClient
		rpcClient.stop();
		
		ctx.close();
		
	}

}
