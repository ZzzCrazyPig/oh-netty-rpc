package com.crazypig.rpc.netty.registry;

import java.io.Closeable;
import java.util.List;

/**
 * 服务发现接口
 * @author CrazyPig
 *
 */
public interface ServiceDiscovery extends Closeable {
    
    public void start();
    
    public List<ServerAddress> getAllServerAddress();

    public void subscribe();
    
    public void close();
    
}
