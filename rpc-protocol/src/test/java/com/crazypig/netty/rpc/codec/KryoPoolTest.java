package com.crazypig.netty.rpc.codec;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

public class KryoPoolTest {
    
    private KryoPool kryoPool;
    private KryoFactory kryoFactory;
    
    @Before
    public void init() {
        kryoFactory = new KryoFactory() {
            
            @Override
            public Kryo create() {
                return new Kryo();
            }
        };
        kryoPool = new KryoPool.Builder(kryoFactory).build();
    }
    
    @Test
    public void testBorrowAndRelease() {
        Kryo kryo = kryoPool.borrow();
        assertTrue(kryo != null);
        kryoPool.release(kryo);
    }
    
    @After
    public void destory() {
        kryoFactory = null;
        kryoPool = null;
    }

}
