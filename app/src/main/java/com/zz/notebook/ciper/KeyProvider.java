package com.zz.notebook.ciper;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.spec.IvParameterSpec;

import static com.zz.notebook.ciper.CipherService.aesKeyFromSeed;
import static com.zz.notebook.util.ByteArrayUtils.concat;
import static com.zz.notebook.util.ByteArrayUtils.uuidToBytes;
import static java.lang.System.exit;

/**
 * 用于生成不同对象的不同加密密钥的密钥生成器，根据全局密钥设定，生成具体的加密密钥
 */
public class KeyProvider{
    byte[] masterkey_hash;
    byte[] randomkey;
    public KeyProvider(byte[] masterkey_hash, byte[] randomkey){this.randomkey=randomkey;this.masterkey_hash = masterkey_hash;}
    public Key forAccount(UUID uuid){//对应每一条账户信息的加密密钥
        byte[] uid_bytes=uuidToBytes(uuid);
        byte[] accountkey= concat(concat(randomkey, masterkey_hash),uid_bytes);
        return aesKeyFromSeed(accountkey);
    }
    public IvParameterSpec getIv(UUID uuid){
        try {
            byte[] uid_bytes=uuidToBytes(uuid);
            byte[] accountkey= concat(concat(randomkey,uid_bytes),masterkey_hash);
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(accountkey);
            byte[] result=new byte[16];
            random.nextBytes(result);
            return new IvParameterSpec(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            exit(1);
        }
        return null;
    }
}
