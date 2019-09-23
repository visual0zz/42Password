package com.zz.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.zz.notebook.database.Database;
import com.zz.notebook.finger.FingerPrint;
import com.zz.notebook.ui.home.HomeViewModel;
import com.zz.notebook.util.Bash;
import com.zz.notebook.util.BasicService;

import java.io.File;

import static com.zz.notebook.database.ByteArrayUtils.isEqual;
import static com.zz.notebook.database.CipherService.hash;
import static com.zz.notebook.util.BasicService.getDatabaseFilePath;
import static com.zz.notebook.util.BasicService.getFingerPath;

public class LoginActivity extends Activity implements View.OnClickListener {
    ImageView avatar;
    Button login_button;
    Button purge_button;
    TextView password_view;
    TextView repeat_pass_view;
    LinearLayout repeat_frame;
    boolean login;

    ImageView finger;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasicService.rootContext=getApplicationContext();//将context缓存用于产生Toast消息
        setContentView(R.layout.activity_login);
        setupUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//如果版本支持改变状态栏颜色
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
    }
    @Override
    public void onResume(){
        if(new File(getFingerPath()).exists()){
            fingerLogin.onClick(null);
        }
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_purgedata_action:
                purgeData();
                break;
            case R.id.login_button:
                loginOrRegist();
                break;
        }
    }
    View.OnClickListener fingerLogin = view -> {//指纹登陆
        FingerPrint.authenticate(LoginActivity.this, password -> {
            try {
                HomeViewModel.database=new Database(new File(BasicService.getDatabaseFilePath()),password);
            } catch (Database.WrongMasterPasswordException |Database.DatabaseException e) {
                e.printStackTrace();
                BasicService.toast(getResources().getString(R.string.figer_faile));
                return;
            }
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            LoginActivity.this.setVisible(false);
        });
    };
    private void setupUI(){//配置UI界面
        avatar=findViewById(R.id.login_avatar);
        login_button=findViewById(R.id.login_button);
        password_view=findViewById(R.id.login_password_textview);
        purge_button=findViewById(R.id.login_purgedata_action);
        repeat_pass_view=findViewById(R.id.login_password_repeat);
        repeat_frame=findViewById(R.id.repeat_frame);//重复密码框的外框
        finger=findViewById(R.id.icon_finger);
        login_button.setOnClickListener(this);
        purge_button.setOnClickListener(this);
        avatar.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.big_ic_laucher));
        update();



        if(new File(getFingerPath()).exists()){
            finger.setVisibility(View.VISIBLE);
        }
        else {
            finger.setVisibility(View.INVISIBLE);//todo invisible
        }
        finger.setImageDrawable(getResources().getDrawable(R.drawable.icon_finger_print));
        finger.setOnClickListener(fingerLogin);
    }

    private void update(){//根据当前状态更新UI界面
        String path=getDatabaseFilePath();
        if(new File(path).exists()&& new File(path).isFile())login=true;
        else login=false;
        if(login){
            login_button.setText(R.string.login);
            purge_button.setVisibility(View.VISIBLE);
            repeat_pass_view.setVisibility(View.GONE);
            repeat_frame.setVisibility(View.GONE);
        } else {
            login_button.setText(R.string.regist);
            purge_button.setVisibility(View.INVISIBLE);
            repeat_pass_view.setVisibility(View.VISIBLE);
            repeat_frame.setVisibility(View.VISIBLE);
        }
    }
    private void loginOrRegist(){
        CharSequence sequence=password_view.getText();
        if(!login){//如果是注册，需要验证两次密码输入是否一致
            CharSequence sequence2=repeat_pass_view.getText();
            if(!isEqual(sequence.toString().getBytes(),sequence2.toString().getBytes()))
            {
                BasicService.toast("密码不一致");
                return;
            }
        }
        try {
            byte[] pass=sequence.toString().getBytes();
            HomeViewModel.database=new Database(new File(BasicService.getDatabaseFilePath()),hash(pass,pass,3));
        } catch (Database.DatabaseException e) {
            e.printStackTrace();
            BasicService.toast("数据库错误，无法登录或注册");
            update();
            return;
        } catch (Database.WrongMasterPasswordException e) {
            e.printStackTrace();
            BasicService.toast(BasicService.rootContext.getString(R.string.wrong_password));
            update();
            return;
        }
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        this.setVisible(false);
    }
    private void purgeData(){
        //BasicService.toast("清除数据库");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_to_purge_database);
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
            try {
                Bash.rmRd(new File(getDatabaseFilePath()));
                Bash.rmRd(new File(getFingerPath()));
            } catch (Exception e) {
                e.printStackTrace();
                BasicService.toast("清除数据库失败");
            }
            update();
            BasicService.toast("清除数据库成功");
        });
        builder.show();
    }


}
