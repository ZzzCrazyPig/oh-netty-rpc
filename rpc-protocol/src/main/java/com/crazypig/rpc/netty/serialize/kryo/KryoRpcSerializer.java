package com.crazypig.rpc.netty.serialize.kryo;

import java.io.IOException;

import com.crazypig.rpc.netty.serialize.RpcSerializer;
import com.crazypig.rpc.netty.serialize.RpcSerializerType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * kryo实现的RPC序列化实现
 * @author CrazyPig
 *
 */
public class KryoRpcSerializer implements RpcSerializer {
    
    private static final KryoFactory DEFAULT_KRYO_FACTORY = new KryoFactory() {
        
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            return kryo;
        }
    };
    
    private static final KryoPool DEFAULT_KRYO_POOL = new KryoPool.Builder(DEFAULT_KRYO_FACTORY).build();
    
    private static final int DEFAULT_BUFFER_SIZE = 256;
    
    private KryoPool kryoPool;
    
    public KryoRpcSerializer() {
        this.kryoPool = DEFAULT_KRYO_POOL;
    }
    
    public KryoRpcSerializer(KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }
    
    @Override
    public byte[] serialize(Object value) throws IOException {
        Kryo kryo = kryoPool.borrow();
        Output output = new Output(DEFAULT_BUFFER_SIZE);
        try {
            kryo.writeObject(output, value);
        } finally {
            kryoPool.release(kryo);
        }
        return output.getBuffer();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> objClass) throws IOException {
        Kryo kryo = kryoPool.borrow();
        T result = null;
        try {
            result = kryo.readObject(new Input(data), objClass);
        } finally {
            kryoPool.release(kryo);
        }
        return result;
    }

    @Override
    public RpcSerializerType getSerializerType() {
        return RpcSerializerType.KRYO;
    }

}
