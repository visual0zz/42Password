package com.zz.notebook;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class GitPushActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_push);
        setTitle(R.string.config_export_database);
    }
}
