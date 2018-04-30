package com.crazypig.rpc.netty.client.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // rpc服务端返回的结果, 设置future done
    	Channel channel = ctx.channel();
    	RpcConnection rpcConnection = channel.attr(RpcConnection.ATTR_KEY).get();
    	rpcConnection.onRpcRequestDone(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        Channel ch = ctx.channel();
        RpcConnection rpcConn = ch.attr(RpcConnection.ATTR_KEY).get();
        if (rpcConn != null) {
            rpcConn.close(true);
        } else {
            logger.warn("no rpc connection bind!");
        }
    }
    
}
