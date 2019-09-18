package com.zz.notebook.database;

import androidx.annotation.NonNull;

import com.zz.notebook.util.BasicService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class PasswordProperty implements Serializable {//表示一个密码,平时也保持加密，仅仅在需要显示的时候解密一段时间
    private static final long serialVersionUID =
            PasswordProperty.class.getCanonicalName().concat(BasicService.global_serialVersionUID).hashCode();
    public PasswordProperty(){}
    public PasswordProperty(String plain,CipherProvider provider){
        set(plain,provider);
    }
    byte[] data;
    public  String get(CipherProvider provider){
        try {
            if(data==null)return "";
            ByteArrayInputStream stream=new ByteArrayInputStream(provider.getInnerCipher(Cipher.DECRYPT_MODE).doFinal(data));
            String plain=(String)new ObjectInputStream(stream).readObject();
            if(plain==null)
                return "";
            else
                return plain;
        } catch (IOException | BadPaddingException | IllegalBlockSizeException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("密码模块发生内部错误");
        }
    }
    public void set(String plain,CipherProvider provider){
        try {
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            new ObjectOutputStream(stream).writeObject(plain);
            data=provider.getInnerCipher(Cipher.ENCRYPT_MODE).doFinal(stream.toByteArray());
        } catch (IOException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("密码模块发生内部错误");
        }
    }

    @NonNull
    @Override
    public String toString() {
        return ByteArrayUtils.bytesToHex(data);
    }
}
