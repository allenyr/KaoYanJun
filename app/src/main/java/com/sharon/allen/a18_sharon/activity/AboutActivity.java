package com.sharon.allen.a18_sharon.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;


/**
 * Created by Administrator on 2016/8/14.
 */
public class AboutActivity extends BaseActivity {
    
    private RelativeLayout rl_about_back;
    private PackageManager packageManager;
    private PackageInfo packageInfo;
    private LinearLayout ll_about_group1;
    private LinearLayout ll_about_group2;
    private ImageView iv_about_qr_code;
    private Context mContext;
    private MyOkHttp myOkHttp;
    private String downloadPath = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MyOkHttp.DOWNLOAD_SUCCESS:
                    ToastUtils.Toast(mContext,"保存成功,存放在"+msg.obj);
                    break;
            }
        }
    };

    @Override
    public void initView() {
        setContentView(R.layout.activity_about);
        rl_about_back = (RelativeLayout) findViewById(R.id.rl_about_back);
        ll_about_group1 = (LinearLayout)findViewById(R.id.ll_about_group1);
        ll_about_group2 = (LinearLayout)findViewById(R.id.ll_about_group2);
        iv_about_qr_code = (ImageView)findViewById(R.id.iv_about_qr_code);
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        rl_about_back.setOnClickListener(this);
        ll_about_group1.setOnClickListener(this);
        ll_about_group2.setOnClickListener(this);
        iv_about_qr_code.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                confirmDialog();
                return true;
            }
        });
    }

    @Override
    public void initData() {
        mContext = AboutActivity.this;
        myOkHttp = new MyOkHttp();
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.rl_about_back:
                finish();
                break;
            case R.id.ll_about_group1:
                joinQQGroup("8EoVfetrX7x6uKsPAwGsKykJiXg5IyE8");
                break;
            case R.id.ll_about_group2:
                joinQQGroup("XJOmMSI8vQmeSgPFlMUYt9eZW2YURl9i");
                break;
        }
    }

    /**
     * 获取版本号
     * @return
     */
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

    /****************
     *
     * 发起添加群流程。
     * 群号：16/17广东工业大学考研(22789307) 的 key 为： 8EoVfetrX7x6uKsPAwGsKykJiXg5IyE8
     * 群号：2017广东工业大学考研(566010949) 的 key 为： XJOmMSI8vQmeSgPFlMUYt9eZW2YURl9i
     * 调用 joinQQGroup(8EoVfetrX7x6uKsPAwGsKykJiXg5IyE8) 即可发起手Q客户端申请加群 16/17广东工业大学考研(22789307)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    //确认下载弹窗
    protected void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否保存二维码？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                myOkHttp.downLoadFile(Constant.Server.PATH+"/img/logo_wechat_kyj.png",downloadPath,"kaoyanjun_wechat_qr_code.png");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
