package com.crazypig.rpc.netty.serialize.protostuff;

import java.io.IOException;

import com.crazypig.rpc.netty.serialize.RpcSerializer;
import com.crazypig.rpc.netty.serialize.RpcSerializerType;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * protostuff RPC序列化实现
 * @author CrazyPig
 *
 */
public class ProtostuffRpcSerializer implements RpcSerializer {

    @Override
    public RpcSerializerType getSerializerType() {
        return RpcSerializerType.PROTOSTUFF;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public byte[] serialize(Object value) throws IOException {
        Schema schema = RuntimeSchema.getSchema(value.getClass());
        return ProtostuffIOUtil.toByteArray(value, schema, LinkedBuffer.allocate(512));
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> objClass) throws IOException {
        T obj = null;
        try {
            obj = objClass.newInstance();
            Schema<T> schema = RuntimeSchema.getSchema(objClass);
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IOException(e);
        }
        return obj;
    }

}
