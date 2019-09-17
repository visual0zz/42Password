package com.zz.notebook.ciper;

import org.junit.Assert;
import org.junit.Test;

public class PasswordPropertyTest {

    @Test
    public void set() {
        String pass="abcasdlfkjsadf;ljasdfjklas";
        CipherProvider cipherProvider=new CipherProvider("asdf".getBytes(),"fasd".getBytes());
        PasswordProperty password=new PasswordProperty(pass,cipherProvider);
        Assert.assertEquals(pass,password.get(cipherProvider));
    }
}