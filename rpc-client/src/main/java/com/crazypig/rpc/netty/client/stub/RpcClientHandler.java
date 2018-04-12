package com.crazypig.rpc.netty.client.stub;

import com.crazypig.rpc.netty.client.stub.async.RpcResponseFuture;
import com.crazypig.rpc.netty.protocol.RpcResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * netty client 自定义handler 处理rpc请求响应
 * @author CrazyPig
 *
 */
@Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // rpc服务端返回的结果, 设置future done
    	Channel channel = ctx.channel();
    	RpcConnection rpcConnection = channel.attr(RpcConnection.ATTR_KEY).get();
        RpcResponseFuture rpcRespFuture = rpcConnection.getRpcRespFutureMap().get(response.getRequestId());
        rpcRespFuture.setDone(response);
    }
    
}
