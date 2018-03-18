package com.crazypig.rpc.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.crazypig.rpc.netty.registry.ServerAddress;
import com.crazypig.rpc.netty.registry.ServiceRegistry;

/**
 * RPC Server, 使用Netty构建服务端, 接收RPC Client请求, 执行具体service请求, 然后将结果序列化写回RPC client
 * @author CrazyPig
 *
 */
public class RpcServer {
    
    private static Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private String host;
    private int port;
    
    private NettyServer nettyServer;
    private ServiceRegistry serviceRegistry;
    
    public RpcServer() {
    }
    
    public RpcServer(String host, int port, ServiceRegistry serviceRegistry) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = serviceRegistry;
    }
    
    public void start() throws Exception {
    	if (serviceRegistry == null) {
    		throw new IllegalArgumentException("serviceRegistry is null");
    	}
    	this.nettyServer = NettyServer.createNettyServer(host, port);
        nettyServer.start();
        serviceRegistry.start();
        serviceRegistry.register(new ServerAddress(host, port));
        logger.info("RpcServer start at {}:{} successfully", host, port);
    }
    
    public void shutdown() {
    	logger.info("rpc server begin to shutdown ...");
        // close service registry
        serviceRegistry.close();
        // stop netty server
        nettyServer.shutdown();
        logger.info("rpc server shutdown successfully!");
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
    
}
