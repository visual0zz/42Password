package com.zz.notebook.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.zz.notebook.util.ByteArrayUtils.byte2long;
import static com.zz.notebook.util.ByteArrayUtils.bytesToUUID;
import static com.zz.notebook.util.ByteArrayUtils.long2byte;
import static com.zz.notebook.util.ByteArrayUtils.uuidToBytes;
import static org.junit.Assert.*;

public class ByteArrayUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testByteToLong(){
        long a= (long) (Math.random()*Long.MAX_VALUE);
        long b=byte2long(long2byte(a));
        Assert.assertEquals(a,b);
    }

    @Test
    public void testuuidToBytes() {
        UUID a=UUID.randomUUID();
        UUID b=bytesToUUID(uuidToBytes(a));
        Assert.assertEquals(a,b);
    }

    @Test
    public void testRepeat(){
        for(int i=0;i<1000;i++){
            testByteToLong();
            testuuidToBytes();
        }
    }

}