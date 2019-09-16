package com.zz.notebook.ciper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import static com.zz.notebook.util.ByteArrayUtils.bytesToHex;

public class CipherProviderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testforAccount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz= CipherProvider.class;
        Method forAccount=clazz.getDeclaredMethod("forAccount", UUID.class);
        forAccount.setAccessible(true);

        UUID uuid1=UUID.randomUUID();
        UUID uuid2=new UUID(uuid1.getMostSignificantBits(),uuid1.getLeastSignificantBits());
        System.out.println(uuid1);
        System.out.println(uuid2);
        CipherProvider provider=new CipherProvider("sdaf".getBytes(),"asfdsadf".getBytes());
        String a=bytesToHex(((Key)forAccount.invoke(provider,uuid1)).getEncoded());
        String b=bytesToHex(((Key)forAccount.invoke(provider,uuid2)).getEncoded());
        System.out.println(a);
        System.out.println(b);
        Assert.assertEquals(a,b);
        Assert.assertNotEquals(a,bytesToHex(((Key)forAccount.invoke(provider,UUID.randomUUID())).getEncoded()));
    }
    @Test
    public void testIvParameterSpec() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class clazz= CipherProvider.class;
        Method getIv=clazz.getDeclaredMethod("getIv", UUID.class);
        getIv.setAccessible(true);

        UUID uuid1=UUID.randomUUID();
        UUID uuid2=new UUID(uuid1.getMostSignificantBits(),uuid1.getLeastSignificantBits());
        System.out.println(uuid1);
        System.out.println(uuid2);
        CipherProvider provider=new CipherProvider("sdaf".getBytes(),"asfdsadf".getBytes());
        String a=bytesToHex(((IvParameterSpec)getIv.invoke(provider,uuid1)).getIV());
        String b=bytesToHex(((IvParameterSpec)getIv.invoke(provider,uuid2)).getIV());
        System.out.println(a);
        System.out.println(b);
        Assert.assertEquals(a,b);
        Assert.assertNotEquals(a,bytesToHex(((IvParameterSpec)getIv.invoke(provider,UUID.randomUUID())).getIV()));
    }
}