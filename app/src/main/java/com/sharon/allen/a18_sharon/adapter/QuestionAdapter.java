package com.sharon.allen.a18_sharon.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.activity.PersonalActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.fragment.QuestionFragment;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Question;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.ShareSdkUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;
import com.sharon.allen.a18_sharon.view.CircleImageView.CircleImageView;
import com.sharon.allen.a18_sharon.view.ImageButton;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class QuestionAdapter extends RecyclerView.Adapter {

    List<Question> questionList;
    private Context context;
    private Handler handler;
    private LayoutInflater inflater;
    private UserDataManager userDataManager;

    public QuestionAdapter(Context context, List<Question> questionList,Handler handler,UserDataManager userDataManager){
        this.context = context;
        this.questionList = questionList;
        this.inflater = LayoutInflater.from(context);
        this.handler = handler;
        this.userDataManager = userDataManager;
    }

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

    //外部调用，listview改变通知刷新adapter
    public void refresh(List<Question> list) {
        questionList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final QuestionViewHolder viewHolder = (QuestionViewHolder) holder;
        viewHolder.position = position;
        final Question question = questionList.get(position);
        viewHolder.tv_question_content.setText(question.getQuestion());
        Glide.with(context).load(Constant.Server.PATH+question.getHeadurl()).into(viewHolder.civ_question_head);
        viewHolder.tv_question_username.setText(question.getUsername());
        viewHolder.tv_question_replycount.setText(question.getReplycount()+"");
        if(question.getSex().equals("男")){
            viewHolder.iv_question_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if (question.getSex().equals("女")){
            viewHolder.iv_question_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            viewHolder.iv_question_sex.setVisibility(View.GONE);
        }
        LogUtils.i("hotItem.getSex()="+question.getSex());

        if(question.getType().equals("0")){
            viewHolder.iv_question_type.setUnsolved();
        }else if(question.getType().equals("1")){
            viewHolder.iv_question_type.setsolved();
        }else {
            viewHolder.iv_question_type.setVisibility(View.GONE);
        }
        //显示时间
        String time = null;
        try {
            time = TimeUtils.getTimeGap(question.getTime(),TimeUtils.getCurrentTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        viewHolder.tv_question_time.setText(time);
        //查看资料卡
        viewHolder.civ_question_head.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                LogUtils.i("显示卡片！");
                startPersonalData(question.getUserid(), viewHolder.civ_question_head);
            }
        });
        //修改类型
        if (userDataManager.getId() == question.getUserid()||userDataManager.getId() == 61) {
            viewHolder.iv_question_type.setEnable(context,true);
        }
        viewHolder.iv_question_type.setOnStateListener(new ImageButton.OnStateListener() {
            @Override
            public void onState(View view, String state) {
                Message message = handler.obtainMessage();
                message.what = QuestionFragment.WHAT_QUESTION_TYPE;
                if (state.equals("未解决")) {
                    message.arg1 = 0;
                } else if (state.equals("已解决")) {
                    message.arg1 = 1;
                } else {
                    message.arg1 = 2;
                }
                message.arg2 = position;
                handler.sendMessage(message);
                ToastUtils.Toast(context,message.arg1);
            }
        });
        //分享
        viewHolder.iv_question_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSdkUtils.showShare(context,"考研君", Constant.Server.APP_STORE_URL,question.getQuestion(),userDataManager,handler,1);
            }
        });
        //底部判断
        if(position == questionList.size()-1){
            LogUtils.i("position"+position);
            onRecyclerViewListener.onLoadMoreData(position);
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
            size = questionList.size();
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CircleImageView civ_question_head;
        public TextView tv_question_username;
        public TextView tv_question_content;
        public TextView tv_question_time;
        public ImageView iv_question_sex;
        public TextView tv_question_replycount;
        public TextView iv_question_type;
        public ImageView iv_question_share;
        public View rootView;
        public int position;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            civ_question_head = (CircleImageView) itemView.findViewById(R.id.civ_question_head);
            tv_question_username = (TextView) itemView.findViewById(R.id.tv_question_username);
            tv_question_time = (TextView) itemView.findViewById(R.id.tv_question_time);
            tv_question_content = (TextView) itemView.findViewById(R.id.tv_question_content);
            tv_question_replycount = (TextView) itemView.findViewById(R.id.tv_question_replycount);
            iv_question_sex = (ImageView) itemView.findViewById(R.id.iv_question_sex);
            iv_question_type = (TextView) itemView.findViewById(R.id.iv_question_type);
            iv_question_share = (ImageView) itemView.findViewById(R.id.iv_question_share);
            rootView = itemView.findViewById(R.id.rootview_question);
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

    //跳转到PersonalActivity
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startPersonalData(int userid, View view){
        Intent intent=new Intent();
        intent.setClass(context, PersonalActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userid);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context,view,"share_head").toBundle());
        }else {
            context.startActivity(intent);
        }
    }

    private void typeDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("类型");
        final String[] type = {"待解决", "已解决","删除"};
        builder.setSingleChoiceItems(type, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int type)
            {
                Message message = handler.obtainMessage();
                message.what = QuestionFragment.WHAT_QUESTION_TYPE;
                message.arg1 = type;
                message.arg2 = position;
                handler.sendMessage(message);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void typeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("类型");
        final String[] type = {"未解决", "已解决","删除"};
        builder.setSingleChoiceItems(type, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int type)
            {
                switch (type){
                    case 0:
                        setUnsolved();

                        break;
                    case 1:
                        setsolved();
                        break;
                    case 2:
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
