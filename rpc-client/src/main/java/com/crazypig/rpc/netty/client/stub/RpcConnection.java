package com.crazypig.rpc.netty.client.stub;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.crazypig.rpc.netty.client.stub.async.RpcResponseFuture;
import com.crazypig.rpc.netty.protocol.RpcRequest;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

/**
 * 代表RPC client到RPC server的连接, 用于发送RPC请求
 * @author CrazyPig
 *
 */
public final class RpcConnection implements Closeable {

	public static final AttributeKey<RpcConnection> ATTR_KEY = AttributeKey.valueOf("rpcConnection");
	
    private String host;
    private int port;
    private Channel toServerChannel;
    private NettyClient nettyClient;
    /**
     * 保存request id与 response future的map, 解决netty异步调用识别哪个请求通知哪些client的问题
     * Tips: rpc约定请求带requestid且返回的response也必须带回requestid
     */
    private ConcurrentMap<String, RpcResponseFuture> rpcRespFutureMap;
    
    RpcConnection(String host, int port, Channel channel, NettyClient nettyClient) {
        this.host = host;
        this.port = port;
        this.nettyClient = nettyClient;
        this.rpcRespFutureMap = new ConcurrentHashMap<String, RpcResponseFuture>();
        this.toServerChannel = channel;
        this.toServerChannel.attr(ATTR_KEY).set(this);
    }
    
    /**
     * 发送RPC请求, 返回异步future
     * @param {@link RpcRequest}
     * @return {@link RpcResponseFuture}
     */
    public RpcResponseFuture sendRpcRequest(final RpcRequest request) {
        RpcResponseFuture rpcRespFuture = new RpcResponseFuture(this);
        rpcRespFutureMap.put(request.getRequestId(), rpcRespFuture);
        toServerChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
            	// 处理消息发送失败的情况
                if (future.isCancelled() || !future.isSuccess()) {
                	rpcRespFutureMap.get(request.getRequestId()).cancel(false);
                }
            }
        });
        return rpcRespFuture;
    }
    
    /**
     * RpcConnection close调用, 回收到连接池里
     */
    @Override
    public void close() {
        close(false);
    }
    
    public void close(boolean realClose) {
        if (realClose) {
            realClose();
        } else {
            recycle();
        }
    }
    
    public void realClose() {
        if (toServerChannel != null && toServerChannel.isOpen()) {
            try {
                toServerChannel.close().sync();
            } catch (InterruptedException e) {
                //
            }
        }
    }
    
    public void recycle() {
        if (toServerChannel != null && toServerChannel.isOpen()) {
            nettyClient.recycle(this);
        }
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
	public ConcurrentMap<String, RpcResponseFuture> getRpcRespFutureMap() {
		return rpcRespFutureMap;
	}

}
