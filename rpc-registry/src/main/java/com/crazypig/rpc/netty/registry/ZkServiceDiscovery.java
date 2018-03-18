package com.crazypig.rpc.netty.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * zk 实现的服务发现
 * @author CrazyPig
 *
 */
public class ZkServiceDiscovery implements ServiceDiscovery {
    
    private static Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);
    
    private volatile List<ServerAddress> addressList; 
    
    private String zkConnectString;
    private String basePath;
    private CuratorFramework zkClient;
    private PathChildrenCache watcher;
    private ExecutorService executor;
    
    public ZkServiceDiscovery() {}
    
    public ZkServiceDiscovery(String zkConnectString, String basePath) {
        this.addressList = new ArrayList<ServerAddress>();
        this.zkConnectString = zkConnectString;
        this.basePath = basePath;
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    @Override
    public List<ServerAddress> getAllServerAddress() {
        return addressList;
    }

    @Override
    public void start() {
    	
    	if (Strings.isNullOrEmpty(zkConnectString)) {
    		throw new IllegalArgumentException("zkConnectString is null");
    	}
    	
    	if (Strings.isNullOrEmpty(basePath)) {
    		throw new IllegalArgumentException("basePath is null");
    	}
    	
        zkClient = CuratorFrameworkFactory.builder()
                    .connectString(zkConnectString)
                    .retryPolicy(new RetryNTimes(3, 3000))
                    .connectionTimeoutMs(5000)
                    .sessionTimeoutMs(30000)
                    .build();
        zkClient.start();
        logger.info("zk discovery started!");
    }

    @Override
    public void close() {
        if (watcher != null) {
            try {
                watcher.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (zkClient != null) {
            zkClient.close();
        }
        logger.info("zk discovery closed!");
    }

    @Override
    public void subscribe() {
        // 首次加载server address
        logger.info("start to load server address list");
        loadServerAddressList();
        // 注册监听
        watcher = new PathChildrenCache(zkClient, basePath, false);
        watcher.getListenable().addListener(new PathChildrenCacheListener() {
            
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType() == Type.CHILD_ADDED 
                        || event.getType() == Type.CHILD_REMOVED) {
                    logger.info("on server address add or remove, will reload again");
                    loadServerAddressList();
                }
            }
        }, executor);
    }
    
    private void loadServerAddressList() {
        try {
            List<String> subPathList = zkClient.getChildren().forPath(basePath);
            addressList = formServerAddressList(subPathList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private List<ServerAddress> formServerAddressList(List<String> subPathList) {
        List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
        for (String subPath : subPathList) {
            String[] arr = subPath.split("_", 2);
            ServerAddress address = new ServerAddress(arr[0], Integer.parseInt(arr[1]));
            serverAddressList.add(address);
        }
        return serverAddressList;
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
