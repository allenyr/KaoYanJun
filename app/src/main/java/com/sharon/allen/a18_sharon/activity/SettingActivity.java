package com.sharon.allen.a18_sharon.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.utils.AnimationUtils;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/10/26.
 */

public class SettingActivity extends BaseActivity {

    private LinearLayout ll_setting_app_store;
    private Context mContext;
    private LinearLayout ll_setting_add_our;
    private LinearLayout ll_setting_update;
    private LinearLayout ll_setting_feedback;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setting);


        ll_setting_add_our = (LinearLayout) findViewById(R.id.ll_setting_add_our);
        ll_setting_update = (LinearLayout) findViewById(R.id.ll_setting_update);
        ll_setting_feedback = (LinearLayout) findViewById(R.id.ll_setting_feedback);
        ll_setting_app_store = (LinearLayout) findViewById(R.id.ll_setting_app_store);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        tv_titlebar_title.setText("设置");
        rl_titlebar_back.setVisibility(View.VISIBLE);
        iv_titlebar_camera.setVisibility(View.GONE);

        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        rl_titlebar_back.setOnClickListener(this);
        ll_setting_add_our.setOnClickListener(this);
        ll_setting_update.setOnClickListener(this);
        ll_setting_feedback.setOnClickListener(this);
        ll_setting_app_store.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();

    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_titlebar_back:
                finish();
                break;
            case R.id.ll_setting_add_our:
                startActivity(new Intent(mContext,AboutActivity.class));
                break;
            case R.id.ll_setting_update:
                startActivity(new Intent(mContext, CheckUpdateActivity.class));
                break;
            case R.id.ll_setting_feedback:
                startActivity(new Intent(mContext,FeedbackActivity.class));
                break;
            case R.id.ll_setting_app_store:
                startAppStore();
//                String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kaoyanjun.apk";
//                installApkForN(localPath);
                break;
        }
    }

    //打开应用市场评价
    private void startAppStore(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName()); //传递包名，让市场接收
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //安装的方法
    private void installApkForN(String filePath) {
        Intent intent = new Intent();
        File file = new File(filePath);
        Uri photoURI = FileProvider.getUriForFile(mContext, "com.sharon.fileprovider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
        startActivityForResult(intent,0);

    }
}
