package com.zz.notebook.model;


import com.zz.notebook.ciper.KeyProvider;
import com.zz.notebook.util.BasicService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.zz.notebook.util.BasicService.global_encrypt_algorithm;
import static com.zz.notebook.util.ByteArrayUtils.bytesToString;
import static java.lang.System.exit;
import static java.lang.System.in;

/**
 * 为了适配keepass的csv输出格式 属性定义为:"Group","Title","Username","Password","URL","Notes"
 */
public class AccountItem implements Serializable {//表示一条帐号记录
    private static final long serialVersionUID =
            AccountItem.class.getCanonicalName().concat(BasicService.global_serialVersionUID).hashCode();
    UUID uid;
    String group;
    String title;
    String username;
    String url;
    String notes;
    PasswordProperty password;


    public AccountItem setGroup(String group) {
        this.group = group;
        return this;
    }
    public AccountItem setTitle(String title) {
        this.title = title;
        return this;
    }
    public AccountItem setUsername(String username) {
        this.username = username;
        return this;
    }
    public AccountItem setUrl(String url) {
        this.url = url;
        return this;
    }
    public AccountItem setNotes(String notes) {
        this.notes = notes;
        return this;
    }
    public AccountItem setPassword(PasswordProperty password) {
        this.password = password;
        return this;
    }
    public AccountItem setUid(UUID uid){
        this.uid=uid;
        return this;
    }


    public String getGroup() { return group; }
    public String getTitle() { return title; }
    public String getUsername() { return username; }
    public String getUrl() { return url; }
    public String getNotes() { return notes; }
    public PasswordProperty getPassword() { return password; }
    public UUID getUid() { return uid; }




    public AccountItem(){//构造新的帐号记录
        uid=UUID.randomUUID();
        group=title=url=username=notes="";
        password=new PasswordProperty();
    }
    public void setAndDecryptData(UUID uid,KeyProvider keyProvider,byte[] data) throws InvalidKeyException, ClassNotFoundException {//从密文解密构造帐号记录
        try {
            Key key=keyProvider.forAccount(uid);
            Cipher cipher = Cipher.getInstance(global_encrypt_algorithm);//默认ECB模式
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            ObjectInputStream inputStream=new ObjectInputStream(new ByteArrayInputStream(cipher.doFinal(data)));//将数据解密
            set((AccountItem) inputStream.readObject());//将数据写入自己
            if(!this.uid.equals(uid))throw new InvalidKeyException("解密出的uid不一致，密码错误或者解码错了对象");
        } catch ( NoSuchAlgorithmException|NoSuchPaddingException| BadPaddingException| IllegalBlockSizeException|IOException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    public byte[] getEncryptedData(KeyProvider keyProvider){//得到密文
        try {
            Key key=keyProvider.forAccount(uid);
            ByteArrayOutputStream arrayStream=new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(arrayStream);
            objectOutputStream.writeObject(this);//将自己写入字节数组流

            Cipher cipher = Cipher.getInstance(global_encrypt_algorithm);//默认ECB模式
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return cipher.doFinal(arrayStream.toByteArray());//返回加密结果

        } catch ( Exception e) {
            e.printStackTrace();
            exit(1);
        }
        return null;
    }
    public void set(AccountItem in){
        uid=in.uid;
        title=in.title;
        username=in.username;
        url=in.url;
        notes=in.notes;
        password=in.password;
    }
}

