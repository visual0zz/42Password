package com.zz.notebook.ciper;

import androidx.annotation.NonNull;

import com.zz.notebook.util.BasicService;
import com.zz.notebook.util.ByteArrayUtils;

import java.io.Serializable;
import java.security.Key;

public class PasswordProperty<T extends Serializable> implements Serializable {//表示一个密码,平时也保持加密，仅仅在需要显示的时候解密一段时间
    private static final long serialVersionUID =
            PasswordProperty.class.getCanonicalName().concat(BasicService.global_serialVersionUID).hashCode();
    byte[] data;
    public  T get(Key decrypt_key){
        return null;
    }
    public void set(T plain,Key encrypt_key){

    }

    @NonNull
    @Override
    public String toString() {
        return ByteArrayUtils.bytesToHex(data);
    }
}
