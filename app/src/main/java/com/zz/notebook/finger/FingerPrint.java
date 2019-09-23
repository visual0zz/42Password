package com.zz.notebook.finger;

import android.app.Activity;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.zz.notebook.database.CipherProvider;
import com.zz.notebook.database.CipherService;
import com.zz.notebook.finger.biometriclib.BiometricPromptManager;
import com.zz.notebook.ui.home.HomeViewModel;
import com.zz.notebook.util.Bash;
import com.zz.notebook.util.BasicService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import static com.zz.notebook.database.CipherService.hash;
import static java.lang.System.exit;

public class FingerPrint {
    public static boolean isFingerPrintAvailable(){
        if(!FingerprintManagerCompat.from(BasicService.rootContext).isHardwareDetected())return false;//没有硬件就直接退出
        return true;
    }

    /**
     *
     * @param password 用户密码
     * @return 执行成功与否 true表示成功 false表示密码错误
     */
    public static boolean writPrintInfo(byte[] password){
        if(HomeViewModel.database.checkPassword(password)){
            try {
                byte[] data=new CipherProvider("1".getBytes(),"1".getBytes())
                        .getCipherMaster(Cipher.ENCRYPT_MODE).doFinal(password);
                File finger=new File(BasicService.getFingerPath());
                Bash.touch(finger);
                new FileOutputStream(finger).write(data);//写入密码
            } catch (IOException|IllegalBlockSizeException|BadPaddingException e) {
                e.printStackTrace();
                exit(1);
            }
            return true;
        }else {
            return false;
        }
    }

    /**
     * 验证指纹并返回事先存储的密码
     * @return 是否有事先设置的finger文件  true表示有，可以正常执行，false表示没有
     */
    public static boolean authenticate(Activity activity,Login login){
        File finger=new File(BasicService.getFingerPath());
        if(!finger.exists())return false;//没有finger文件
        BiometricPromptManager manager=new BiometricPromptManager(activity);
        manager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
            @Override
            public void onSucceeded(Cipher cipher) {
                try {
                    byte[]data=new byte[48];
                    new FileInputStream(finger).read(data);
                    byte[]pass= new CipherProvider("1".getBytes(),"1".getBytes())
                            .getCipherMaster(Cipher.DECRYPT_MODE).doFinal(data);
                    login.login(pass);
                } catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
                    e.printStackTrace();
                    exit(1);
                }
            }
        });
        return true;

    }

    public  interface Login{
         void login(byte[] password);
    }

}
