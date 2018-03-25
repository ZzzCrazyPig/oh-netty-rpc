package com.crazypig.netty.rpc.codec;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.crazypig.rpc.netty.protocol.RpcRequest;
import com.crazypig.rpc.netty.protocol.RpcRequestBuilder;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTest {
    
    private Kryo kryo;
    private Output output;
    private Input input;
    private RpcRequest rpcRequest;
    
    @Before
    public void init() {
        kryo = new Kryo();
        rpcRequest = RpcRequestBuilder
                        .newBuilder()
                        .setRequestId("abc")
                        .setServiceName("helloService")
                        .setMethodName("hello")
                        .setParamTypes(new String[] {"java.lang.String"})
                        .setParamValues(new Object[] {"CrazyPig"})
                        .build();
        output = new Output(256);
        input = new Input(output.getBuffer());
    }
    
    @Test
    public void testEncodeAndDecode() {
        
        // 序列化
        kryo.writeObjectOrNull(output, rpcRequest, RpcRequest.class);
        // 反序列化
        RpcRequest _rpcRequest = kryo.readObjectOrNull(input, RpcRequest.class);
        assertEquals("abc", _rpcRequest.getRequestId());
        assertEquals("helloService", _rpcRequest.getServiceName());
        assertEquals("hello", _rpcRequest.getMethodName());
        assertArrayEquals(new String[] {"java.lang.String"}, _rpcRequest.getParamTypes());
        assertArrayEquals(new Object[] {"CrazyPig"}, _rpcRequest.getParamValues());
        
    }
    
    @After
    public void destory() {
        kryo = null;
        if (output != null) {
            output.close();
        }
    }

}
