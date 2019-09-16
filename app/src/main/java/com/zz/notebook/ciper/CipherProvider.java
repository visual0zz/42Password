package com.zz.notebook.ciper;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import static com.zz.notebook.ciper.CipherService.aesKeyFromSeed;
import static com.zz.notebook.util.BasicService.global_encrypt_algorithm;
import static com.zz.notebook.util.ByteArrayUtils.concat;
import static com.zz.notebook.util.ByteArrayUtils.uuidToBytes;

/**
 * 用于生成不同对象的不同加密密钥的密钥生成器，根据全局密钥设定，生成具体的加密密钥
 */
public class CipherProvider {
    byte[] masterkey_hash;
    byte[] randomkey;
    public CipherProvider(byte[] masterkey_hash, byte[] randomkey){this.randomkey=randomkey;this.masterkey_hash = masterkey_hash;}
    private Key getKeyForAccount(UUID uuid){//对应每一条账户信息的加密密钥
        byte[] uid_bytes=uuidToBytes(uuid);
        byte[] accountkey= concat(concat(randomkey, masterkey_hash),uid_bytes);
        return aesKeyFromSeed(accountkey);
    }
    private IvParameterSpec getIvForAccount(UUID uuid){return getIvFromSeed(uuidToBytes(uuid));}
    private IvParameterSpec getIvFromSeed(byte[] seed){
        try {
            byte[] accountkey= concat(concat(randomkey,seed),masterkey_hash);
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(accountkey);
            byte[] result=new byte[16];
            random.nextBytes(result);
            return new IvParameterSpec(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("使用了不存在的算法");
        }
    }

    /**
     * 得到用于加密每一条账户信息的加密器
     * @param uuid 这条账户信息的uuid
     * @param opmode 操作模式 就是Cipher类定义的表示加密还是解密的常量
     * @return 加密器实例
     */
    public Cipher getCipherForAccount(UUID uuid,int opmode){
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(global_encrypt_algorithm);
            cipher.init(opmode, getKeyForAccount(uuid), getIvForAccount(uuid));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试生成账户条目加密器时发生不可恢复错误");
        }
    }

    public Cipher getCipherMaster(int opmode){
        try {
            Key key=aesKeyFromSeed(masterkey_hash);
            Cipher cipher= Cipher.getInstance(global_encrypt_algorithm);
            cipher.init(opmode,key,getIvFromSeed(masterkey_hash));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试生成主密钥加密器时发生不可恢复错误");
        }
    }
    public Cipher getInnerCipher(int opmode){

        try {
            Key key=aesKeyFromSeed(randomkey);
            Cipher cipher= Cipher.getInstance(global_encrypt_algorithm);
            cipher.init(opmode,key,getIvFromSeed(randomkey));
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("尝试生成内部加密器时发生不可恢复错误");
        }
    }
}
