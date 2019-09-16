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

    public static Key aesKeyFromSeed(byte[] seed){
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(seed);
            kgen.init(256, random);
            return kgen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }
    public static byte[] getSalt(){
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(256, new SecureRandom());
            return kgen.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }
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
