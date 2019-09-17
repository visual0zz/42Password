package com.zz.notebook.ciper;

import android.provider.Settings;

import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static com.zz.notebook.ciper.ByteArrayUtils.bytesToHex;
import static com.zz.notebook.ciper.ByteArrayUtils.concat;
import static com.zz.notebook.ciper.ByteArrayUtils.int2byte;

public class CipherService {
    public static Logger logger=Logger.getLogger(CipherService.class.getName());

    public static final int aes_key_length=32;//key的实际长度
    public static Key aesKeyFromSeed(byte[] seed){
        KeyGenerator kgen = null;

        byte[] key_data=hash(new byte[]{1,2,3,4,},seed);
        SecretKey result= new SecretKey() {
            @Override
            public String getAlgorithm() {
                return "AES";
            }

            @Override
            public String getFormat() {
                return "RAW";
            }

            @Override
            public byte[] getEncoded() {
                return key_data;
            }
        };
        logger.info("从seed="+bytesToHex(seed)+"产生了key="+bytesToHex(result.getEncoded()));
        return result;

    }

    public static IvParameterSpec getIvFromSeed(byte[] seed){
        byte[] tmp=hash(new byte[]{4,7,8,1},seed);
        byte[] result=new byte[16];
        for(int i=0;i<16;i++){
            result[i]= (byte) (tmp[2*i]^tmp[2*i+1]);
        }
        logger.info("从seed="+bytesToHex(seed)+"产生了IV="+bytesToHex(result));
        return new IvParameterSpec(result);
    }

    public static final int random_bytes_length=32;
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



    public static final int hash_length=32;
    public static byte[] hash(byte[] a,byte[] b){
        try {
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(concat(int2byte(0x13758496),concat(a,b)));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }
}
