package com.sharon.allen.a18_sharon.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.tools.utils.UIHandler;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.dao.UserSQLite;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.User;

import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyJPush;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

/**
 * Created by Allen on 2016/5/27.
 */
public class LoginActivity extends BaseActivity implements Handler.Callback,PlatformActionListener {

    private Context mContext;
    private boolean mQQUser = false;
    public static final int LOGIN = 1;
    private static final int LOGIN_SUCCESS = 3;

    private EditText username_et;
    private EditText password_et;
    private Button login_bt;
    private TextView forgot_tv;
    private TextView register_tv;
    private UserSQLite userSQLite;
    private ImageView iv_qqLogin;
    private String jsonData;
    private AlertDialog dialog;
    private static final int REQUESTCODE = 1;
    private static final int RESULTCODE = 1;
    private static final int QQ_LOGIN = 2;
    private static final int QQ_LOGIN_DONE = 1;
    private static final int QQ_LOGIN_ERROR = 2;
    private static final int QQ_LOGIN_CANCLE = 3;
    private UserDataManager userDataManager;
    private List<User> userList = new ArrayList<User>();
    private MyOkHttp myOkHttp;
    private String phoneName = TimeUtils.getPhotoFileName();
    private File tempFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_AVATAR_PATH,phoneName);
    private String avatarDir = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_AVATAR_PATH;

    //主线程handle
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOGIN:
                    if("登录成功".equals((String) msg.obj)){
                        //查询服务器个人数据
                        getPersonDataFromServer(handler,userDataManager.getPhone());
                    }else {
                        dialog.dismiss();
                        MySharePreference.putSP(mContext,"autologin",false);
                        Toast.makeText(mContext, (String) msg.obj,Toast.LENGTH_SHORT).show();
                    }
                    break;

                //查询到服务器的个人数据
                case LOGIN_SUCCESS:
//                    SQLiteDatabase sqLiteDatabase = userSQLite.getWritableDatabase();
                    jsonData = (String) msg.obj;
                    if (jsonData.equals("assign")){
                        LogUtils.i("注册");
                        //下载头像到本地
                        myOkHttp.downLoadFile(mQQAvatarUrl,avatarDir,phoneName);
                        //注册或者登陆
                        register();
                    }else {
                        jsonData = (String) msg.obj;
                        MySharePreference.putSP(mContext,"personalCache",jsonData);
                        userList = pullJson(jsonData);
                        LogUtils.i("userList="+userList.toString());
                        saveUserData(userList);
//                        personInfoSaveToSQLite(jsonData,sqLiteDatabase,mContext);
                        dialog.dismiss();
                        MySharePreference.putSP(mContext,"autologin",true);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    break;
                //网络连接失败
                case 404:
                    dialog.dismiss();
                    Toast.makeText(mContext, "连接失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private String mQQAvatarUrl;

    //ShareSDK_Handle
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.arg1) {
            case QQ_LOGIN_DONE: {
                // 成功
//                Toast.makeText(mContext, "QQ授权成功", Toast.LENGTH_SHORT).show();
                ArrayList<String> list = new ArrayList<String>();
                list.add(userDataManager.getUsername());
                list.add("");
                userDataManager.setHeadUrl("/head/"+phoneName);
                list.add(userDataManager.getHeadUrl());
                list.add(userDataManager.getPhone());
                list.add(userDataManager.getSex());
                list.add(userDataManager.getAddress());
                myOkHttp.okhttpGet(handler,LOGIN_SUCCESS,Constant.Server.GET_PATH,list,47);
                loadingDialog();
            }
            break;

            case QQ_LOGIN_ERROR: {
                // 失败
                Toast.makeText(mContext, "失败", Toast.LENGTH_SHORT).show();

            }
            break;
            case QQ_LOGIN_CANCLE: {
                // 取消
                Toast.makeText(LoginActivity.this, "取消", Toast.LENGTH_SHORT)
                        .show();
            }
            break;
        }
        return false;
    }

    @Override
    public void initView() {
        ShareSDK.initSDK(this);
        setContentView(R.layout.activity_login);
        //拿到布局中的组件
        username_et = (EditText) findViewById(R.id.username_et);
        password_et = (EditText) findViewById(R.id.password_et);
        login_bt = (Button) findViewById(R.id.login_bt);
        forgot_tv = (TextView) findViewById(R.id.forgot_tv);
        register_tv = (TextView) findViewById(R.id.register_tv);
        iv_qqLogin = (ImageView)findViewById(R.id.iv_qqLogin);

        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        login_bt.setOnClickListener(this);
        register_tv.setOnClickListener(this);
        forgot_tv.setOnClickListener(this);
        iv_qqLogin.setOnClickListener(this);
    }

    @Override
    public void initData() {

        mContext = getApplicationContext();
        userDataManager = UserDataManager.getInstance(mContext);
        myOkHttp = new MyOkHttp();
        myOkHttp.setOnOkhttpListener(new MyOkHttp.OnOkhttpListener() {
            @Override
            public void onProgress(int progress) {
                LogUtils.i("下载中progress="+progress);
            }

            @Override
            public void onSuccess(String msg) {
                LogUtils.i("下载完成,存放在"+msg);
                //下载的头像更新服务器
                myOkHttp.upLoadFile(handler,tempFile,Constant.Server.UPLOAD_HEAD_URL);
            }

            @Override
            public void onError(String msg) {
                LogUtils.i("下载出错"+msg);
            }
        });
        if(!MySharePreference.getSP(mContext,"qquser",false)){
            username_et.setText(MySharePreference.getSP(mContext,"phone",""));
            password_et.setText(MySharePreference.getSP(mContext,"password",""));
        }
        //新建数据库
        userSQLite = UserSQLite.getInstance(mContext);
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.login_bt:
                //登录
                mQQUser = false;
                MySharePreference.putSP(mContext,"qquser",mQQUser);
                loadingDialog();
                onLogin();
                break;
            case R.id.register_tv:
                //注册
                startRegister(1,REQUESTCODE);
                break;
            case R.id.forgot_tv:
                startRegister(2,REQUESTCODE);
                break;
            case R.id.iv_qqLogin:
                mQQUser = true;
                MySharePreference.putSP(mContext,"qquser",mQQUser);
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.SSOSetting(false); // 设置false表示使用SSO授权方式
                qq.setPlatformActionListener(this); // 设置分享事件回调
                if (qq.isValid()) {
                    qq.removeAccount(true);
                }
                if (qq.isClientValid()) {
                    System.out.println("安装了QQ");
                } else {
                    System.out.println("没有安装了QQ");

                }
                qq.showUser(null);// 获取到用户信息
                // qq.authorize();//只是单独授权登录
                break;
        }
    }

    private void loadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        TextView tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText("正在登录");
        builder.setCancelable(false);
        dialog = builder.show();

    }

    // 回调
    @Override
    public void onCancel(Platform platform, int action) {
        Message msg = new Message();
        msg.what = QQ_LOGIN;
        msg.arg1 = QQ_LOGIN_CANCLE;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.what = QQ_LOGIN;
        msg.arg1 = QQ_LOGIN_DONE;
        msg.arg2 = action;
        msg.obj = platform;
        LogUtils.i(res.toString());
        LogUtils.i("nickname==="+res.get("nickname"));
        userDataManager.setUsername(platform.getDb().getUserName());
        userDataManager.setPhone(platform.getDb().getUserId());
        userDataManager.setSex((String)res.get("gender"));
        userDataManager.setAddress(res.get("province")+" "+res.get("city"));
        mQQAvatarUrl = (String) res.get("figureurl_qq_2");
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        t.printStackTrace();

        Message msg = new Message();
        msg.what = QQ_LOGIN;
        msg.arg1 = QQ_LOGIN_ERROR;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);
        // 分享失败的统计
        ShareSDK.logDemoEvent(4, platform);
    }

    //接收上一个activity返回数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESTCODE:
                if(resultCode == RESULTCODE){
                    String phone = data.getStringExtra("phone");
                    String password = data.getStringExtra("password");
                    username_et.setText(phone);
                    password_et.setText(password);
                }
                break;
        }
    }

    //登陆
    public void onLogin(){
        userDataManager.setPhone(username_et.getText().toString());
        userDataManager.setPassword(password_et.getText().toString());

        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getPhone());
        list.add(userDataManager.getPassword());
        myOkHttp.okhttpGet(handler,LOGIN,Constant.Server.GET_PATH,list,1);
    }

    //查询MYSQL获取个人信息
    public void getPersonDataFromServer(Handler handler,String username){
        ArrayList<String> list = new ArrayList<String>();
        list.add(username);
        myOkHttp.okhttpGet(handler,LOGIN_SUCCESS,Constant.Server.GET_PATH,list,3);

    }

    public void register(){
        //注册或者登陆
        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getUsername());
        list.add("");
        list.add(userDataManager.getHeadUrl());
        list.add(userDataManager.getPhone());
        list.add(userDataManager.getSex());
        list.add(userDataManager.getAddress());
        myOkHttp.okhttpGet(handler,LOGIN_SUCCESS,Constant.Server.GET_PATH,list,47);
    }

    private List<User> pullJson(String jsonData) {
        if (jsonData!=null&&!jsonData.isEmpty()){
            try {
                Gson gson = new Gson();
                return gson.fromJson(jsonData, new TypeToken<List<User>>() {}.getType());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private void saveUserData(List<User> list){
        for(User user:list) {
            userDataManager.setId(user.getId());
            userDataManager.setUsername(user.getUsername());
            userDataManager.setPassword(user.getPassword());
            userDataManager.setHeadUrl(user.getHeadurl());
            userDataManager.setPhone(user.getPhone());
            userDataManager.setSex(user.getSex());
            userDataManager.setAddress(user.getAddress());
            userDataManager.setMoney(user.getMoney());
            userDataManager.setSigna(user.getSigna());
            userDataManager.setSigna(user.getSigna());
            userDataManager.setMessagenum(user.getMessagenum());
        }
    }

    //JSON数据存储到本机sql
    public void personInfoSaveToSQLite(String jsonData,SQLiteDatabase db,Context context){
        Gson gson = new Gson();
        List<User> userList1 = gson.fromJson(jsonData,new TypeToken<List<User>>(){}.getType());
        for(User user:userList1){
            int id = user.getId();
            String username = user.getUsername();
            String password = user.getPassword();
            String headurl = user.getHeadurl();
            String phone = user.getPhone();
            String sex = user.getSex();
            String address = user.getAddress();
            int money = user.getMoney();
            String signa = user.getSigna();
            int messagenum =user.getMessagenum();
            userDataManager.setId(user.getId());
            userDataManager.setUsername(user.getUsername());
            userDataManager.setPassword(user.getPassword());
            userDataManager.setHeadUrl(user.getHeadurl());
            userDataManager.setPhone(user.getPhone());
            userDataManager.setSex(user.getSex());
            userDataManager.setAddress(user.getAddress());
            userDataManager.setMoney(user.getMoney());
            userDataManager.setSigna(user.getSigna());
            userDataManager.setSigna(user.getSigna());
            userDataManager.setMessagenum(user.getMessagenum());
            //查找数据库有没有这个用户名
            Cursor cursor = db.rawQuery("select * from person where id=?",new String[]{id+""});
            if (cursor.moveToNext()) {
                //如果数据库有这个用户名，则更新数据库
                db.execSQL("update person set username = ?,password = ?,headurl = ?,phone = ?,sex = ?,address = ?,money = ?,signa = ?,messagenum = ? where id = ?",new Object[]{username,password, headurl, phone, sex, address,money,signa,messagenum,id});
                Log.i("登录界面","有这个用户名"+id);
            }else{
                //如果数据库没有这个用户名，则插入到数据库
                db.execSQL("insert into person(id,username,password,headurl,phone,sex,address,money,signa,messagenum) values(?,?,?,?,?,?,?,?,?,?)",new Object[]{id,username, password, headurl, phone, sex, address,money,signa,messagenum});
                Log.i("登录界面","没有这个用户名"+id);
            }
            MyJPush.setAlias(context,id+"");
        }
        db.close();
    }

    //跳转到注册页面
    public void startRegister(int what,int requestCode){
        Intent intent=new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("what",what);
        intent.putExtras(bundle);
        startActivityForResult(intent,requestCode);
    }

}
