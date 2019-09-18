package com.zz.notebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zz.notebook.database.Database;

import java.util.UUID;

public class EditorActivity extends AppCompatActivity {

    private static Database.Editor editor;
    boolean editing;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        initUI();
        update();
    }

    public static void edit(Context from,Database db,int index){//使用index<0表示新建
        if(index<0) editor=db.getEditor(null);
        else editor=db.getEditor(index);
        Intent intent=new Intent(from,EditorActivity.class);
        from.startActivity(intent);
    }

    private View.OnClickListener onCancel= view -> {

    };
    private View.OnClickListener onSubmit= view -> {

    };
    private View.OnClickListener onDelete= view -> {

    };
    private View.OnClickListener onEditor= view -> {

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
        if(!editing && editor.isNew()){
            finish();//新建条目没有展示界面，退出编辑后就直接退出编辑器
            return;
        }
        if(editing)this.setTitle(R.string.edit);
        else this.setTitle(R.string.app_name);

    }
    private void initUI(){//初始化UI状态
        if(editor.isNew())editing=true;
        else editing=false;
    }
}
