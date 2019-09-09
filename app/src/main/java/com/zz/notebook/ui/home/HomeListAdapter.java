package com.zz.notebook.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.zz.notebook.R;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.VH>{
    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final ImageView avatar;
        public final TextView app_name;
        public final TextView account;
        public VH(View v) {
            super(v);
            app_name = v.findViewById(R.id.list_item_name);
            avatar=v.findViewById(R.id.list_item_avatar);
            account=v.findViewById(R.id.list_item_account);
        }
    }

    public HomeListAdapter() {
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.app_name.setText("这是第"+position+"项");
        holder.avatar.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.avata1));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(),"点击了第"+position+"个按钮",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 23;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new VH(v);
    }
}
