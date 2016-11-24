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
 * Created by Administrator on 2016/10/27.
 */

public class FeedbackActivity extends BaseActivity {

    private Button bt_feedback_send;
    private EditText et_feedback;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;
    private MyOkHttp myOkHttp;
    private Context mContext;
    private UserDataManager userDataManager;
    private static final int WHAT_FEEDBACK = 1;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_FEEDBACK:
                    ToastUtils.Toast(mContext, (String) msg.obj);
                    break;
            }
        }
    };

    @Override
    public void initView() {
        setContentView(R.layout.activity_feedback);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("反馈");
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        iv_titlebar_camera.setVisibility(View.GONE);

        bt_feedback_send = (Button) findViewById(R.id.bt_feedback_send);
        et_feedback = (EditText) findViewById(R.id.et_feedback);

        SystemBarTintUtils.setActionBar(this);

    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        bt_feedback_send.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();
        myOkHttp = new MyOkHttp();
        userDataManager = UserDataManager.getInstance(mContext);
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_titlebar_back:
                finish();
                break;
            case R.id.bt_feedback_send:
                String temp = et_feedback.getText().toString();
                et_feedback.setText("");
                ArrayList<String> list = new ArrayList<String>();
                list.add(userDataManager.getId()+"");
                list.add(temp);
                myOkHttp.okhttpGet(handler,WHAT_FEEDBACK, Constant.Server.GET_PATH,list,13);
                break;
        }
    }
}
