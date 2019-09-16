package com.zz.notebook.ciper;


import androidx.annotation.NonNull;

import com.zz.notebook.util.BasicService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.zz.notebook.util.BasicService.global_encrypt_algorithm;
import static java.lang.System.exit;

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

    /**
     * 读取密文并解密填入自身
     * @param uid 本账户条目的uuid
     * @param cipherProvider 提供解密密钥的密钥工厂
     * @param data 密文数据
     * @throws InvalidKeyException 密钥工厂提供的密钥无法解密密文，
     * @throws ClassNotFoundException 解密出的明文中读取不出格式正确的数据
     */
    public void setAndDecryptData(UUID uid, CipherProvider cipherProvider, byte[] data) throws InvalidKeyException, ClassNotFoundException {//从密文解密构造帐号记录
        try {
            Cipher cipher = cipherProvider.getCipherForAccount(uid,Cipher.DECRYPT_MODE);// 初始化
            byte[] decrypted_data=cipher.doFinal(data);
            ObjectInputStream inputStream=new ObjectInputStream(new ByteArrayInputStream(decrypted_data));//将数据解密
            assign((AccountItem) inputStream.readObject());//将数据写入自己
            if(!this.uid.equals(uid))throw new InvalidKeyException("解密出的uid不一致，密码错误或者解码错了对象");
        } catch (BadPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("AccountItem读入了无法解析的数据块");
        }
    }

    /**
     * 将本账户条目加密得到密文
     * @param cipherProvider 提供加密密钥的密钥工厂
     * @return 加密得到的密文
     */
    public byte[] getEncryptedData(CipherProvider cipherProvider){//得到密文
        try {
            ByteArrayOutputStream arrayStream=new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(arrayStream);
            objectOutputStream.writeObject(this);//将自己写入字节数组流

            Cipher cipher = cipherProvider.getCipherForAccount(uid,Cipher.ENCRYPT_MODE);// 初始化
            return cipher.doFinal(arrayStream.toByteArray());//返回加密结果

        } catch (BadPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("加密AccountItem时发生不可恢复错误");
        }
    }


    //    UUID uid;
    //    String group;
    //    String title;
    //    String username;
    //    String url;
    //    String notes;
    //    PasswordProperty password;

    /**
     * 将另一个账户条目赋值到本对象，复制所有成员
     * @param in 源对象
     */
    public void assign(AccountItem in){
        uid=in.uid;
        group=in.group;
        title=in.title;
        username=in.username;
        url=in.url;
        notes=in.notes;
        password=in.password;
    }

    /**
     * 构造一个新的账户条目对象，复制原对象成员，但有新的UUID
     * @param old 旧的对象
     */
    public AccountItem(AccountItem old){
        this.assign(old);
        this.uid=UUID.randomUUID();
    }

    //"Group","Title","Username","Password","URL","Notes"
    @NonNull
    @Override
    public String toString() {
        return "{uid="+uid+"\tgroup="+group+"\ttitle="+title+"\tusername="+username+"\tpassword="+password+"\turl="+url+"\tnotes="+notes+"}";
    }


}

