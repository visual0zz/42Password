package com.zz.notebook.finger;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.zz.notebook.finger.biometriclib.BiometricPromptManager;
import com.zz.notebook.ui.home.HomeViewModel;
import com.zz.notebook.util.BasicService;

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

            return true;
        }else {
            return false;
        }
    }

    /**
     * 验证指纹并返回事先存储的密码
     * @return
     */
    public static byte[] authenticate(){
        return null;
    }

}
