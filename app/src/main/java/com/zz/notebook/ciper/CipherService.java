package com.zz.notebook.ciper;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import static com.zz.notebook.util.ByteArrayUtils.concat;
import static com.zz.notebook.util.ByteArrayUtils.int2byte;
import static java.lang.System.exit;

public class CipherService {

    public static final int aes_key_length=256;
    public static Key aesKeyFromSeed(byte[] seed){
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);
            kgen.init(aes_key_length, random);
            return kgen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }


    public static final int random_bytes_length=256;
    public static byte[] getRandomBytes(){
        try {
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            byte[] result=new byte[random_bytes_length];
            random.nextBytes(result);
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }



    public static final int hash_length=256;
    public static byte[] hash(byte[] in){
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(concat(int2byte(0x13758496),in));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }
}
