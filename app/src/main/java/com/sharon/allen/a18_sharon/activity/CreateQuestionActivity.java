package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;

import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/10/13.
 */

public class CreateQuestionActivity extends BaseActivity {

    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;
    private Button bt_create_question_send;
    private UserDataManager userDataManager;

    private static final int SUCCESS = 1;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS:
                    ToastUtils.Toast(mContext,(String) msg.obj);
                    finish();
                    break;
            }
        }
    };
    private Context mContext;
    private EditText et_create_question;
    private MyOkHttp myOkHttp;

    @Override
    public void initView() {
        setContentView(R.layout.activity_create_question);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("提问");
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        iv_titlebar_camera.setVisibility(View.GONE);
        bt_create_question_send = (Button) findViewById(R.id.bt_create_question_send);
        et_create_question = (EditText) findViewById(R.id.et_create_question);
        SystemBarTintUtils.setActionBar(this);

    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        bt_create_question_send.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();
        userDataManager = UserDataManager.getInstance(mContext);
        myOkHttp = new MyOkHttp();
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_titlebar_back:
                    finish();
                break;
            case R.id.bt_create_question_send:
                ArrayList<String> list = new ArrayList<String>();
                list.add(userDataManager.getId()+"");
                list.add(userDataManager.getUsername());
                list.add(userDataManager.getHeadUrl());
                list.add(userDataManager.getSex());
                list.add(et_create_question.getText().toString());
                list.add(TimeUtils.getCurrentTime());
                myOkHttp.okhttpGet(handler,SUCCESS, Constant.Server.GET_PATH,list,33);
                break;
        }
    }
}
