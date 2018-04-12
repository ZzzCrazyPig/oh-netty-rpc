package com.crazypig.rpc.netty.client;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.crazypig.rpc.netty.client.stub.NettyClient;
import com.crazypig.rpc.netty.client.stub.RpcClientInvoker;
import com.crazypig.rpc.netty.registry.ServiceDiscovery;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * rpc客户端
 * @author CrazyPig
 *
 */
public class RpcClient {
    
    private NettyClient nettyClient;
    private ServiceDiscovery serviceDiscovery;
    private ListeningExecutorService businessExecutor;
    
    public RpcClient() {}
    
    public RpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    
    public void init() {
    	if (serviceDiscovery == null) {
    		throw new IllegalArgumentException("serviceDiscovery is null");
    	}
    	this.nettyClient = NettyClient.createNettyClient();
        // 连接注册中心
        this.serviceDiscovery.start();
        // 订阅
        this.serviceDiscovery.subscribe();
        // 创建线程池  TODO 可配置大小
        ThreadPoolExecutor jdkExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1024), 
                new ThreadFactoryBuilder().setNameFormat("rpcClientExecutor%d").build(), new ThreadPoolExecutor.CallerRunsPolicy());
        this.businessExecutor = MoreExecutors.listeningDecorator(jdkExecutor);
    }

	/**
	 * 通过service接口创建service代理类, 通过代理类实现远程rpc服务调用
	 * @param serviceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public <T> T createServiceProxy(Class<T> serviceClass) {
	    return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), 
	            new Class[] {serviceClass}, new RpcClientInvoker(serviceClass, this));
	}
	
	public void stop() {
	    nettyClient.close();
	    serviceDiscovery.close();
	}
	
	public NettyClient getNettyClient() {
	    return this.nettyClient;
	}
	
	public ServiceDiscovery getServiceDiscovery() {
	    return this.serviceDiscovery;
	}

	public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

    public ListeningExecutorService getBusinessExecutor() {
        return businessExecutor;
    }

    public void setBusinessExecutor(ListeningExecutorService businessExecutor) {
        this.businessExecutor = businessExecutor;
    }
	
}
