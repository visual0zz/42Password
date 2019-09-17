package com.zz.notebook.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.zz.notebook.ciper.AccountItem;
import com.zz.notebook.ciper.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        List<AccountItem> data=database.getData();
        for(int i=0;i<data.size();i++){//遍历数据库所有数据
            AccountItem item=data.get(i);
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