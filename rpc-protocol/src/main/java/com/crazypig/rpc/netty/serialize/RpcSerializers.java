package com.crazypig.rpc.netty.serialize;

import com.crazypig.rpc.netty.serialize.kryo.KryoRpcSerializer;
import com.crazypig.rpc.netty.serialize.protostuff.ProtostuffRpcSerializer;

/**
 * 
 * @author CrazyPig
 *
 */
public class RpcSerializers {
    
    public static RpcSerializer kryoRpcSerializer = new KryoRpcSerializer();
    
    public static RpcSerializer protosutffRpcSerializer = new ProtostuffRpcSerializer();
    
}
