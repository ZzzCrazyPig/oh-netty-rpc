package com.crazypig.rpc.netty.serialize;

import java.io.IOException;

/**
 * 抽象RPC序列化方法
 * @author CrazyPig
 *
 */
public interface RpcSerializer {
    
    public RpcSerializerType getSerializerType();
    
    /**
     * 序列化
     * @param value
     * @return
     * @throws IOException
     */
    public byte[] serialize(Object value) throws IOException;
    
    /**
     * 反序列化
     * @param data
     * @param objClass
     * @return
     * @throws IOException
     */
    public <T> T deserialize(byte[] data, Class<T> objClass) throws IOException;

}
