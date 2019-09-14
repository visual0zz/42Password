package com.zz.notebook.data;

import com.zz.notebook.ciper.AccountItem;
import com.zz.notebook.ciper.KeyProvider;

import org.junit.Assert;
import org.junit.Test;

import java.security.InvalidKeyException;

import static com.zz.notebook.util.ByteArrayUtils.bytesToUUID;
import static com.zz.notebook.util.ByteArrayUtils.uuidToBytes;

public class AccountItemTest {
    @Test
    public void setAndDecryptData() throws ClassNotFoundException, InvalidKeyException {
        String note="123asdf!@#";
        AccountItem accountItem1=new AccountItem();
        accountItem1.setNotes(note);
        KeyProvider provider=new KeyProvider("123".getBytes(),"4564".getBytes());
        byte[] encryptedData=accountItem1.getEncryptedData(provider);
        //System.out.println("加密的结果="+ByteArrayUtils.bytesToHex(encryptedData));

        AccountItem accountItem2=new AccountItem();
        accountItem2.setAndDecryptData(bytesToUUID(uuidToBytes(accountItem1.getUid())),provider,encryptedData);
        //System.out.println(accountItem2.getNotes());
        Assert.assertEquals(note,accountItem2.getNotes());
        Assert.assertEquals(accountItem1.getNotes(),accountItem2.getNotes());
        Assert.assertEquals(accountItem1.getGroup(),accountItem2.getGroup());
        Assert.assertEquals(accountItem1.getUid(),accountItem2.getUid());
        Assert.assertEquals(accountItem2.getNotes(),"123asdf!@#");
    }
}