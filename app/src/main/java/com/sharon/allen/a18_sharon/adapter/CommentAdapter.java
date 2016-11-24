package com.sharon.allen.a18_sharon.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.CommentActivity;
import com.sharon.allen.a18_sharon.activity.PersonalActivity;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Comment;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/21.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> commentList;
    private LayoutInflater inflater;
    private Handler handler;

    public CommentAdapter(Context context, List<Comment> commentList, Handler handler){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.commentList = commentList;
        this.handler = handler;

    }

    //外部调用，listview改变通知刷新adapter
    public void refresh(List<Comment> list) {
        commentList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = 0;
        try {
            size = commentList.size();
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Comment comment = commentList.get(position);
        //使用缓存条目，防止总是创建view对象而造成内存溢出
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_comment, null);
            //创建viewHoler封装所有条目使用的组件
            viewHolder = new ViewHolder();
            //可以理解为从vlist获取view  之后把view返回给ListView
            viewHolder.iv_comment_head = (ImageView) convertView.findViewById(R.id.iv_comment_head);
            viewHolder.iv_comment_sex = (ImageView) convertView.findViewById(R.id.iv_comment_sex);
            viewHolder.tv_comment_name = (TextView) convertView.findViewById(R.id.tv_comment_name);
            viewHolder.tv_comment_time = (TextView) convertView.findViewById(R.id.tv_comment_time);
            viewHolder.tv_comment_content = (TextView) convertView.findViewById(R.id.tv_comment_content);
            viewHolder.ll_comment_reply = (LinearLayout) convertView.findViewById(R.id.ll_comment_reply);

            //把viewHolder封装至view对象中，这样view被缓存时，viewHolder也就被缓存了
            convertView.setTag(viewHolder);

        } else {
            //从view中取出保存的viewHolder，viewHolder中就有所有的组件对象，不需要再去findViewById
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load(Constant.Server.PATH+comment.getHeadurl()).into(viewHolder.iv_comment_head);
        viewHolder.tv_comment_name.setText(comment.getUsername());
        viewHolder.tv_comment_content.setText(comment.getComment());
        if(comment.getSex().equals("男")){
            viewHolder.iv_comment_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if(comment.getSex().equals("女")){
            viewHolder.iv_comment_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            viewHolder.iv_comment_sex.setVisibility(View.GONE);
        }

        viewHolder.ll_comment_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                message.obj = comment.getUsername();
                message.arg1 = comment.getUserid();
                message.what = CommentActivity.WHAT_COMMENT_FOR_SOMEONE;
                handler.sendMessage(message);
            }
        });

        try {
            viewHolder.tv_comment_time.setText(TimeUtils.getTimeGap(comment.getTime(),TimeUtils.getCurrentTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        viewHolder.iv_comment_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("调整卡片！");
                startPersonalData(comment.getUserid());
            }
        });


        return convertView;
    }
    //把条目需要使用到的所有组件封装在这个类中
    class ViewHolder{
        ImageView iv_comment_head;
        ImageView iv_comment_sex;
        TextView tv_comment_name;
        TextView tv_comment_content;
        TextView tv_comment_time;
        LinearLayout ll_comment_reply;
    }

    //跳转到CommentActivity
    private void startPersonalData(int userid){
        Intent intent=new Intent();
        intent.setClass(context, PersonalActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userid);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
