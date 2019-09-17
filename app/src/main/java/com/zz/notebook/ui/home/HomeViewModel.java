package com.zz.notebook.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.zz.notebook.database.AccountItem;
import com.zz.notebook.database.Database;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private HomeListAdapter listAdapter;
    List<Integer> cache;
    public static Database database;
    public void setListAdapter(HomeListAdapter adapter){listAdapter=adapter;}
    public void doSearch(String str){
        if(str==null||str.equals("")){//str为空表示不进行过滤
            cache=null;
            return;
        }
        cache=new ArrayList<>();
        for(int i=0;i<database.size();i++){//遍历数据库所有数据
            AccountItem item=database.getAccountItem(i);
            if(item.getBirthmark().contains(str)){//如果搜索有结果
                cache.add(i);
            }
        }
    }
    @NonNull
    public AccountItem getAt(int index){
        if(database!=null){
            if(cache==null)
                return database.getAccountItem(index);
            else
                return database.getAccountItem(cache.get(index));
        }
        else return null;
    }
    public int count(){
        if(database!=null){
            if(cache==null)
                return database.size();
            else
                return cache.size();
        }
        else return 0;
    }
}