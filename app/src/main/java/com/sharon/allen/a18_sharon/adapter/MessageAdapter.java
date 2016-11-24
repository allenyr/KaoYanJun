package com.sharon.allen.a18_sharon.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.MyPhotoActivity;
import com.sharon.allen.a18_sharon.activity.PersonalActivity;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Message;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */

public class MessageAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Message> mMessageList;
    private LayoutInflater mLayoutInflater;
    private Handler mHandler;
    private int oldPosition;

    //-----------------------回调------------------------
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
        void onLoadMoreData(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
    //-------------------------------------------------

    public MessageAdapter(Context context, List<Message> messageList, Handler handler){
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMessageList = messageList;
        this.mHandler = handler;
    }

    public void refresh(List<Message> list) {
        mMessageList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder viewHolder = (MessageViewHolder) holder;
        final Message message = mMessageList.get(position);
        Glide.with(mContext).load(Constant.Server.PATH+message.getSenderimg()).into(viewHolder.iv_message_senderurl);
        Glide.with(mContext).load(Constant.Server.PATH+message.getImgurl()).into(viewHolder.iv_message_img);
        viewHolder.tv_message_sender.setText(message.getSendername());
        viewHolder.tv_message_detail.setText(message.getDetail());
        //性别
        if(message.getSendersex().equals("男")){
            viewHolder.iv_message_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if (message.getSendersex().equals("女")){
            viewHolder.iv_message_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            viewHolder.iv_message_sex.setVisibility(View.GONE);
        }
        //时间
        try {
            viewHolder.tv_message_time.setText(TimeUtils.getTimeGap(message.getTime(),TimeUtils.getCurrentTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //头像点击侦听
        viewHolder.iv_message_senderurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPersonalActivity(message.getSenderid());
            }
        });
        //底部判断
//        oldPosition
        if(position == mMessageList.size()-1){
            onRecyclerViewListener.onLoadMoreData(position);
            LogUtils.i("position"+position);
        }

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        int size = 0;
        try {
            size = mMessageList.size();
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView iv_message_senderurl;
        public ImageView iv_message_img;
        public ImageView iv_message_sex;
        public TextView tv_message_sender;
        public TextView tv_message_detail;
        public TextView tv_message_time;
        public View rootView;
        public int position;

        public MessageViewHolder(View itemView) {
            super(itemView);
            iv_message_senderurl = (ImageView) itemView.findViewById(R.id.iv_message_senderurl);
            iv_message_img = (ImageView) itemView.findViewById(R.id.iv_message_img);
            iv_message_sex = (ImageView) itemView.findViewById(R.id.iv_message_sex);
            tv_message_sender = (TextView) itemView.findViewById(R.id.tv_message_sender);
            tv_message_detail = (TextView) itemView.findViewById(R.id.tv_message_detail);
            tv_message_time = (TextView) itemView.findViewById(R.id.tv_message_time);
            rootView = itemView.findViewById(R.id.ll_message_item);
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

    //跳转到PhotoActivity
    private void startPhotoActivity(){
        Intent intent=new Intent();
        intent.setClass(mContext, MyPhotoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    //跳转到PersonalActivity
    private void startPersonalActivity(int userid){
        Intent intent=new Intent();
        intent.setClass(mContext, PersonalActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userid);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
//        context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context,view,"share_head").toBundle());
    }

}
