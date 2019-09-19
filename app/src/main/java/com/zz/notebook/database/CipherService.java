package com.zz.notebook.database;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static com.zz.notebook.database.ByteArrayUtils.bytesToHex;
import static com.zz.notebook.database.ByteArrayUtils.concat;
import static com.zz.notebook.database.ByteArrayUtils.int2byte;

public class CipherService {
    public static Logger logger=Logger.getLogger(CipherService.class.getName());

    public static final int aes_key_length=32;//key的实际长度
    public static Key aesKeyFromSeed(byte[] seed){
        KeyGenerator kgen = null;

        byte[] key_data=hash(new byte[]{1,2,3,4,},seed,normal_key_hash_count);
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
        //logger.info("从seed="+bytesToHex(seed)+"产生了key="+bytesToHex(result.getEncoded()));
        return result;

    }

    public static IvParameterSpec getIvFromSeed(byte[] seed){
        byte[] tmp=hash(new byte[]{4,7,8,1},seed,normal_key_hash_count);
        byte[] result=new byte[16];
        for(int i=0;i<16;i++){
            result[i]= (byte) (tmp[2*i]^tmp[2*i+1]);
        }
        //logger.info("从seed="+bytesToHex(seed)+"产生了IV="+bytesToHex(result));
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




    /**
     * 求哈希
     * @param salt 盐
     * @param data 要求哈希的数据
     * @param count 要进行的迭代次数
     * @return
     */
    public static final int hash_length=32;
    public static final int main_key_hash_count=10000;//主密钥使用的迭代次数
    public static final int normal_key_hash_count=100;//普通密钥使用的迭代次数
    public static byte[] hash(byte[] salt,byte[] data,int count){
        byte[]tmp;
        try {
            for(int i=0;i<count;i++){
                MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
                messageDigest.update(concat(salt,concat(int2byte(0x13758496+i),data)));
                tmp=salt;
                salt=messageDigest.digest();
                data=tmp;
            }
            return data;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试使用不存在的算法");
        }
    }
}
