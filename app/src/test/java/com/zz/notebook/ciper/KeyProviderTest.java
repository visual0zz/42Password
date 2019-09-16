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
    public void testforAccount() {
        UUID uuid1=UUID.randomUUID();
        UUID uuid2=new UUID(uuid1.getMostSignificantBits(),uuid1.getLeastSignificantBits());
        System.out.println(uuid1);
        System.out.println(uuid2);
        KeyProvider provider=new KeyProvider("sdaf".getBytes(),"asfdsadf".getBytes());
        String a=bytesToHex(provider.forAccount(uuid1).getEncoded());
        String b=bytesToHex(provider.forAccount(uuid2).getEncoded());
        System.out.println(a);
        System.out.println(b);
        Assert.assertEquals(a,b);
        Assert.assertNotEquals(a,bytesToHex(provider.forAccount(UUID.randomUUID()).getEncoded()));
    }
    @Test
    public void testIvParameterSpec(){

        UUID uuid1=UUID.randomUUID();
        UUID uuid2=new UUID(uuid1.getMostSignificantBits(),uuid1.getLeastSignificantBits());
        System.out.println(uuid1);
        System.out.println(uuid2);
        KeyProvider provider=new KeyProvider("sdaf".getBytes(),"asfdsadf".getBytes());
        String a=bytesToHex(provider.getIv(uuid1).getIV());
        String b=bytesToHex(provider.getIv(uuid2).getIV());
        System.out.println(a);
        System.out.println(b);
        Assert.assertEquals(a,b);
        Assert.assertNotEquals(a,bytesToHex(provider.getIv(UUID.randomUUID()).getIV()));
    }
}