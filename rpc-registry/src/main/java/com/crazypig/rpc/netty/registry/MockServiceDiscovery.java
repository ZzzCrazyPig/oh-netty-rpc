package com.crazypig.rpc.netty.registry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author CrazyPig
 *
 */
public class MockServiceDiscovery implements ServiceDiscovery {

    private static Logger logger = LoggerFactory.getLogger(MockServiceDiscovery.class);
    private List<ServerAddress> addressList;
    
    public MockServiceDiscovery(List<ServerAddress> addressList) {
        this.addressList = addressList;
    }
    
    @Override
    public void start() {
        logger.info("mock service discovery start");
    }

    @Override
    public List<ServerAddress> getAllServerAddress() {
        return addressList;
    }

    @Override
    public void subscribe() {
        logger.info("mock service discovery subscribe");
    }

    @Override
    public void close() {
        logger.info("mock service discovery close");
    }

}
