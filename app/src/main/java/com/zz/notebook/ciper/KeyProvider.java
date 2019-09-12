package com.zz.notebook.ciper;

import com.zz.notebook.util.ByteArrayUtils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.zz.notebook.util.BasicService.global_encrypt_algorithm;
import static com.zz.notebook.util.ByteArrayUtils.concat;
import static java.lang.System.exit;

public class KeyProvider{
    byte[] userkey_hash;
    byte[] randomkey;
    public KeyProvider(byte[] userkey_hash,byte[] randomkey){this.randomkey=randomkey;this.userkey_hash=userkey_hash;}
    public Key forAccount(UUID uuid){//对应每一条账户信息的加密密钥
        try {
            byte[] uid_bytes=uuid.toString().getBytes("utf-8");
            byte[] accountkey= concat(concat(randomkey,userkey_hash),uid_bytes);

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(accountkey));
            return kgen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        }
        return null;
    }
}
