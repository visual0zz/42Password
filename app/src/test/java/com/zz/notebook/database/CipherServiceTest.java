package com.zz.notebook.database;

import org.junit.Assert;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.zz.notebook.database.CipherService.aesKeyFromSeed;
import static com.zz.notebook.database.CipherService.getRandomBytes;
import static com.zz.notebook.database.CipherService.hash;
import static com.zz.notebook.database.ByteArrayUtils.bytesToHex;
import static com.zz.notebook.database.ByteArrayUtils.isEqual;
import static com.zz.notebook.database.CipherService.main_key_hash_count;
import static com.zz.notebook.database.CipherService.normal_key_hash_count;

public class CipherServiceTest {
    @Test
    public void testSecureRandom() throws NoSuchAlgorithmException {
        byte[]seed= getRandomBytes();
        SecureRandom random1=SecureRandom.getInstance("SHA1PRNG");
        SecureRandom random2=SecureRandom.getInstance("SHA1PRNG");
        random1.setSeed(seed);
        random2.setSeed(seed);
        byte[] a,b;a=new byte[10];b=new byte[10];
        random1.nextBytes(a);
        random2.nextBytes(b);
        String hex1=bytesToHex(a);
        String hex2=bytesToHex(b);
        Assert.assertEquals(hex1,hex2);
    }

    @Test
    public void testaesKeyFromSeed() {
        byte[] seed1= getRandomBytes();
        byte[] seed2= getRandomBytes();
        byte[] key1=aesKeyFromSeed(seed1).getEncoded();
        byte[] key2=aesKeyFromSeed(seed1).getEncoded();
        byte[] key3=aesKeyFromSeed(seed2).getEncoded();
        Assert.assertTrue(isEqual(key1,key2));
        Assert.assertFalse(isEqual(key2,key3));
    }

    @Test
    public void testgetSalt() {
        byte[] salt1= getRandomBytes();
        byte[] salt2= getRandomBytes();
        Assert.assertFalse(isEqual(salt1,salt2));
    }

    @Test
    public void testhash() {
        byte[] salt1= getRandomBytes();
        byte[] salt2= getRandomBytes();

        byte[] hash1=hash(salt1,salt1,3);
        byte[] hash2=hash(salt1,salt2,3);
        byte[] hash3=hash(salt1,salt2,3);

        Assert.assertTrue(isEqual(hash2,hash3));
        Assert.assertFalse(isEqual(hash1,hash2));
    }

    @Test
    public void testHashTime(){//测量加密的时间，以确定常量的值
        long pre=System.currentTimeMillis();
        CipherService.hash(getRandomBytes(),getRandomBytes(),main_key_hash_count);
        long after=System.currentTimeMillis();
        long between=after-pre;
        System.out.println("主密钥加密时间为:"+between+"ms");

        pre=System.currentTimeMillis();
        CipherService.hash(getRandomBytes(),getRandomBytes(),normal_key_hash_count);
        after=System.currentTimeMillis();
        between=after-pre;
        System.out.println("普通密钥加密时间为:"+between+"ms");

    }
}