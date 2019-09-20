package com.zz.notebook.finger.biometriclib;

import android.os.CancellationSignal;
import android.support.annotation.NonNull;

/**
 * Created by gaoyang on 2018/06/19.
 */
interface IBiometricPromptImpl {

    void authenticate(@NonNull CancellationSignal cancel,
                      @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);

}
