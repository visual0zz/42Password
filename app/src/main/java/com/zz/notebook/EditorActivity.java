package com.zz.notebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zz.notebook.database.Database;
import com.zz.notebook.util.BasicService;

import java.util.List;
import java.util.Set;

public class EditorActivity extends AppCompatActivity {

    private static Database.Editor editor;
    boolean editing;
    TextView timeView;
    EditText accountView;
    EditText urlView;
    EditText notesView;
    EditText titleView;
    EditText passwordView;

    Button submit_edit_button;
    Button delete_button;
    Button cancel_button;
    Button chose_group;

    CheckBox show_pass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        initUI();
        update();
    }

    public static void edit(Activity from, Database db, int index){//使用index<0表示新建
        if(index<0) editor=db.getEditor(null);
        else editor=db.getEditor(index);
        Intent intent=new Intent(from,EditorActivity.class);
        from.startActivityForResult(intent,0);
    }

    private View.OnClickListener onCancel= view -> {
        if(!editing)finish();//如果是查看模式，没必要提醒，直接退出就好
        else MessageBox(getResources().getString(R.string.editor_giveup_confirm),(dialogInterface, i) -> {
            finish();
        },null);
    };
    private View.OnClickListener onSubmit= view -> {
        MessageBox(getResources().getString(R.string.editor_save_confirm),(dialogInterface, i) -> {
            editor.setTitle(titleView.getText().toString());
            editor.setNotes(notesView.getText().toString());
            editor.setUsername(accountView.getText().toString());
            editor.setUrl(urlView.getText().toString());
            editor.setPassword(passwordView.getText().toString());
            editor=editor.submit();
            editor.saveDatabaseToFile();
            intoShow();
        },null);
    };
    private View.OnClickListener onDelete= view -> {
        MessageBox(getResources().getString(R.string.editor_delete_confirm),(dialogInterface, i) -> {
            editor.delete();
            editor.saveDatabaseToFile();
            finish();
        },null);
    };
    private View.OnClickListener onEditor= view -> {
        intoEdit();
    };

    private void intoShow(){//编辑器进入展示模式
        editing=false;
        update();

    }
    private void intoEdit(){//编辑器进入编辑模式
        editing=true;
        update();
    }
    private void update(){//更新UI状态
        if(editing){
            this.setTitle(R.string.edit);
        } else {
            this.setTitle(R.string.app_name);
        }

        if(editing){//如果是编辑状态就开放所有编辑框的编辑权限
            titleView.setEnabled(true);
            accountView.setEnabled(true);
            urlView.setEnabled(true);
            notesView.setEnabled(true);
            passwordView.setEnabled(true);
            chose_group.setOnClickListener(this::onChoseGroupButton);
        }else {//显示模式就禁止编辑
            titleView.setEnabled(false);
            accountView.setEnabled(false);
            urlView.setEnabled(false);
            notesView.setEnabled(false);
            passwordView.setEnabled(false);
            chose_group.setOnClickListener(null);
        }

        if(editing){//修改编辑按钮的字和功能
            submit_edit_button.setText(R.string.save);
            submit_edit_button.setOnClickListener(onSubmit);
        }else {
            submit_edit_button.setText(R.string.edit);
            submit_edit_button.setOnClickListener(onEditor);
        }

        ImageView avatar=findViewById(R.id.editor_avatar);
        if(editing){//修改头像为笔
            avatar.setImageDrawable(getResources().getDrawable(R.drawable.pen));
        }else {
            avatar.setImageDrawable(getResources().getDrawable(R.drawable.big_ic_laucher));
        }
        if(editor.getGroup().equals("")) {//如果没有分组
            chose_group.setText(R.string.chose_group);
        }else {
            chose_group.setText(editor.getGroup());
        }
    }//update
    private void initUI(){//初始化UI状态
        if(editor.isNew())editing=true;
        else editing=false;

        titleView=findViewById(R.id.editor_title);
        accountView=findViewById(R.id.editor_account);
        urlView=findViewById(R.id.editor_url);
        notesView=findViewById(R.id.editor_notes);
        passwordView=findViewById(R.id.editor_password);
        timeView=findViewById(R.id.editor_time);

        submit_edit_button=findViewById(R.id.editor_button_edit);
        delete_button=findViewById(R.id.editor_button_delete);
        cancel_button=findViewById(R.id.editor_button_cancel);
        chose_group=findViewById(R.id.editor_chose_group);

        cancel_button.setOnClickListener(onCancel);
        delete_button.setOnClickListener(onDelete);


        show_pass=findViewById(R.id.editor_showpass);
        show_pass.setOnCheckedChangeListener((compoundButton, b) -> {//选择隐藏还是显示密码的眼睛按钮
            if(b){
                passwordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        reReadData();
    }
    private void reReadData(){
        urlView.setText(editor.getUrl());
        accountView.setText(editor.getAccountName());
        titleView.setText(editor.getTitle());
        notesView.setText(editor.getNotes());
        passwordView.setText(editor.getPassword());
        timeView.setText(editor.getTimeString());
    }
    private void MessageBox(String message, DialogInterface.OnClickListener onOk, DialogInterface.OnClickListener onCancel){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.confirm,onOk);
        builder.setNegativeButton(R.string.cancel,onCancel);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        onCancel.onClick(null);
    }

    private void onChoseGroupButton(View v){//选择分组
        PopupMenu popupMenu=new PopupMenu(this,v);
        Menu menu=popupMenu.getMenu();
        String[] groups=editor.getGroups();//获得所有分组
        menu.add(R.string.setup_custom_group);
        for(String group:groups){
            menu.add(group);
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String title= (String) menuItem.getTitle();
            if(title.equals(getResources().getString(R.string.setup_custom_group))){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                EditText editText=new EditText(this);
                builder.setTitle(R.string.hint_input_groupname);
                builder.setView(editText);
                builder.setNegativeButton(R.string.cancel,null);
                builder.setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    editor.setGroup(editText.getText().toString());
                    update();
                });
                builder.show();
            }else
                editor.setGroup(title);
            update();
            return false;
        });
        popupMenu.show();
    }
}
