package com.crazypig.rpc.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 
 * Netty client实现
 * @author CrazyPig
 *
 */
public final class NettyClient {
    
    private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
    
    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private NettyClient _this;
    
    private ConcurrentMap<String, ConcurrentLinkedQueue<RpcConnection>> rpcConnPool;
    
    private NettyClient() {
        this.rpcConnPool = new ConcurrentHashMap<String, ConcurrentLinkedQueue<RpcConnection>>();
        _this = this;
    }
    
    public static NettyClient createNettyClient() {
        return new NettyClient().build();
    }
    
    private NettyClient build() {
    	// TODO config ???
        eventLoopGroup = new NioEventLoopGroup(2);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                        .addLast(new ObjectEncoder())
                        .addLast(new RpcClientHandler());
                }});
        return this;
    }
    
    public RpcConnection getRpcConnection(String host, int port) throws InterruptedException {
        String key = keyOf(host, port);
        ConcurrentLinkedQueue<RpcConnection> queue = rpcConnPool.get(key);
        if (queue == null) {
            ConcurrentLinkedQueue<RpcConnection> newQueue = new ConcurrentLinkedQueue<RpcConnection>();
            newQueue = rpcConnPool.putIfAbsent(key, newQueue);
            return newRpcConnection(host, port);
        }
        RpcConnection rpcConn = queue.poll();
        if (rpcConn == null) {
            return newRpcConnection(host, port);
        }
        return rpcConn;
    }
    
    /**
     * 创建到rpc server的连接
     * @param host
     * @param port
     * @return
     * @throws InterruptedException
     */
    private RpcConnection newRpcConnection(String host, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port)).sync();
        return new RpcConnection(host, port, channelFuture.channel(), _this);
    }
    
    public void close() {
        logger.info("start release rpc connection pool and event loop group ...");
        if (rpcConnPool.size() > 0) {
            for (String key : rpcConnPool.keySet()) {
                ConcurrentLinkedQueue<RpcConnection> queue = rpcConnPool.get(key);
                RpcConnection rpcConn = null;
                while ((rpcConn = queue.poll()) != null) {
                    rpcConn.close(true);
                }
            }
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
        logger.info("end release rpc connection pool and event loop group!");
    }
    
    public void recycle(RpcConnection rpcConn) {
        rpcConnPool.get(keyOf(rpcConn.getHost(), rpcConn.getPort())).offer(rpcConn);
    }
    
    private String keyOf(String host, int port) {
        return host + ":" + port;
    }
    
}
