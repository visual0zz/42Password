package com.zz.notebook.ui.config;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.zz.notebook.R;
import com.zz.notebook.database.CipherService;
import com.zz.notebook.finger.FingerPrint;
import com.zz.notebook.util.Bash;
import com.zz.notebook.util.BasicService;

import java.io.File;
import java.nio.file.AccessDeniedException;

import static com.zz.notebook.database.CipherService.hash;
import static java.lang.System.exit;

public class ConfigFragment extends Fragment {

    private ConfigViewModel configViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        configViewModel =
                ViewModelProviders.of(this).get(ConfigViewModel.class);
        View root = inflater.inflate(R.layout.fragment_config, container, false);
        Switch finger_switch=root.findViewById(R.id.finger_switch);
        finger_switch.setChecked(new File(BasicService.getFingerPath()).exists());//finger文件
        finger_switch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){//如果是要设置指纹解锁

                TextView password=new EditText(getContext());
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(getContext())
                .setTitle(R.string.hint_password)
                .setView(password)
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    byte[] pass=password.getText().toString().getBytes();
                    if(!FingerPrint.writPrintInfo(hash(pass,pass,3))){
                        finger_switch.setChecked(false);//如果密码不对就取消选择
                        BasicService.toast(getContext().getResources().getString(R.string.wrong_password));
                    }
                }).show();
            }else {//如果是要取消指纹解锁
                try {
                    Bash.rmRd(new File(BasicService.getFingerPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    exit(1);
                }
            }
        });
        return root;
    }
}