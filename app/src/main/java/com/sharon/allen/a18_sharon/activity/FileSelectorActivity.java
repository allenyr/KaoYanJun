package com.sharon.allen.a18_sharon.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;

/**
 * Created by Administrator on 2016/9/28.
 */

public class FileSelectorActivity extends BaseActivity {

    private Button bt_file_selector;
    private TextView tv_file_path;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;

    @Override
    public void initView() {
        setContentView(R.layout.activity_file_selector);
        bt_file_selector = (Button) findViewById(R.id.bt_file_selector);
        tv_file_path = (TextView) findViewById(R.id.tv_file_path);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("上传文件");
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        bt_file_selector.setOnClickListener(this);
        rl_titlebar_back.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_titlebar_back:
                finish();
                break;
            case R.id.bt_file_selector:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1100);
                break;
        }
    }

    //  选择完毕后在onActivityResult方法中回调     从data中拿到文件路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (resultCode == Activity.RESULT_OK)
        {
            Uri uri = data.getData();
            tv_file_path.setText(uri.toString());
        }
    }
}
