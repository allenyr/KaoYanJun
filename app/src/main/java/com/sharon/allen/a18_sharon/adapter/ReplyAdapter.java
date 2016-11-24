package com.sharon.allen.a18_sharon.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.sharon.allen.a18_sharon.activity.PersonalActivity;
import com.sharon.allen.a18_sharon.activity.ReplyActivity;
import com.sharon.allen.a18_sharon.fragment.QuestionFragment;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Reply;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.view.CircleImageView.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/8/21.
 */
public class ReplyAdapter extends BaseAdapter {

    private Context context;
    private List<Reply> replyList;
    private LayoutInflater inflater;
    private Handler handler;

    public ReplyAdapter(Context context, List<Reply> replyList, Handler handler){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.replyList = replyList;
        this.handler = handler;

    }

    //外部调用，listview改变通知刷新adapter
    public void refresh(List<Reply> list) {
        replyList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = 0;
        try {
            size = replyList.size();
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

        final Reply reply = replyList.get(position);
        //使用缓存条目，防止总是创建view对象而造成内存溢出
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_reply, null);
            //创建viewHoler封装所有条目使用的组件
            viewHolder = new ViewHolder();
            //可以理解为从vlist获取view  之后把view返回给ListView
            viewHolder.iv_reply_head = (CircleImageView) convertView.findViewById(R.id.civ_reply_head);
            viewHolder.iv_reply_sex = (ImageView) convertView.findViewById(R.id.iv_reply_sex);
            viewHolder.tv_reply_username = (TextView) convertView.findViewById(R.id.tv_reply_username);
            viewHolder.tv_reply_time = (TextView) convertView.findViewById(R.id.tv_reply_time);
            viewHolder.tv_reply_content = (TextView) convertView.findViewById(R.id.tv_reply_content);
            viewHolder.ll_reply_up = (LinearLayout) convertView.findViewById(R.id.ll_reply_up);
            viewHolder.ll_reply_down = (LinearLayout) convertView.findViewById(R.id.ll_reply_down);
            viewHolder.tv_reply_up = (TextView) convertView.findViewById(R.id.tv_reply_up);
            viewHolder.tv_reply_down = (TextView) convertView.findViewById(R.id.tv_reply_down);
            viewHolder.iv_reply_type = (ImageView) convertView.findViewById(R.id.iv_reply_type);

            //把viewHolder封装至view对象中，这样view被缓存时，viewHolder也就被缓存了
            convertView.setTag(viewHolder);

        } else {
            //从view中取出保存的viewHolder，viewHolder中就有所有的组件对象，不需要再去findViewById
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(context).load(Constant.Server.PATH+reply.getHeadurl()).into(viewHolder.iv_reply_head);
        viewHolder.tv_reply_username.setText(reply.getUsername());
        viewHolder.tv_reply_content.setText(reply.getReply());
        viewHolder.tv_reply_up.setText(reply.getUp()+"");
        viewHolder.tv_reply_down.setText(reply.getDown()+"");

        if(reply.getSex().equals("男")){
            viewHolder.iv_reply_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if(reply.getSex().equals("女")){
            viewHolder.iv_reply_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            viewHolder.iv_reply_sex.setVisibility(View.GONE);
        }

        try {
            viewHolder.tv_reply_time.setText(TimeUtils.getTimeGap(reply.getTime(),TimeUtils.getCurrentTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        viewHolder.iv_reply_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("调整卡片！");
                startPersonalData(reply.getUserid());
            }
        });


        viewHolder.ll_reply_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("发送赞请求");
                Message message = handler.obtainMessage();
                message.what = ReplyActivity.WHAT_REPLEY_UP;
                message.arg1 = position+1;
                handler.sendMessage(message);

            }
        });

        viewHolder.ll_reply_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("发送踩请求");
                Message message = handler.obtainMessage();
                message.what = ReplyActivity.WHAT_REPLEY_DOWN;
                message.arg1 = position+1;
                handler.sendMessage(message);
            }
        });

        viewHolder.iv_reply_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeDialog(position+1);
            }
        });

        return convertView;
    }
    //把条目需要使用到的所有组件封装在这个类中
    class ViewHolder{
        CircleImageView iv_reply_head;
        ImageView iv_reply_sex;
        LinearLayout ll_reply_up;
        LinearLayout ll_reply_down;
        TextView tv_reply_username;
        TextView tv_reply_content;
        TextView tv_reply_time;
        TextView tv_reply_up;
        TextView tv_reply_down;
        ImageView iv_reply_type;

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

    private void typeDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("类型");
        final String[] type = {"待解决", "已解决","删除"};
        builder.setSingleChoiceItems(type, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Message message = handler.obtainMessage();
                message.what = QuestionFragment.WHAT_QUESTION_TYPE;
                message.arg1 = which;
                message.arg2 = position;
                handler.sendMessage(message);
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
