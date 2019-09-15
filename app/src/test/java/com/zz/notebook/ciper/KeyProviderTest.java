package com.zz.notebook.ciper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.zz.notebook.util.ByteArrayUtils.bytesToHex;

public class KeyProviderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void forAccount() {
        UUID a=UUID.randomUUID();
        KeyProvider provider=new KeyProvider("sdaf".getBytes(),"asfdsadf".getBytes());
        System.out.println(bytesToHex(provider.forAccount(a).getEncoded()));
        System.out.println(bytesToHex(provider.forAccount(a).getEncoded()));
        Assert.assertEquals(provider.forAccount(a),provider.forAccount(a));
        Assert.assertNotEquals(provider.forAccount(a),provider.forAccount(UUID.randomUUID()));
    }
}