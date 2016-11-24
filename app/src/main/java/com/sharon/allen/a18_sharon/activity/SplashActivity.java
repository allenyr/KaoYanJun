package com.sharon.allen.a18_sharon.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.dao.UserSQLite;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.model.HotItem;
import com.sharon.allen.a18_sharon.model.User;
import com.sharon.allen.a18_sharon.model.Version;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyJPush;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class SplashActivity extends BaseActivity {

    private File dir;
    private PackageManager packageManager;
    private PackageInfo packageInfo;
    private String versionName;
    private int versionCode;
    private String versionDes;
    private String versionDownLoadUrl;
    private UserSQLite userSQLite;
    private List<User> userList = new ArrayList<User>();

    public static final int LOGIN = 1;
    public static final int VERSION = 2;
    private  static final int UPDATE_COUNT = 3;
    private  static final int CODE_UPDATE_DIALOG = 3;
    private  static final int CODE_AUTO_LOGIN = 4;
    private  static final int AUTO_LOGIN_SUCCESS = 5;
    private  static final int SPLASH_TIME = 2000;
    private  static final int CODE_NETWORK_ERROR = 404;

    private  static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH;
    private  static final String APK_DIR = Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH;
    private  static final String APK_NAME = "kaoyanjun.apk";
    private Context mContext;
    private UserDataManager userDataManager;
    private String jsonData;
    private MyOkHttp myOkHttp;
    private ImageView iv_shaple_img;
    private NumberProgressBar pb_splash_progress;
    private ProgressBar pb_splash_loading;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOGIN:
                    if("登录成功".equals((String) msg.obj)){
                        //查询服务器个人数据
                        queryPersonInfoFromServer(userDataManager.getPhone());
                    }
                    else{
                        ToastUtils.Toast(mContext,(String) msg.obj);
                        MySharePreference.putSP(mContext,"autologin",false);
                        enterLoginActivity();
                    }
                    break;
                //检测是否更新
                case VERSION:
                    jsonData = (String) msg.obj;
                    checkVersionUpdate(jsonData);
                    break;
                //弹更新窗口
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                //进入主界面
                case CODE_AUTO_LOGIN:
                    autoLogin();
                    break;
                //个人数据
                case AUTO_LOGIN_SUCCESS:
                    SQLiteDatabase sqLiteDatabase = userSQLite.getWritableDatabase();
                    jsonData = (String) msg.obj;

                    MySharePreference.putSP(mContext,"personalCache",jsonData);
                    userList = pullJson(jsonData);
                    saveuserData(userList);

                    personInfoSaveToSQLite(jsonData,sqLiteDatabase,mContext);
                    enterMainActivity();
                    break;
                //网络异常
                case CODE_NETWORK_ERROR:
                    Toast.makeText(mContext,"网络异常",Toast.LENGTH_SHORT).show();
                    if (MySharePreference.getSP(mContext,"autologin",false)){
                        enterMainActivity();
                    }else {
                        enterLoginActivity();
                    }
                    break;
                default:
                    enterLoginActivity();
                    break;
            }
        }
    };


    @Override
    public void initView() {
        setContentView(R.layout.activity_splash);
        pb_splash_progress = (NumberProgressBar)findViewById(R.id.pb_splash_progress);
        pb_splash_loading = (ProgressBar)findViewById(R.id.pb_splash_loading);
        iv_shaple_img = (ImageView) findViewById(R.id.iv_shaple_img);
        // 让图片铺满屏幕
        hideSystemUI(iv_shaple_img);
        Glide.with(this).load(R.mipmap.shaple_img).into(iv_shaple_img);
    }

    @Override
    public void initListener() {
        iv_shaple_img.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();
        //获取数据库对象
        userSQLite = UserSQLite.getInstance(mContext);
        //获取用户管理对象
        userDataManager = UserDataManager.getInstance(mContext);
        //okhttp
        myOkHttp = new MyOkHttp();
        myOkHttp.setOnOkhttpListener(new MyOkHttp.OnOkhttpListener() {
            @Override
            public void onProgress(int progress) {
                pb_splash_progress.setProgress(progress);
            }

            @Override
            public void onSuccess(String msg) {
                pb_splash_progress.setVisibility(View.INVISIBLE);
                installApk(APK_DIR+APK_NAME);
            }
            @Override
            public void onError(String msg) {
                pb_splash_progress.setVisibility(View.INVISIBLE);
            }
        });
        //获取权限
        requestMultiplePermissions();
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.iv_shaple_img:
                hideSystemUI(iv_shaple_img);
                break;
        }
    }

    //隐藏SystemBar
    private void hideSystemUI(ImageView imageView) {
        imageView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    //检测是否提示更新
    public boolean checkVersionUpdateAgain(){
        int useCount =MySharePreference.getSP(mContext,"useCount",UPDATE_COUNT);
        if(useCount >= UPDATE_COUNT) {
            return true;
        }
        else {
            return false;
        }
    }

    //获取版本信息
    public void requestVersionMessage(){
        ArrayList<String> list = new ArrayList<String>();
        list = null;
        myOkHttp.okhttpGet(handler,VERSION,Constant.Server.GET_PATH,list,14);
    }

    //查询MYSQL获取个人信息
    public void queryPersonInfoFromServer(String username){
        ArrayList list = new ArrayList();
        list.add(username);
        myOkHttp.postAsynHttp(handler,AUTO_LOGIN_SUCCESS,Constant.Server.GET_PATH,list,3);
    }

    //检测更新
    public void checkVersionUpdate(final String jsonData){
        Gson gson = new Gson();
        try {
            List<Version> versionList = gson.fromJson(jsonData,new TypeToken<List<Version>>(){}.getType());
            for(Version version:versionList) {
                versionName = version.getVersionName();
                versionCode = version.getVersionCode();
                versionDes = version.getDes();
                versionDownLoadUrl = version.getDownLoadUrl();
                LogUtils.i("versionCode="+versionCode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(getmVersionCode() < versionCode){
            System.out.println("有更新");
            if(checkVersionUpdateAgain()){
                handler.sendEmptyMessage(CODE_UPDATE_DIALOG);
            }else {
                //记录登录的次数
                int useCount = MySharePreference.getSP(mContext,"useCount",0);
                MySharePreference.putSP(mContext,"useCount",++useCount);
                handler.sendEmptyMessageDelayed(CODE_AUTO_LOGIN,SPLASH_TIME);
            }
        }else {
            System.out.println("无更新");
            handler.sendEmptyMessageDelayed(CODE_AUTO_LOGIN,SPLASH_TIME);
        }
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

    //获取版Code
    private int getmVersionCode(){
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

    private void showUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本:"+ versionName);
        builder.setMessage(versionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载安装包
                pb_splash_progress.setVisibility(View.VISIBLE);
                pb_splash_loading.setVisibility(View.INVISIBLE);
                myOkHttp.downLoadFile(versionDownLoadUrl,APK_DIR,APK_NAME);
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //登录次数
                MySharePreference.putSP(mContext,"useCount",0);
                //自动登录
                handler.sendEmptyMessageDelayed(CODE_AUTO_LOGIN,SPLASH_TIME);
            }
        });

        //用户取消弹窗的监听，比如返回键或者点空白
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //自动登录
                handler.sendEmptyMessageDelayed(CODE_AUTO_LOGIN,SPLASH_TIME);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public void enterMainActivity(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    public void enterLoginActivity(){
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    //安装的方法
    private void installApk(String filePath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent();
            File file = new File(filePath);
            Uri photoURI = FileProvider.getUriForFile(mContext, "com.sharon.allen.a18_sharon.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
            startActivityForResult(intent,0);
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            startActivityForResult(intent,0);
        }


    }

    //自动登录
    private void autoLogin(){
//        SharedPreferences sharedPreferences = getSharedPreferences("info",MODE_PRIVATE);
//        boolean autoLoginBit = sharedPreferences.getBoolean("autologin",false);
        boolean autoLoginBit = MySharePreference.getSP(mContext,"autologin",false);
        if(autoLoginBit){
            ArrayList<String> list = new ArrayList<String>();
            list.add(userDataManager.getPhone());
            list.add(userDataManager.getPassword());
            myOkHttp.okhttpGet(handler,LOGIN,Constant.Server.GET_PATH,list,1);
        }
        else {
            enterLoginActivity();
        }
    }

    //用户取消安装，会回调此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        autoLogin();
    }

    //创建自己的文件夹
    private void createDirectory(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                // 目录路径
                dir = new File(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_ROOT_PATH);
                createdir(dir);
                dir = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_IMAGE_PATH);
                createdir(dir);
                dir = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_AVATAR_PATH);
                createdir(dir);
                dir = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_LIKE_PATH);
                createdir(dir);
                dir = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_DOWNLOAD_PATH);
                createdir(dir);
                dir = new File(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_CACHE_PATH);
                createdir(dir);
                createFile(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_CACHE_PATH,"friendCircle.txt");
                createFile(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_CACHE_PATH,"question.txt");
                createFile(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_CACHE_PATH,"information.txt");
                copyAssetsToSD(DOWNLOAD_PATH);
            }
        };
        thread.start();
    }

    private void createdir(File dir){
        if (!dir.exists()) {// 如果不存在，则创建路径名
            if (dir.mkdirs()) {// 创建该路径名，返回true则表示创建成功
                LogUtils.i("创建目录"+dir.getAbsolutePath()+"成功");
            } else {
                LogUtils.i("创建目录"+dir.getAbsolutePath()+"失败");
            }
        }
        else {
            LogUtils.i("目录"+dir.getAbsolutePath()+"存在");
        }
    }

    public void createFile(String filePath,String fileName) {
        File file = new File(filePath,fileName);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

        }
    }

    //请求SD卡权限
    final public static int PERMISSION_REQUEST_CODE = 100;
    private void requestMultiplePermissions() {

        //判断版本(6.0以上需要权限申请)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //权限项
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            //检测权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }else {
                splashInit();
            }
        }else {
            splashInit();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以去放肆了。
//                    ToastUtils.Toast(mContext,"授权成功");
                    LogUtils.i("授权成功");
                } else {
                    // 权限被用户拒绝了，洗洗睡吧。
                    ToastUtils.Toast(mContext,"授权失败");
                }
                //进入初始化
                splashInit();
                return;
            }
        }
    }

    //极光推送初始化和检测版本
    private void splashInit(){
        // 创建目录
        createDirectory();
        //极光推送
//        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //获取RegistrationID
        LogUtils.i("RegistrationID="+JPushInterface.getRegistrationID(this));
//        setNotification();
        //检测版本
        if (Constant.Server.LOCAL){
//            enterMainActivity();
            requestVersionMessage();
        }else {
            requestVersionMessage();
        }
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

    private void saveuserData(List<User> list){
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

    //通知栏样式
    private void setNotification(){
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        Notification notification=builder.setContentTitle("考研君")
                .setContentText("hello world")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.drawable.ico_notification)
                .setColor(Color.parseColor("#000000"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ico_logo_ffcc00))
                .setContentIntent(PendingIntent.getActivities(this,0x0001,new Intent[]{new Intent(this,MainActivity.class)},PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        notificationManager.notify(1,notification);
    }
    private void copyAssetsToSD(String filePath){
        InputStream inputStream;
        try {
            inputStream = mContext.getResources().getAssets().open("share_logo.png");
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdirs();
                }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "share_logo.png");
            byte[] buffer = new byte[512];
            int count = 0;
            while((count = inputStream.read(buffer)) > 0){
                fileOutputStream.write(buffer, 0 ,count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            System.out.println("success");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isExist(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }
}

