package com.zz.notebook.ui.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.zz.notebook.LoginActivity;
import com.zz.notebook.MainActivity;
import com.zz.notebook.R;
import com.zz.notebook.database.ByteArrayUtils;
import com.zz.notebook.finger.biometriclib.BiometricPromptManager;
import com.zz.notebook.util.BasicService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class TestFragment extends Fragment {
    Logger logger= Logger.getLogger(TestFragment.class.getName());

    private TestViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(TestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        Button button=root.findViewById(R.id.test_button1);
        button.setOnClickListener(view->{test();});
        return root;
    }

    void test(){
        BiometricPromptManager manager=new BiometricPromptManager(getActivity());
        manager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
            @Override
            public void onSucceeded(Cipher cipher) {

            }
        });
    }
    void test2(){

    }

}