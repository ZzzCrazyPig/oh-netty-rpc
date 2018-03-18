package com.crazypig.rpc.netty.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author CrazyPig
 *
 */
public class MockServiceRegistry implements ServiceRegistry {

    private static Logger logger = LoggerFactory.getLogger(MockServiceRegistry.class);
    
    @Override
    public void start() {
        logger.info("mock service registry start");
    }

    @Override
    public void register(ServerAddress serverAddress) throws Exception {
        logger.info("mock service register server {}:{}", serverAddress.getHost(), serverAddress.getPort());
    }

    @Override
    public void close() {
        logger.info("mock service registry close");
    }

}
