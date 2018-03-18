package com.crazypig.rpc.netty.registry;

import java.io.Closeable;

/**
 * 服务注册接口
 * @author CrazyPig
 *
 */
public interface ServiceRegistry extends Closeable {
    
    public void start() throws Exception;
    
    public void register(ServerAddress serverAddress) throws Exception;
    
    public void close();
    
}
