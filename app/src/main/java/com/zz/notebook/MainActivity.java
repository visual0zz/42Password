package com.zz.notebook;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.zz.notebook.ui.home.HomeFragment;
import com.zz.notebook.ui.home.HomeViewModel;
import com.zz.notebook.ui.home.SearchActionProvider;
import com.zz.notebook.util.BasicService;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);;
        fab.setOnClickListener(view ->{//悬空的 "+" 按钮
            EditorActivity.edit(this, HomeViewModel.database,-1);
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_test, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    Logger logger=Logger.getLogger(this.getClass().getName());
    public SearchActionProvider searchActionProvider=null;//搜索服务可以注册到这个变量里
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//加载菜单 菜单内容只有一个 “搜索”
        getMenuInflater().inflate(R.menu.main, menu);
        searchButton=menu.findItem(R.id.action_search);//得到菜单中 "搜索" 那一项
        SearchView searchView=(SearchView) searchButton.getActionView();//得到对应的搜索栏视图对象

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //logger.info("搜索栏提交了字符串="+s);
                if(searchActionProvider!=null)searchActionProvider.doSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {//当搜索栏变化时执行搜索操作
                //logger.info("搜索栏修改了字符串="+s);
                if(s.trim().isEmpty()&&searchActionProvider!=null)searchActionProvider.doSearch(null);
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            //logger.info("搜索栏关闭了");
            if(searchActionProvider!=null)searchActionProvider.doSearch(null);//null表示不进行搜索，显示所有东西
            return false;
        });
        //logger.log(Level.INFO,"添加了搜索按钮");
        if(searchActionProvider!=null)searchActionProvider.doSearch(null);//null表示不进行搜索，显示所有东西
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//处理点击搜索按钮的消息
        switch (item.getItemId()){
            case R.id.action_search://点击搜索按钮
                Toast.makeText(getBaseContext(),"搜索="+this,Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested(@Nullable SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }
    private MenuItem searchButton;
    public void showSearchButton(boolean show){
        if(searchButton!=null)searchButton.setVisible(show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HomeFragment.update();
    }
}
