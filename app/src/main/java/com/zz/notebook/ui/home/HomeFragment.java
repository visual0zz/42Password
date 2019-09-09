package com.zz.notebook.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zz.notebook.MainActivity;
import com.zz.notebook.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    MainActivity activity=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.note_list_view);//加载主列表
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));//设置列表为线性布局
        recyclerView.setAdapter(new HomeListAdapter());
        homeViewModel.getText().observe(this, (s)->{});

        activity= (MainActivity) getActivity();
        activity.showSearchButton(true);//显示搜索按钮
        return root;
    }

    @Override
    public void onDestroyView() {
        activity.showSearchButton(false);//隐藏搜索按钮
        super.onDestroyView();
    }
}

