package com.zz.notebook.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.zz.notebook.R;
import com.zz.notebook.dao.PasswordNote;
import com.zz.notebook.util.BasicService;

import java.security.InvalidParameterException;
import java.util.logging.Logger;

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
    public HomeListAdapter(HomeViewModel model) {
        dataModel=model;
        dataModel.setListAdapter(this);//将自己加入HomeViewModel，方便两个类沟通
        //这里两个对象的生命周期是不一致的，Adapter跟随Fragment 朝生暮死,而ViewModel长很多
        //所以每次需要借助HomeFragment来清除已经用不到的引用
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        PasswordNote note=dataModel.getAt(position);
        holder.app_name.setText("这是第"+position+"项");
        holder.account.setText(position+"@"+position+".com");
        holder.avatar.setImageDrawable(BasicService.getAvatar(position));
        holder.time.setText(position+":"+position+":"+position);
        holder.itemView.setOnClickListener(v ->toast("点击了第"+position+"个按钮"));
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
