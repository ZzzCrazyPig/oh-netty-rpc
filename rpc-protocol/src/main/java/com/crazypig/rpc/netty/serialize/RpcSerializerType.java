package com.crazypig.rpc.netty.serialize;

/**
 * 定义支持的RPC序列化方式类型
 * @author CrazyPig
 *
 */
public enum RpcSerializerType {
    
    KRYO("kryo"),
    PROTOSTUFF("protostuff"),
    JDK("jdk");
    
    private String name;
    
    private RpcSerializerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    
}
