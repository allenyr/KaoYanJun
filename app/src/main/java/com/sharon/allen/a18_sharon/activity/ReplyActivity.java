package com.sharon.allen.a18_sharon.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.adapter.ReplyAdapter;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.fragment.QuestionFragment;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Reply;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */

public class ReplyActivity extends BaseActivity {

    private Context mContext;
    private String mHeadUrl;
    private String mUserName;
    private String mTime;
    private String mQuestion;
    private int mReceiverId;
    private int mQuestionId;
    private String mSex;
    private String mType;
    private String mReply;
    private int mReplyId;

    private ImageView civ_reply_activity_head;
    private TextView tv_reply_activity_username;
    private TextView tv_reply_activity_time;
    private TextView tv_reply_activity_content;
    private ImageView iv_reply_activity_sex;
    private TextView iv_reply_activity_type;

    private List<Reply> mReplyList = new ArrayList<>();
    private ReplyAdapter replyAdapter;
    private UserDataManager userDataManager;

    private static final int WHAT_UPLOAD_REPLY = 1;
    private static final int WHAT_PUBLIC_REPLY = 2;
    public static final int WHAT_REPLEY_UP = 3;
    public static final int WHAT_REPLEY_UP_COMPLETE = 4;
    public static final int WHAT_REPLEY_DOWN = 5;
    public static final int WHAT_REPLEY_DOWN_COMPLETE = 6;
    public static final int WHAT_QUESTION_TYPE_COMPLETE = 7;
    public static final int WHAT_QUESTION_DELETE_COMPLETE = 8;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<String> list = new ArrayList<String>();
            switch (msg.what){
                case WHAT_UPLOAD_REPLY:
                    mReplyList = pullJson((String) msg.obj);
                    replyAdapter.refresh(mReplyList);
                    break;
                //评论成功
                case WHAT_PUBLIC_REPLY:
                    mReplyList =  pullJson((String) msg.obj);
                    replyAdapter.refresh(mReplyList);
                    userDataManager.setMoney(userDataManager.getMoney()+1);
                    ToastUtils.Toast(getApplicationContext(),"评论成功！");
                    break;
                case WHAT_REPLEY_UP:
                    mReplyId = msg.arg1;
                    if(!MySharePreference.getSP(mContext, mQuestionId +" "+ mReplyId+" "+userDataManager.getId(),false)){
                        MySharePreference.putSP(mContext, mQuestionId +" "+ mReplyId+" "+userDataManager.getId(),true);
                        list.add(mQuestionId+"");
                        list.add(mReplyId+"");
                        list.add(mReplyList.get(mReplyId-1).getUserid()+"");
                        list.add(userDataManager.getId()+"");
                        list.add(userDataManager.getUsername());
                        list.add(userDataManager.getHeadUrl());
                        list.add(userDataManager.getSex());
                        list.add(mType);
                        list.add(TimeUtils.getCurrentTime());
                        myOkHttp.okhttpGet(handler,WHAT_REPLEY_UP_COMPLETE,Constant.Server.GET_PATH,list,38);
                    }else {
                        ToastUtils.Toast(mContext,"已投");
                    }

                    break;
                case WHAT_REPLEY_UP_COMPLETE:
                    mReplyList =  pullJson((String) msg.obj);
                    replyAdapter.refresh(mReplyList);
                    break;
                case WHAT_REPLEY_DOWN:
                    mReplyId = msg.arg1;
                    if(!MySharePreference.getSP(mContext, mQuestionId +" "+ mReplyId+" "+userDataManager.getId(),false)) {
                        MySharePreference.putSP(mContext, mQuestionId + " " + mReplyId + " " + userDataManager.getId(), true);
                        list.add(mQuestionId+"");
                        list.add(mReplyId+"");
                        myOkHttp.okhttpGet(handler,WHAT_REPLEY_DOWN_COMPLETE,Constant.Server.GET_PATH,list,39);
                    }else {
                        ToastUtils.Toast(mContext,"已投");
                    }
                    break;
                case WHAT_REPLEY_DOWN_COMPLETE:
                    mReplyList =  pullJson((String) msg.obj);
                    replyAdapter.refresh(mReplyList);
                    break;

                case WHAT_QUESTION_TYPE_COMPLETE:

                    ToastUtils.Toast(mContext,"修改成功");
                    break;
                case WHAT_QUESTION_DELETE_COMPLETE:
                    finish();
                    break;

            }
        }
    };
    private EditText et_reply_activity_txt;
    private Button bt_reply_activity_send;
    private ListView lv_reply_activity_list;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;
    private MyOkHttp myOkHttp;

    @Override
    public void initView() {
        setContentView(R.layout.activity_reply);
        civ_reply_activity_head = (ImageView) findViewById(R.id.civ_reply_activity_head);
        tv_reply_activity_username = (TextView) findViewById(R.id.tv_reply_activity_username);
        tv_reply_activity_time = (TextView) findViewById(R.id.tv_reply_activity_time);
        tv_reply_activity_content = (TextView) findViewById(R.id.tv_reply_activity_content);
        iv_reply_activity_sex = (ImageView) findViewById(R.id.iv_reply_activity_sex);
        iv_reply_activity_type = (TextView) findViewById(R.id.iv_reply_activity_type);
        iv_reply_activity_type.setVisibility(View.VISIBLE);
        et_reply_activity_txt = (EditText) findViewById(R.id.et_reply_activity_txt);
        bt_reply_activity_send = (Button) findViewById(R.id.bt_reply_activity_send);
        lv_reply_activity_list = (ListView) findViewById(R.id.lv_reply_activity_list);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        tv_titlebar_title.setText("解答");
        rl_titlebar_back.setVisibility(View.VISIBLE);
        iv_titlebar_camera.setVisibility(View.GONE);

        SystemBarTintUtils.setActionBar(this);

    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        bt_reply_activity_send.setOnClickListener(this);
        civ_reply_activity_head.setOnClickListener(this);
        iv_reply_activity_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDataManager.getId() == mReceiverId||userDataManager.getId() == 377){
                    typeDialog();
                }
            }
        });
    }

    @Override
    public void initData() {
        mContext = ReplyActivity.this;
        userDataManager = UserDataManager.getInstance(getApplicationContext());
        myOkHttp = new MyOkHttp();
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收imageurl值
        mHeadUrl = bundle.getString("headurl");
        mUserName = bundle.getString("username");
        mTime = bundle.getString("time");
        mQuestion = bundle.getString("question");
        mReceiverId = bundle.getInt("userid");
        mQuestionId = bundle.getInt("id");
        mSex = bundle.getString("sex");
        mType = bundle.getString("type");

        if(mSex.equals("男")){
            iv_reply_activity_sex.setBackgroundResource(R.drawable.ico_sex_male);
        }else if (mSex.equals("女")){
            iv_reply_activity_sex.setBackgroundResource(R.drawable.ico_sex_female);
        }else {
            iv_reply_activity_sex.setVisibility(View.GONE);
        }

        if(mType.equals("0")){
            iv_reply_activity_type.setText("未解決");
            iv_reply_activity_type.setBackgroundResource(R.drawable.shape_circle_red_bg);
        }else if(mType.equals("1")){
            iv_reply_activity_type.setText("已解決");
            iv_reply_activity_type.setBackgroundResource(R.drawable.shape_circle_green_bg);
        }else {
            iv_reply_activity_type.setVisibility(View.GONE);
        }

        Glide.with(this).load(Constant.Server.PATH+ mHeadUrl).into(civ_reply_activity_head);
        tv_reply_activity_username.setText(mUserName);
        tv_reply_activity_time.setText(mTime);
        tv_reply_activity_content.setText(mQuestion);
        //显示时间
        try {
            tv_reply_activity_time.setText(TimeUtils.getTimeGap(mTime,TimeUtils.getCurrentTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        replyAdapter = new ReplyAdapter(mContext,mReplyList,handler);
        lv_reply_activity_list.setAdapter(replyAdapter);
        //获取回复条目
        getCommentList(mQuestionId +"");
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.civ_reply_activity_head:
                startPersonalData(mReceiverId);
                break;
            case R.id.bt_reply_activity_send:
                mReply = et_reply_activity_txt.getText().toString();
                if(!mReply.equals("")&&mReply != null){
                    publicReply(mReply);
                    et_reply_activity_txt.setText("");
                }
                break;
            case R.id.rl_titlebar_back:
                finish();
                break;
        }
    }

    //获取回答列表
    public void getCommentList(String id){
        ArrayList<String> list = new ArrayList<String>();
        list.add(id);
        myOkHttp.okhttpGet(handler,WHAT_UPLOAD_REPLY, Constant.Server.GET_PATH,list,36);
    }

    //解析Json数据
    private List<Reply> pullJson(String jsonData) {
        try{
            Gson gson = new Gson();
            return gson.fromJson(jsonData, new TypeToken<List<Reply>>() {}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //评论
    public void publicReply(String reply){
        mTime = TimeUtils.getCurrentTime();

        ArrayList<String> list = new ArrayList<String>();
        list.add(mQuestionId +"");
        list.add(mReceiverId +"");
        list.add(userDataManager.getId()+"");
        list.add(userDataManager.getUsername());
        list.add(userDataManager.getHeadUrl());
        list.add(userDataManager.getSex());
        list.add(mTime);
        list.add(reply);
        list.add(0+"");
        list.add(0+"");
        myOkHttp.okhttpGet(handler,WHAT_PUBLIC_REPLY,Constant.Server.GET_PATH,list,37);

    }

    //跳转到CommentActivity
    private void startPersonalData(int userid){
        Intent intent=new Intent();
        intent.setClass(mContext, PersonalActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userid);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void typeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("类型");
        final String[] type = {"待解决", "已解决","删除","取消"};
        builder.setSingleChoiceItems(type, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int type)
            {
                ArrayList<String> okhttpParamList = new ArrayList<String>();
                switch (type){
                    case 0:
                        iv_reply_activity_type.setText("未解決");
                        iv_reply_activity_type.setBackgroundResource(R.drawable.shape_circle_red_bg);
                        okhttpParamList.add(type+"");
                        okhttpParamList.add(mQuestionId+"");
                        myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE,Constant.Server.GET_PATH,okhttpParamList,40);
                        break;
                    case 1:
                        iv_reply_activity_type.setText("已解決");
                        iv_reply_activity_type.setBackgroundResource(R.drawable.shape_circle_green_bg);
                        okhttpParamList.add(type+"");
                        okhttpParamList.add(mQuestionId+"");
                        myOkHttp.okhttpGet(handler,WHAT_QUESTION_TYPE_COMPLETE,Constant.Server.GET_PATH,okhttpParamList,40);
                        break;
                    case 2:
                        okhttpParamList.add(mQuestionId+"");
                        myOkHttp.okhttpGet(handler,WHAT_QUESTION_DELETE_COMPLETE,Constant.Server.GET_PATH,okhttpParamList,41);
                        break;
                    case 3:

                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
