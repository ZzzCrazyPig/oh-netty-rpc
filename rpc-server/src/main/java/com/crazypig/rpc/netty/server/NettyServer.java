package com.crazypig.rpc.netty.server;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 
 * Netty实现的server,接受rpc请求,通过反射调用并返回相应结果
 * 
 * @author CrazyPig
 *
 */
public class NettyServer {
    
    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    private String host;
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    
    private NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public static NettyServer createNettyServer(String host, int port) {
        return new NettyServer(host, port).build();
    }
    
    public NettyServer build() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                        .addLast(new ObjectEncoder())
                        .addLast(new RpcServerHandler());
                    
                }});
        return this;
    }
    
    public void start() {
        serverBootstrap.bind(new InetSocketAddress(host, port));
    }
    
    public void shutdown() {
        logger.info("shutdown netty server");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
    
}
