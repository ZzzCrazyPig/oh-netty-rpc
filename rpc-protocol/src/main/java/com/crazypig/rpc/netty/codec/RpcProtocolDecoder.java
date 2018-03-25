package com.crazypig.rpc.netty.codec;

import com.crazypig.rpc.netty.serialize.RpcSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * RPC协议解码器
 * @author CrazyPig
 *
 */
public class RpcProtocolDecoder extends LengthFieldBasedFrameDecoder {
    
    private static final int MAX_FRAME_LENGTH = 16 * 1024 * 1024;
    
    private Class<?> targetClass;
    private RpcSerializer rpcSerializer;
    
    public RpcProtocolDecoder(Class<?> targetClass, RpcSerializer rpcSerializer) {
        super(MAX_FRAME_LENGTH, 0, 4);
        this.targetClass = targetClass;
        this.rpcSerializer = rpcSerializer;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        int dataLength = frame.readInt();
        byte[] data = new byte[dataLength];
        frame.getBytes(frame.readerIndex(), data);
        return rpcSerializer.deserialize(data, targetClass);
    }
    
}
