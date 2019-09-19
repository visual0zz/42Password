package com.zz.notebook.finger;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.zz.notebook.util.BasicService;

public class FingerPrint {
    public static boolean isFingerPrintAvailable(){
        if(!FingerprintManagerCompat.from(BasicService.rootContext).isHardwareDetected())return false;//没有硬件就直接退出
        return true;
    }
}
