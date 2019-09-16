package com.zz.notebook.ciper;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static com.zz.notebook.ciper.CipherService.getRandomBytes;
import static com.zz.notebook.ciper.CipherService.hash;
import static com.zz.notebook.util.ByteArrayUtils.concat;
import static org.junit.Assert.*;

public class PasswordPropertyTest {

    @Test
    public void set() {
        String pass="abcasdlfkjsadf;ljasdfjklas";
        CipherProvider cipherProvider=new CipherProvider("asdf".getBytes(),"fasd".getBytes());
        PasswordProperty password=new PasswordProperty(pass,cipherProvider);
        Assert.assertEquals(pass,password.get(cipherProvider));
    }
}