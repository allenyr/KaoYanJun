package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.Version;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */

public class CheckUpdateActivity extends BaseActivity {

    private TextView tv_check_update_current;
    private TextView tv_check_update_new;
    private PackageManager packageManager;
    private PackageInfo packageInfo;
    private MyOkHttp myOkHttp;
    private String jsonData;
    private static final int WHAT_GET_UPDATE = 1;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_GET_UPDATE:
                    jsonData = (String) msg.obj;
                    pullVersion(jsonData);
                    break;
            }
        }
    };
    private String versionName;
    private int versionCode;
    private String versionDes;
    private String versionDownLoadUrl;
    private Context mContext;
    private RelativeLayout rl_about_back;

    @Override
    public void initView() {
        setContentView(R.layout.activity_check_update);
        rl_about_back = (RelativeLayout) findViewById(R.id.rl_about_back);
        tv_check_update_current = (TextView) findViewById(R.id.tv_check_update_current);
        tv_check_update_new = (TextView) findViewById(R.id.tv_check_update_new);
        tv_check_update_current.setText("当前版本:V"+getVersinName());
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        rl_about_back.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = CheckUpdateActivity.this;
        myOkHttp = new MyOkHttp();
        getVersion();
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_about_back:
                finish();
                break;
        }
    }

    //获取版本信息
    public void getVersion(){
        ArrayList<String> list = new ArrayList<String>();
        list = null;
        myOkHttp.okhttpGet(handler,WHAT_GET_UPDATE, Constant.Server.GET_PATH,list,14);

    }

    //获取版本号
    private int getVersionCode(){
        //包管理器
        packageManager = getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //获取版本名
    private String getVersinName(){
        //包管理器
        packageManager = getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    //检测更新
    public void pullVersion(final String jsonData) {
        Gson gson = new Gson();
        try {
            List<Version> versionList = gson.fromJson(jsonData, new TypeToken<List<Version>>() {
            }.getType());
            for (Version version : versionList) {
                versionName = version.getVersionName();
                versionCode = version.getVersionCode();
                versionDes = version.getDes();
                versionDownLoadUrl = version.getDownLoadUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_check_update_new.setText("最新版本:V"+versionName);
        if (getVersionCode() < versionCode){
            tv_check_update_new.setTextColor(getResources().getColor(R.color.colorRed));
        }else {
            tv_check_update_new.setTextColor(getResources().getColor(R.color.colorTextBlack));
        }
    }
}
