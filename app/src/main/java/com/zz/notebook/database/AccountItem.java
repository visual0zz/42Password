package com.zz.notebook.database;


import androidx.annotation.NonNull;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import static com.zz.notebook.database.CipherConfig.global_serialVersionUID;

/**
 * 为了适配keepass的csv输出格式 属性定义为:"Group","Title","Username","Password","URL","Notes"
 */
public class AccountItem implements Serializable {//表示一条帐号记录
    private static final long serialVersionUID =
            AccountItem.class.getCanonicalName().concat(global_serialVersionUID).hashCode();
    private UUID uid;
    private String group;
    private String title;
    private String account;
    private String url;
    private String notes;
    private PasswordProperty password;
    private long timestamp;


    public AccountItem setGroup(String group) {
        this.group = (group==null?"":group);
        return this;
    }
    public AccountItem setTitle(String title) {
        this.title = (title==null?"":title);
        return this;
    }
    public AccountItem setAccount(String account) {
        this.account = (account==null?"":account);
        return this;
    }
    public AccountItem setUrl(String url) {
        this.url = (url==null?"":url);
        return this;
    }
    public AccountItem setNotes(String notes) {
        this.notes = (notes==null?"":notes);
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
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() { return timestamp;}
    public String getGroup() { return group; }
    public String getTitle() { return title; }
    public String getAccountName() { return account; }
    public String getUrl() { return url; }
    public String getNotes() { return notes; }
    public PasswordProperty getPassword() { return password; }
    public UUID getUid() { return uid; }




    public AccountItem(){//构造新的帐号记录
        uid=UUID.randomUUID();
        group=title=url= account =notes="";
        password=new PasswordProperty();
        timestamp=System.currentTimeMillis();//获得当前时间记录为时间戳
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
    //    String account;
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
        account =in.account;
        url=in.url;
        notes=in.notes;
        password=in.password;
        timestamp=in.timestamp;
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
        return "{uid="+uid+"\tgroup="+group+"\ttitle="+title+"\taccount="+ account +"\tpassword="+password+"\turl="+url+"\tnotes="+notes+"}";
    }
    public String getBirthmark(){
        return notes+ account +title+url;
    }
    public String getAvatarSeed(){return notes+title;}
    public String getTimeString(){
        Date date=new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return formatter.format(date);
    }
}

