package com.crazypig.rpc.netty.client.stub.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.crazypig.rpc.netty.registry.ServerAddress;

/**
 * 默认的服务地址负载均衡, 轮询
 * @author CrazyPig
 *
 */
public class DefaultServerAddressLoadBalancer implements ServerAddressLoadBalancer {

    private AtomicInteger cnt;
    
    public DefaultServerAddressLoadBalancer() {
        this.cnt = new AtomicInteger(0);
    }
    
    @Override
    public ServerAddress getServerAddress(List<ServerAddress> addressList) {
        int count = this.cnt.getAndIncrement();
        int idx = Math.abs(count % addressList.size());
        return addressList.get(idx);
    }

}
