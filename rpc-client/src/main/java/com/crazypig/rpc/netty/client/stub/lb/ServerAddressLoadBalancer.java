package com.crazypig.rpc.netty.client.stub.lb;

import java.util.List;

import com.crazypig.rpc.netty.registry.ServerAddress;

/**
 * 
 * @author CrazyPig
 *
 */
public interface ServerAddressLoadBalancer {
    
    public ServerAddress getServerAddress(List<ServerAddress> addressList);

}
