package com.crazypig.rpc.netty;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.crazypig.rpc.netty.client.RpcClient;
import com.crazypig.rpc.netty.server.RpcServer;
import com.crazypig.rpc.netty.service.HelloService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
public class RpcConcurrentTest {
	
	private static Logger logger = LoggerFactory.getLogger(RpcConcurrentTest.class);
	
	@Autowired
    private RpcServer rpcServer;
	@Autowired
    private RpcClient rpcClient;
    
    @Before
    public void startRpcServer() throws Exception {
    	rpcServer.start();
    }
    
    @Test
    public void test() {
        rpcClient.init();
        final HelloService helloService = rpcClient.createServiceProxy(HelloService.class);
        int thdCount = 100;
        Thread[] thds = new Thread[thdCount];
        final CountDownLatch startCdl = new CountDownLatch(1);
        final CountDownLatch endCdl = new CountDownLatch(thdCount);
        
        final AtomicBoolean startBoolean = new AtomicBoolean(false);
        final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        
        for (int i = 0; i < thds.length; i++) {
            final int k = i;
            thds[i] = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        startCdl.await();
                        if (startBoolean.compareAndSet(false, true)) {
                            startTime.set(System.currentTimeMillis());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(helloService.sayHelloAgain("CrazyPig", k));
                    endCdl.countDown();
                }
            }, "thd" + i);
            thds[i].start();
        }
        
        startCdl.countDown();
        try {
            endCdl.await();
            logger.info("cost : " + (System.currentTimeMillis() - startTime.get()) + "ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void destory() {
        rpcClient.stop();
        rpcServer.shutdown();
    }

}
