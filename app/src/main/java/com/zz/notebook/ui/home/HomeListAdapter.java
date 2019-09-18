package com.zz.notebook.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zz.notebook.EditorActivity;
import com.zz.notebook.R;
import com.zz.notebook.database.AccountItem;
import com.zz.notebook.util.BasicService;

import static com.zz.notebook.util.BasicService.toast;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.VH>{
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final ImageView avatar;
        public final TextView app_name;
        public final TextView account;
        public final TextView time;
        public VH(View v) {
            super(v);
            app_name = v.findViewById(R.id.list_item_name);
            avatar=v.findViewById(R.id.list_item_avatar);
            account=v.findViewById(R.id.list_item_account);
            time=v.findViewById(R.id.list_item_time);
        }
    }
    private HomeViewModel dataModel;//暂存数据
    private HomeFragment fragment;
    public HomeListAdapter(HomeViewModel model,HomeFragment fragment) {
        this.fragment=fragment;
        dataModel=model;
        dataModel.setListAdapter(this);//将自己加入HomeViewModel，方便两个类沟通
        //这里两个对象的生命周期是不一致的，Adapter跟随Fragment 朝生暮死,而ViewModel长很多
        //所以每次需要借助HomeFragment来清除已经用不到的引用
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        AccountItem note=dataModel.getAt(position);
        holder.app_name.setText(note.getTitle());
        holder.account.setText(note.getUsername());
        holder.avatar.setImageDrawable(BasicService.getAvatar(note.getBirthmark()));
        holder.time.setText(note.getTimeString());
        holder.itemView.setOnClickListener(v->{EditorActivity.edit(fragment.getContext(),HomeViewModel.database,position);});
    }

    @Override
    public int getItemCount() {
        return dataModel.count();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new VH(v);
    }
}
