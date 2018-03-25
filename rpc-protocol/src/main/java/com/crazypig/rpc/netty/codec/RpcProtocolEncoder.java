package com.crazypig.rpc.netty.codec;

import com.crazypig.rpc.netty.serialize.RpcSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
 
/**
 * RPC协议编码器
 * @author CrazyPig
 *
 */
public class RpcProtocolEncoder extends MessageToByteEncoder<Object> {
    
    private RpcSerializer rpcSerializer;
    
    public RpcProtocolEncoder(RpcSerializer rpcSerializer) {
        super();
        this.rpcSerializer = rpcSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = rpcSerializer.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

}
