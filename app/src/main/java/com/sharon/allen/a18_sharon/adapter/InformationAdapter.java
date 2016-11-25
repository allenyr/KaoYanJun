package com.sharon.allen.a18_sharon.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.WebActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.fragment.InformationFragment;
import com.sharon.allen.a18_sharon.model.Infom;

import java.util.List;


/**
 * Created by Administrator on 2016/8/18.
 */
public class InformationAdapter extends RecyclerView.Adapter {

    List<Infom> dataList;
    private Context mContext;
    private LayoutInflater inflater;
    private Handler handler;
    private UserDataManager userDataManager;

    //国家研究生网
    private static final String GDUT_URL = "http://yzw.gdut.edu.cn/";
    //广东工业大学研究生招生官网
    private static final String CHINA_URL = "http://yz.chsi.com.cn/";

    public InformationAdapter(Context context, List<Infom> dataList, Handler handler, UserDataManager userDataManager) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.handler = handler;
        this.userDataManager = userDataManager;
    }

    //-----------------------回调------------------------
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
        void onLoadMoreData(int position);
    }

    private InformationAdapter.OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(InformationAdapter.OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
    //-------------------------------------------------

    //外部调用，listview改变通知刷新adapter
    public void refresh(List<Infom> list) {
        dataList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_infom, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new InformationAdapter.InformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        InformationViewHolder viewHolder = (InformationViewHolder) holder;
        viewHolder.position = position;
        final Infom infom = dataList.get(position);

        viewHolder.tv_database_text.setText(infom.getName());
        if (infom.getNature().equals("0")) {
            viewHolder.iv_file_ico.setBackgroundResource(R.drawable.ico_dir);
            viewHolder.iv_file_share.setVisibility(View.GONE);
        } else {
            viewHolder.iv_file_ico.setBackgroundResource(R.drawable.ico_file);
            viewHolder.iv_file_share.setVisibility(View.GONE);
//            viewHolder.iv_file_share.setVisibility(View.VISIBLE);
        }

        viewHolder.iv_file_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile(infom,position,true);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = dataList.size();
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    //把条目需要使用到的所有组件封装在这个类中
    class InformationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView tv_database_text;
        ImageView iv_file_ico;
        ImageView iv_file_share;
//        LinearLayout ll_database_menus;
        public View rootView;
        public int position;

        public InformationViewHolder(View itemView) {
            super(itemView);
            //可以理解为从vlist获取view  之后把view返回给ListView
            tv_database_text = (TextView) itemView.findViewById(R.id.tv_database_text);
            iv_file_ico = (ImageView) itemView.findViewById(R.id.iv_file_ico);
            iv_file_share = (ImageView) itemView.findViewById(R.id.iv_file_share);
            rootView = itemView.findViewById(R.id.ll_database_menus);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(null != onRecyclerViewListener){
                return onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }


    private void startWebActivity(String url) {
        Intent intent = new Intent();
        intent.setClass(mContext, WebActivity.class);
        //用Bundle携带数据
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString("weburl", url);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    private void shareFile(Infom infom, int position, boolean isShare){
        Message message = handler.obtainMessage();
        if(infom.getNature().equals("0")){
            message.arg1 = 0;
        }else {
            message.arg1 = 1;
        }
        message.arg2 = position;
        message.obj = isShare;
        message.what = InformationFragment.WHAT_FILE_INFO;
        handler.sendMessage(message);
    }
}