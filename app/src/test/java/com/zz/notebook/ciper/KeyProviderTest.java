package com.zz.notebook.ciper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.Key;
import java.util.UUID;

import static com.zz.notebook.util.ByteArrayUtils.bytesToString;
import static org.junit.Assert.*;

public class KeyProviderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void forAccount() {
        KeyProvider provider=new KeyProvider("123".getBytes(),"456".getBytes());
        UUID uid=UUID.randomUUID();
        Key a=provider.forAccount(uid);
        Key b=provider.forAccount(UUID.fromString(uid.toString()));
        System.out.println(bytesToString(a.getEncoded())+"=="+bytesToString(b.getEncoded()));
    }
}