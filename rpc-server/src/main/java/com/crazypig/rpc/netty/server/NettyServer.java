package com.crazypig.rpc.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crazypig.rpc.netty.codec.RpcProtocolDecoder;
import com.crazypig.rpc.netty.codec.RpcProtocolEncoder;
import com.crazypig.rpc.netty.protocol.RpcRequest;
import com.crazypig.rpc.netty.serialize.RpcSerializers;
import com.crazypig.rpc.netty.serialize.RpcSerializerType;
import com.crazypig.rpc.netty.serialize.kryo.KryoRpcSerializer;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.AttributeKey;

/**
 * 
 * Netty实现的server,接受rpc请求,通过反射调用并返回相应结果
 * 
 * @author CrazyPig
 *
 */
public class NettyServer {
    
    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    public static final AttributeKey<ListeningExecutorService> ATTR_BUSINESS_EXECUTOR = AttributeKey.valueOf("businessExecutor");
    private static final RpcSerializerType DEFAULT_SERIALIZER_TYPE = RpcSerializerType.KRYO;
    private static final int DEFAULT_BUSINESS_EXECUTOR_SIZE = 4;
    
    private String host;
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    
    private int businessExecutorSize = DEFAULT_BUSINESS_EXECUTOR_SIZE;
    private ListeningExecutorService guavaExecutorService;
    
    // TODO 可配置
    private RpcSerializerType serializerType = DEFAULT_SERIALIZER_TYPE;
    
    private NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public static NettyServer createNettyServer(String host, int port) {
        return new NettyServer(host, port).build();
    }
    
    public NettyServer build() {
        
        buildBusinessExecutor();
        
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    
                    doInitChannel(ch);
                    
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
    
    /**
     * 初始化channel, 根据serializerType选择相应的序列化encoder和decoder
     * @param ch
     * @throws Exception
     */
    private void doInitChannel(Channel ch) throws Exception {
        
        // 设置业务线程池
        ch.attr(ATTR_BUSINESS_EXECUTOR).set(guavaExecutorService);
        
        switch (serializerType) {
            case KRYO:
                ch.pipeline()
                        .addLast(new RpcProtocolDecoder(RpcRequest.class, RpcSerializers.kryoRpcSerializer))
                        .addLast(new RpcProtocolEncoder(RpcSerializers.kryoRpcSerializer))
                        .addLast(new RpcServerHandler());
                break;
            case JDK:
                ch.pipeline()
                        .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                        .addLast(new ObjectEncoder())
                        .addLast(new RpcServerHandler());
                break;
            case PROTOSTUFF:
                ch.pipeline()
                        .addLast(new RpcProtocolDecoder(RpcRequest.class, RpcSerializers.protosutffRpcSerializer))
                        .addLast(new RpcProtocolEncoder(RpcSerializers.protosutffRpcSerializer))
                        .addLast(new RpcServerHandler());
                break;
            // TODO 更多序列化实现
            default:
                // 默认为kryo序列化方案
                ch.pipeline()
                        .addLast(new RpcProtocolDecoder(RpcRequest.class, new KryoRpcSerializer()))
                        .addLast(new RpcProtocolEncoder(new KryoRpcSerializer()))
                        .addLast(new RpcServerHandler());

        }
    }
    
    private void buildBusinessExecutor() {
        ExecutorService jdkExecutor = new ThreadPoolExecutor(businessExecutorSize, businessExecutorSize, 
                0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(4096), new ThreadPoolExecutor.CallerRunsPolicy());
        guavaExecutorService = MoreExecutors.listeningDecorator(jdkExecutor);
    }
    
}
