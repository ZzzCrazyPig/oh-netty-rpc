package com.crazypig.rpc.netty.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.EnsurePath;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * zk实现的服务注册
 * @author CrazyPig
 *
 */
public class ZkServiceRegistry implements ServiceRegistry {

    private static Logger logger = LoggerFactory.getLogger(ZkServiceRegistry.class);
    
    private String zkConnectString;
    private String basePath;
    private CuratorFramework zkClient = null;
    
    public ZkServiceRegistry() {}
    
    public ZkServiceRegistry(String zkConnectString, String basePath) {
        this.zkConnectString = zkConnectString;
        this.basePath = basePath;
    }
    
    @Override
    public void register(ServerAddress serverAddress) throws Exception {
        // 将地址注册到zk上
        logger.info("will register service address {}:{} to zookeeper path {}", 
                serverAddress.getHost(), serverAddress.getPort(), basePath);
        Stat stat = zkClient.checkExists().forPath(getPathOf(serverAddress));
        if (stat != null) {
            zkClient.delete().forPath(getPathOf(serverAddress));
        }
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(getPathOf(serverAddress));
    }

    @Override
    public void start() throws Exception {
    	
    	if (Strings.isNullOrEmpty(zkConnectString)) {
    		throw new IllegalArgumentException("connectString is null or empty");
    	}
    	
    	if (Strings.isNullOrEmpty(basePath)) {
    		throw new IllegalArgumentException("basePath is null or empty");
    	}
    	
        zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zkConnectString)
                    .retryPolicy(new RetryNTimes(3, 1000))
                    .connectionTimeoutMs(5000)
                    .build();
        zkClient.start();
        // 确保basePath存在
        try {
            EnsurePath ensurePath = new EnsurePath(basePath);
            ensurePath.ensure(zkClient.getZookeeperClient());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (zkClient != null) {
            zkClient.close();
        }
    }
    

    private String getPathOf(ServerAddress serverAddress) {
        return basePath + "/" + serverAddress.getHost() + "_" + serverAddress.getPort();
    }

	public String getZkConnectString() {
		return zkConnectString;
	}

	public void setZkConnectString(String zkConnectString) {
		this.zkConnectString = zkConnectString;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
    
}
