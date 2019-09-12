package com.zz.notebook.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.zz.notebook.model.AccountItem;

public class HomeViewModel extends ViewModel {
    private HomeListAdapter listAdapter;
    public void setListAdapter(HomeListAdapter adapter){listAdapter=adapter;}
    public void doSearch(String str){

    }
    @NonNull
    public AccountItem getAt(int index){
        return null;
    }
    public int count(){
        return 43;
    }
}