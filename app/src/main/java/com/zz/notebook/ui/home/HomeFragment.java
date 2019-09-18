package com.zz.notebook.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zz.notebook.MainActivity;
import com.zz.notebook.R;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private MainActivity activity=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.note_list_view);//加载主列表
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));//设置列表为线性布局
        recyclerView.setAdapter(new HomeListAdapter(homeViewModel,this));

        activity= (MainActivity) getActivity();

        activity.showSearchButton(true);//显示搜索按钮
        activity.searchActionProvider= str -> {//发生搜索事件时
            homeViewModel.doSearch(str);//调用数据层进行搜索
        };
        activity.fab.show();
        return root;
    }

    @Override
    public void onDestroyView() {
        activity.showSearchButton(false);//隐藏搜索按钮
        activity.searchActionProvider=null;//当本页面隐藏时消除搜索事件
        homeViewModel.setListAdapter(null);//清除无效的引用，防止更新已经不存在的view
        super.onDestroyView();
        activity.fab.hide();
    }

}

