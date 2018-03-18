package com.crazypig.rpc.netty.client;

import java.lang.reflect.Proxy;

import com.crazypig.rpc.netty.registry.ServiceDiscovery;

public class RpcClient {
    
    private NettyClient nettyClient;
    private ServiceDiscovery serviceDiscovery;
    
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
	
}
