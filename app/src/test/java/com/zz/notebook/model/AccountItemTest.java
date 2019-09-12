package com.zz.notebook.model;

import com.zz.notebook.ciper.KeyProvider;
import com.zz.notebook.util.ByteArrayUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.util.UUID;

import static org.junit.Assert.*;

public class AccountItemTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void setAndDecryptData() throws ClassNotFoundException, InvalidKeyException {
//        AccountItem accountItem1=new AccountItem();
//        KeyProvider provider=new KeyProvider("123".getBytes(),"4564".getBytes());
//        byte[] encryptedData=accountItem1.getEncryptedData(provider);
//        System.out.println("加密的结果="+ByteArrayUtils.bytesToString(encryptedData));
//
//        AccountItem accountItem2=new AccountItem();
//        accountItem2.setAndDecryptData(accountItem1.uid,provider,encryptedData);
//        System.out.println(accountItem2.getNotes());
    }

    @Test
    public void getEncryptedData() {
    }
}