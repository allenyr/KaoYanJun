package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;

import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/16.
 */
public class MoneyStoreActivity extends BaseActivity {


    private Context mContext;
    private static final int PAY = 1;
    private static final int PAY_DONE = 2;
    private static final int WISH = 3;
    private static final int WISH_DONE = 4;
    private UserDataManager userDataManager;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<String> list = new ArrayList<String>();
            switch (msg.what){
                case PAY:
                    list.add(userDataManager.getId()+"");
                    myOkHttp.okhttpGet(handler,PAY_DONE,Constant.Server.GET_PATH,list,26);
//                    MyHttp.creatHttpRequest(handler,PAY_DONE, userDataManager.getId()+"",26);
                    break;
                case PAY_DONE:
                    tv_store_money.setText((String)msg.obj);
                    ToastUtils.Toast(MoneyStoreActivity.this,"提交成功，系统24小时内为你充值");
                    break;
                case WISH:
                    list.add(userDataManager.getId()+"");
                    myOkHttp.okhttpGet(handler,WISH_DONE,Constant.Server.GET_PATH,list,26);
//                    MyHttp.creatHttpRequest(handler,WISH_DONE,userDataManager.getId()+"",26);
                    break;
                case WISH_DONE:
                    tv_store_money.setText((String)msg.obj);
                    ToastUtils.Toast(MoneyStoreActivity.this,"许愿成功");
                    break;
            }
        }
    };
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;
    private TextView tv_store_money;
    private TextView tv_titlebar_title;
    private MyOkHttp myOkHttp;

    @Override
    public void initView() {
        setContentView(R.layout.activity_money_store);
        tv_store_money = (TextView) findViewById(R.id.tv_store_money);

        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        tv_titlebar_title.setText("积分");

        SystemBarTintUtils.setActionBar(this);

    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();
        userDataManager = UserDataManager.getInstance(mContext);
        myOkHttp = new MyOkHttp();
        tv_store_money.setText(userDataManager.getMoney()+"");
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()) {
            case R.id.rl_titlebar_back:
                finish();
                break;
        }
    }
}
