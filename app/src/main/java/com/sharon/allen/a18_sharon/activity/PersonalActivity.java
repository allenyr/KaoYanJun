package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.FileUtils;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;
import com.sharon.allen.a18_sharon.view.CircleImageView.CircleImageView;
import com.sharon.allen.a18_sharon.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 */
public class PersonalActivity extends BaseActivity {

    private CircleImageView civ_head;
    private TextView tv_person_username;
    private TextView tv_person_sex;
    private TextView tv_person_address;
    private int mId;
    private String jsonData;
    private MyOkHttp myOkHttp;
    private static final int WHAT_PERSONAL_DATA = 1;
    private static final int WHAT_PUBLIC_COMMENT = 2;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_PERSONAL_DATA:
                    jsonData = (String) msg.obj;
                    pullPersonalDatafromJson(jsonData);
                    break;
                case WHAT_PUBLIC_COMMENT:
                    ToastUtils.Toast(mContext,"发送成功");
                    break;
            }
        }
    };
    private RelativeLayout rl_headtitle_back;
    private TextView tv_person_signa;
    private EditText et_personal_chat;
    private Button bt_personal_public;
    private String mTime;
    private String mType;
    private int mReceiveridId;
    private UserDataManager userDataManager;
    private String receiveridHeadurl;
    private Context mContext;
    private File downloadHeadTemp;

    @Override
    public void initView() {

        setContentView(R.layout.activity_personal);
        civ_head = (CircleImageView) findViewById(R.id.civ_head);
        tv_person_username = (TextView) findViewById(R.id.tv_person_username);
        tv_person_sex = (TextView) findViewById(R.id.tv_person_sex);
        tv_person_address = (TextView) findViewById(R.id.tv_person_address);
        rl_headtitle_back = (RelativeLayout) findViewById(R.id.rl_headtitle_back);
        rl_headtitle_back.setVisibility(View.VISIBLE);
        tv_person_signa = (TextView) findViewById(R.id.tv_person_signa);

        et_personal_chat = (EditText) findViewById(R.id.et_personal_chat);
        bt_personal_public = (Button) findViewById(R.id.bt_personal_public);

        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        rl_headtitle_back.setOnClickListener(this);
        bt_personal_public.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = PersonalActivity.this;
        myOkHttp = new MyOkHttp();
        myOkHttp.setOnOkhttpListener(new MyOkHttp.OnOkhttpListener() {
            @Override
            public void onProgress(int progress) {
                LogUtils.i("加载");
            }

            @Override
            public void onSuccess(String msg) {
                Glide.with(mContext).load(downloadHeadTemp).into(civ_head);
            }

            @Override
            public void onError(String msg) {

            }
        });
        userDataManager = UserDataManager.getInstance(getApplicationContext());
        Bundle bundle = this.getIntent().getExtras();
        mId = bundle.getInt("userid");
        getPersonInformation(handler, mId);
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_headtitle_back:
                finish();
                break;
            case R.id.bt_personal_public:
                String chat = et_personal_chat.getText().toString();
                et_personal_chat.setText("");
                if (chat!=null&&!chat.equals("")){
                    privateChat(chat);
                }
                break;
        }
    }

    //查询MYSQL获取个人信息
    public void getPersonInformation(Handler handler, int id){
        ArrayList<String> list = new ArrayList<String>();
        list.add(id+"");
        myOkHttp.okhttpGet(handler,WHAT_PERSONAL_DATA,Constant.Server.GET_PATH,list,27);
    }

    //JSON数据存储到本机sql
    public  void pullPersonalDatafromJson(String jsonData){
        Gson gson = new Gson();
        List<User> userList1 = gson.fromJson(jsonData,new TypeToken<List<User>>(){}.getType());
        for(User user:userList1) {
            int id = user.getId();
            String username = user.getUsername();
            String password = user.getPassword();
            receiveridHeadurl = user.getHeadurl();
            String phone = user.getPhone();
            String sex = user.getSex();
            String address = user.getAddress();
            int money = user.getMoney();
            String signa = user.getSigna();

            downloadHeadTemp = new File(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_AVATAR_PATH, FileUtils.getNameFromPath(Constant.Server.PATH+receiveridHeadurl));
            if(!downloadHeadTemp.exists()) {
                LogUtils.i("头像不存在");
                myOkHttp.downLoadFile(Constant.Server.PATH+receiveridHeadurl,Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_AVATAR_PATH, FileUtils.getNameFromPath(Constant.Server.PATH+receiveridHeadurl));
            }else {
                LogUtils.i("头像存在");
                Glide.with(mContext).load(downloadHeadTemp).into(civ_head);
            }
//            Glide.with(PersonalActivity.this).load(Constant.Server.PATH+receiveridHeadurl).into(civ_head);

            tv_person_username.setText(username);
            tv_person_sex.setText(sex);
            tv_person_address.setText(address);
            tv_person_signa.setText(signa);

        }
    }

    //私聊
    public void privateChat(String chat){
        mTime = TimeUtils.getCurrentTime();
        //消息类型：
        // 1：回复发帖人，2：回复评论人，3：系统
        mType = "1";
        mReceiveridId = mId;
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getId()+"");
        list.add(mReceiveridId +"");
        list.add(userDataManager.getUsername());
        list.add(userDataManager.getHeadUrl());
        list.add(userDataManager.getSex());
        list.add(mType);
        list.add(chat);
        list.add(mTime);
        list.add(receiveridHeadurl);
        myOkHttp.okhttpGet(handler,WHAT_PUBLIC_COMMENT,Constant.Server.GET_PATH,list,46);
    }
}
