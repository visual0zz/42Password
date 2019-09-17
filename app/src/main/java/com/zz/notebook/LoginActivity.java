package com.zz.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zz.notebook.database.Database;
import com.zz.notebook.ui.home.HomeViewModel;
import com.zz.notebook.util.Bash;
import com.zz.notebook.util.BasicService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static com.zz.notebook.util.BasicService.getDatabaseFilePath;

public class LoginActivity extends Activity implements View.OnClickListener {
    ImageView avatar;
    Button login_button;
    Button purge_button;
    TextView password_view;
    boolean login;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BasicService.rootContext=getApplicationContext();//将context缓存用于产生Toast消息
        setContentView(R.layout.activity_main_login);
        setupUI();
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
    private void setupUI(){//配置UI界面
        avatar=findViewById(R.id.login_avatar);
        login_button=findViewById(R.id.login_button);
        password_view=findViewById(R.id.login_password_textview);
        purge_button=findViewById(R.id.login_purgedata_action);
        login_button.setOnClickListener(this);
        purge_button.setOnClickListener(this);
        avatar.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.big_ic_laucher));
        update();
    }

    private void update(){//根据当前状态更新UI界面
        String path=getDatabaseFilePath();
        if(new File(path).exists()&& new File(path).isFile())login=true;
        else login=false;
        if(login){
            login_button.setText(R.string.login);
            purge_button.setVisibility(View.VISIBLE);
        } else {
            login_button.setText(R.string.regist);
            purge_button.setVisibility(View.INVISIBLE);
        }
    }
    private void loginOrRegist(){
        CharSequence sequence=password_view.getText();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        PrintStream printStream=new PrintStream(stream);
        for(int i=0;i<sequence.length();i++){
            printStream.print(sequence.charAt(i));
        }

        try {
            HomeViewModel.database=new Database(new File(BasicService.getDatabaseFilePath()),stream.toByteArray());
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
