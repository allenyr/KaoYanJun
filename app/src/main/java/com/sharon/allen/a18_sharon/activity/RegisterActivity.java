package com.sharon.allen.a18_sharon.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.MobSmsUtil;

import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.util.ArrayList;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/9/3.
 */
public class RegisterActivity extends BaseActivity {

    private EditText et_phone;
    private EditText et_code;
    private EditText et_password;
    private Button bt_send;
    private Button bt_register;
    private Context mContext;
    private static final int HANDLER_REGISTER = 11;
    private static final int HANDLER_DELAY_TIME = 12;
    private static final int HANDLER_UPDATE_PASSWORD = 13;
    private static final int HANDLER_RECALL = 14;
    private int mDelayedTime;
    private boolean mReCall = true;
    private int mWhat;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private AlertDialog dialog;
    private UserDataManager userDataManager;
    private MyOkHttp myOkHttp;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == HANDLER_REGISTER){
//                ToastUtils.Toast(getApplicationContext(), (String) msg.obj);
                if("注册成功".equals((String) msg.obj)){
                    ToastUtils.Toast(getApplicationContext(),"注册成功");
                    //返回data
                    Intent intent = new Intent();
                    intent.putExtra("phone",userDataManager.getPhone());
                    intent.putExtra("password",userDataManager.getPassword());
                    setResult(1,intent);
                    dialog.dismiss();
                    finish();
                }else {
                    ToastUtils.Toast(getApplicationContext(), (String) msg.obj);
                    dialog.dismiss();
                }
            }else if(msg.what == HANDLER_UPDATE_PASSWORD){
                ToastUtils.Toast(getApplicationContext(), (String) msg.obj);
                dialog.dismiss();
                if("修改成功".equals((String) msg.obj)){
                    //返回data
                    Intent intent = new Intent();
                    intent.putExtra("phone",userDataManager.getPhone());
                    intent.putExtra("password",userDataManager.getPassword());
                    setResult(1,intent);
                    finish();
                }
            } else if(msg.what == HANDLER_DELAY_TIME){
                if(mDelayedTime > 0){
                    bt_send.setText("重新发送("+mDelayedTime+")");
                }else {
                    bt_send.setEnabled(true);
                    bt_send.setBackground(getResources().getDrawable(R.color.colorGreen));
                    bt_send.setText("发送验证码");
                }
            }else if(msg.what == HANDLER_RECALL){
                mReCall = true;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=============================" + event);
                Log.e("result", "result=============================" + result);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //验证成功
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//                        ToastUtils.Toast(getApplicationContext(),"验证码正确，正在为你注册...");
                        userDataManager.setUsername(et_phone.getText().toString());
                        userDataManager.setPassword(et_password.getText().toString());
                        userDataManager.setPhone(et_phone.getText().toString());
                        switch (mWhat){
                            case 1:
                                if(mReCall == true){
                                    mReCall = false;
                                    setRegister();
                                    handler.sendEmptyMessageDelayed(HANDLER_RECALL,3000);
                                }
                                break;
                            case 2:
                                if(mReCall == true){
                                    mReCall = false;
                                    setPassword();
                                    handler.sendEmptyMessageDelayed(HANDLER_RECALL,3000);
                                }
                                break;
                            default:
                                break;
                        }
                        //请求成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                        ToastUtils.Toast(getApplicationContext(),"验证码已经发送");

                    } else {
                        ToastUtils.Toast(getApplicationContext(),"验证码请求错误");
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };
    private ImageView iv_titlebar_camera;
    private TextView tv_dialog_title;


    @Override
    public void initView() {
        setContentView(R.layout.activity_register_phone);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_register = (Button) findViewById(R.id.bt_register);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        rl_titlebar_back.setVisibility(View.VISIBLE);

        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        et_phone.addTextChangedListener(mPhoneWatcher);
        bt_send.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        rl_titlebar_back.setOnClickListener(this);
    }

    @Override
    public void initData() {
        userDataManager = UserDataManager.getInstance(RegisterActivity.this);
        //---------------------------------------------------------------------
        //步骤1：初始化短信SDK
        SMSSDK.initSDK(this,"16c865dcff460","de62bea5f889819b95060440d32905ee");
        //步骤2：设置EventHandler
        EventHandler eventHandler = new EventHandler(){
            //在操作结束的时候被触发
            //event表示操作的类型，result表示操作的结果，data表示操作返回的数据
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //步骤3：注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

        mContext = RegisterActivity.this;
        myOkHttp = new MyOkHttp();
        Bundle bundle = this.getIntent().getExtras();
        mWhat = bundle.getInt("what");
        switch (mWhat){
            case 1:
                tv_titlebar_title.setText("注册");
                bt_register.setText("注册");
                break;
            case 2:
                tv_titlebar_title.setText("修改密码");
                bt_register.setText("确定");
                break;
        }
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.bt_send:
                String phone = et_phone.getText().toString();
                if(MobSmsUtil.judgePhoneNums(phone)){
                    SMSSDK.getVerificationCode("86", phone);
                    delayedSendSMS(60);
                }else {
                    ToastUtils.Toast(getApplicationContext(),"手机号码输入有误！");
                }
                break;
            case R.id.bt_register:
                loadingDialog();
                SMSSDK.submitVerificationCode("86", et_phone.getText().toString() , et_code.getText().toString());
                break;
            case R.id.rl_titlebar_back:
//                ToastUtils.Toast(getApplicationContext(),"返回");
                finish();
                break;
        }
    }

    //EdiText侦听
    private TextWatcher mPhoneWatcher = new TextWatcher(){
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            if(!et_phone.getText().toString().equals("")){
                bt_send.setEnabled(true);
                bt_send.setBackground(getResources().getDrawable(R.color.colorGreen));
            }else {
                bt_send.setEnabled(false);
                bt_send.setBackground(getResources().getDrawable(R.color.colorTextHint));
            }
        }
    };

    //注册判断
    public boolean judgeAssign(String username,String password){

        if (username.length() < 2 ||username.length() > 16){
            Toast.makeText(RegisterActivity.this, "用户名长度为2~16",Toast.LENGTH_SHORT).show();
        }
        else if (password.equals("")){
            Toast.makeText(RegisterActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6 ||password.length() > 16){
            Toast.makeText(RegisterActivity.this, "密码长度为6~16",Toast.LENGTH_SHORT).show();
        }

        else {
            return true;
        }
        return false;
    }

    //注册
    public void setRegister(){
        userDataManager.setHeadUrl("/head/head_default.png");
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getPassword());
        list.add(userDataManager.getHeadUrl());
        list.add(userDataManager.getPhone());
        myOkHttp.okhttpGet(handler,HANDLER_REGISTER, Constant.Server.GET_PATH,list,2);

    }

    //修改密码
    public void setPassword(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getPhone());
        list.add(userDataManager.getPassword());
        myOkHttp.okhttpGet(handler,HANDLER_UPDATE_PASSWORD, Constant.Server.GET_PATH,list,22);
    }

    //
    public void delayedSendSMS(int delayedTime){
        mDelayedTime = delayedTime;
        bt_send.setEnabled(false);
        bt_send.setBackground(getResources().getDrawable(R.color.colorTextHint));

        Thread thread = new Thread(){
            @Override
            public void run() {
                while (mDelayedTime > 0){
                    mDelayedTime--;
                    handler.sendEmptyMessageDelayed(HANDLER_DELAY_TIME,1000);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        thread.start();
    }

    private void loadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        builder.setCancelable(false);
        tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
        switch (mWhat){
            case 1:
                tv_dialog_title.setText("正在注册");
                break;
            case 2:
                tv_dialog_title.setText("正在修改");
                break;
        }
        dialog = builder.show();
    }
}
