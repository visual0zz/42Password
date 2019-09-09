package com.zz.notebook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {//悬空的 "+" 按钮
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//加载菜单 菜单内容只有一个 “搜索”
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    ActionMode.Callback searchMode=new ActionMode.Callback() {//标题栏进入搜索状态时的消息处理器
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.test,menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            Toast.makeText(getBaseContext(),menuItem.getTitle(),Toast.LENGTH_LONG).show();
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }
        @SuppressLint("NewApi")
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Toast.makeText(getBaseContext(),"版本="+this,Toast.LENGTH_LONG).show();
                actionMode.hide(ActionMode.TYPE_PRIMARY);
            getSupportActionBar().show();
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//处理点击搜索按钮的消息
        switch (item.getItemId()){
            case R.id.action_search://点击搜索按钮
                Toast.makeText(getBaseContext(),"搜索="+this,Toast.LENGTH_LONG).show();
                ActionBar actionBar=getSupportActionBar();

                this.startActionMode(searchMode);
                actionBar.hide();

        }
        return super.onOptionsItemSelected(item);
    }
}
