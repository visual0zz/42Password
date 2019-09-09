package com.zz.notebook.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zz.notebook.dao.PasswordNote;

public class HomeViewModel extends ViewModel {
    private HomeListAdapter listAdapter;
    public void setListAdapter(HomeListAdapter adapter){listAdapter=adapter;}
    public void doSearch(String str){

    }
    @NonNull
    public PasswordNote getAt(int index){
        return null;
    }
    public int count(){
        return 43;
    }
}