package com.sharon.allen.a18_sharon.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.dao.UserSQLite;
import com.sharon.allen.a18_sharon.fragment.MeFragment;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.FileUtils;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.view.CircleImageView.CircleImageView;
import com.sharon.allen.a18_sharon.utils.ImageFactory;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.MySharePreference;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import id.zelory.compressor.Compressor;

/**
 * Created by Allen on 2016/5/27.
 */
public class PersonalSettingActivity extends BaseActivity {

    private boolean mQQUser = false;
    private CircleImageView civ_head;
    private TextView tv_person_username;
    private Button bt_person_logout;
    private UserSQLite userSQLite;
    private TextView tv_person_sex;
    private TextView tv_person_address;
    private TextView tv_person_phonenumber;
    private static final int COMPLETE = 1;
    private static final int ADDRESS = 2;
    private static final int UPDATE = 3;
    private RelativeLayout rl_person_sex;
    private RelativeLayout rl_person_username;
    private RelativeLayout rl_person_password;
    private RelativeLayout rl_person_phonenumber;
    private RelativeLayout rl_person_address;
    private Context mContext;
    private UserDataManager userDataManager;

    private String[] items = { "拍照", "相册" };
    private String title = "选择照片";
    private static final int PHOTO_CARMERA = 4;
    private static final int PHOTO_PICK = 5;
    private static final int PHOTO_CUT = 6;
    private RelativeLayout rl_headtitle_back;
    private Bitmap bitmap;
    private String originalPath;
    private LinearLayout ll_security;
    private TextView tv_person_signa;
    private File imageFile;
    private String photoName = TimeUtils.getPhotoFileName();
    // 创建一个以当前系统时间为名称的文件，保存上传的头像
//    private File tempFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_AVATAR_PATH, photoName);
    // 创建一个以当前系统时间为名称的文件，用于保存拍照的图片(保存到本地)
    private File cameraTempFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_AVATAR_PATH, photoName);
    // 创建一个以当前系统时间为名称的文件，保存压缩后的拍照图片，用于上次服务器（没有保存到本地）
    private File compressFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_ROOT_PATH, photoName);


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE:
                    Toast.makeText(mContext, (String) msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case MyOkHttp.UPLOAD_SUCCESS:
                    userDataManager.setHeadUrl("/head/"+FileUtils.getNameFromPath(imageFile.getPath()));
//                    userDataManager.setHeadUrl("/head/"+imageFile.getPath());
                    //更新服务器数据库
                    updateSQL(userDataManager.getHeadUrl(),12);
                    break;
            }
        }
    };
    private RelativeLayout rl_person_signa;
    private MyOkHttp myOkHttp;

    @Override
    public void initView() {
        //填充布局，返回view对象
        setContentView(R.layout.activity_personal_setting);
        civ_head = (CircleImageView) findViewById(R.id.civ_head);
        tv_person_username = (TextView) findViewById(R.id.tv_person_username);
        tv_person_phonenumber = (TextView) findViewById(R.id.tv_person_phonenumber);
        tv_person_sex = (TextView) findViewById(R.id.tv_person_sex);
        tv_person_address = (TextView) findViewById(R.id.tv_person_address);
        rl_person_username = (RelativeLayout) findViewById(R.id.rl_person_username);
        rl_person_password = (RelativeLayout)findViewById(R.id.rl_person_password);
        rl_person_phonenumber = (RelativeLayout)findViewById(R.id.rl_person_phonenumber);
        rl_person_sex = (RelativeLayout)findViewById(R.id.rl_person_sex);
        rl_person_address = (RelativeLayout)findViewById(R.id.rl_person_address);
        bt_person_logout = (Button) findViewById(R.id.bt_person_logout);
        rl_headtitle_back = (RelativeLayout) findViewById(R.id.rl_headtitle_back);
        rl_headtitle_back.setVisibility(View.VISIBLE);
        ll_security = (LinearLayout) findViewById(R.id.ll_security);
        tv_person_signa = (TextView) findViewById(R.id.tv_person_signa);
        rl_person_signa = (RelativeLayout) findViewById(R.id.rl_person_signa);
        SystemBarTintUtils.setActionBar(this);

    }

    @Override
    public void initListener() {
        bt_person_logout.setOnClickListener(this);
        rl_person_username.setOnClickListener(this);
        rl_person_phonenumber.setOnClickListener(this);
        rl_person_sex.setOnClickListener(this);
        rl_person_address.setOnClickListener(this);
        rl_person_password.setOnClickListener(this);
        civ_head.setOnClickListener(this);
        rl_headtitle_back.setOnClickListener(this);
        rl_person_signa.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = PersonalSettingActivity.this;
        userDataManager = UserDataManager.getInstance(getApplicationContext());
        myOkHttp = new MyOkHttp();
        //获取数据库对象
        userSQLite = UserSQLite.getInstance(getApplicationContext());

        if(MySharePreference.getSP(mContext,"qquser",false)){
            ll_security.setVisibility(View.INVISIBLE);
        }

        Glide.with(mContext).load(Constant.Server.PATH+userDataManager.getHeadUrl()).into(civ_head);
        tv_person_username.setText(userDataManager.getUsername());
        tv_person_sex.setText(userDataManager.getSex());
        tv_person_phonenumber.setText(userDataManager.getPhone());
        tv_person_address.setText(userDataManager.getAddress());
        tv_person_signa.setText(userDataManager.getSigna());

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()){
            case R.id.bt_person_logout:
                MySharePreference.putSP(mContext,"autologin",false);
                MainActivity.InformationInstantStart=false;
                MainActivity.questionInstantStart=false;
                startActivity(new Intent(mContext, LoginActivity.class));
                MainActivity.Main_Activity.finish();
                finish();
                break;
            case R.id.civ_head:
//                Crop.pickImage(PersonalSettingActivity.this);
                RxGalleryFinal
                        .with(mContext)
                        .image()
                        .radio()
                        .crop()
                        .imageLoader(ImageLoaderType.GLIDE)
                        .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                //图片选择结果
                                originalPath = imageRadioResultEvent.getResult().getOriginalPath();
                                Glide.with(mContext).load(originalPath).into(civ_head);
                                imageFile = new File(originalPath);
                                imageFile = Compressor.getDefault(mContext).compressToFile(imageFile);
                                myOkHttp.upLoadFile(handler,imageFile,Constant.Server.UPLOAD_HEAD_URL);
                                LogUtils.i(imageRadioResultEvent.getResult().toString());
                            }
                        })
                        .openGallery();
                break;
            case R.id.rl_person_username:
                usernameDialog();
                break;
            case R.id.rl_person_phonenumber:
                phoneDialog();
                break;
            case R.id.rl_person_sex:
                sexDialog();
                break;
            case R.id.rl_person_address:
                startActivityForResult(new Intent(mContext, GetAddressInfoActivity.class), ADDRESS);
                break;
            case R.id.rl_person_password:
                passwordDialog();
                break;
            case R.id.rl_headtitle_back:
                //返回data
                finish();
                break;
            case R.id.rl_person_signa:
                signaDialog();
                break;
        }
    }

    //各Activity返回，并存储到数据库
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //android-crop相册返回
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        }
        //android-crop剪切返回
        else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        //地址选择器返回
        else if(requestCode == ADDRESS){
            if(resultCode == Activity.RESULT_OK){
                String province = data.getStringExtra("province");
                String city = data.getStringExtra("city");
                if(city == null){
                    city = "";
                }
                String region = province + " " + city;
                tv_person_address.setText(region);
                userDataManager.setAddress(region);
                //更新远程数据库
                updateSQL(region,9);
            }
        }
    }

    private void usernameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettingActivity.this);
//        builder.setIcon(R.drawable.pass);
        builder.setTitle("修改昵称");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting_username, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText et_dialog_username = (EditText)view.findViewById(R.id.et_dialog_username);
        et_dialog_username.setHint("新昵称");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String newUsername = et_dialog_username.getText().toString();
                tv_person_username.setText(newUsername);
                userDataManager.setUsername(newUsername);
                updateSQL(newUsername,23);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }

    private void sexDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettingActivity.this);
        builder.setTitle("性别");
        final String[] sex1 = {"男", "女"};
        builder.setSingleChoiceItems(sex1, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String sex = sex1[which];
                tv_person_sex.setText(sex);
                userDataManager.setSex(sex);
                updateSQL(sex,8);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void signaDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettingActivity.this);
        builder.setTitle("个性签名");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting_username, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText et_dialog_username = (EditText)view.findViewById(R.id.et_dialog_username);
        et_dialog_username.setHint("介绍一下自己吧");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String newSigna = et_dialog_username.getText().toString();
                tv_person_signa.setText(newSigna);
                userDataManager.setSigna(newSigna);
                updateSQL(newSigna,28);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }

    private void phoneDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettingActivity.this);
//        builder.setIcon(R.drawable.pass);
        builder.setTitle("修改手机号码");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText et_dialog_phone = (EditText)view.findViewById(R.id.et_dialog_phone);
        final EditText et_dialog_password = (EditText)view.findViewById(R.id.et_dialog_password);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String newPhone = et_dialog_phone.getText().toString();
                String passwordTemp = et_dialog_password.getText().toString();
                et_dialog_phone.setHint("新的手机");
                et_dialog_password.setHint("密码");

                if (passwordTemp.equals(userDataManager.getPassword())){
                    if (newPhone.equals("")){
                        Toast.makeText(mContext,"手机号码不能为空",Toast.LENGTH_SHORT).show();
                    }
                    else if (newPhone.length() != 11){
                        Toast.makeText(mContext,"手机号码不正确",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        tv_person_phonenumber.setText(newPhone);
                        userDataManager.setPhone(newPhone);
                        updateSQL(newPhone,10);
                    }

                }
                else {
                    Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }

    private void passwordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalSettingActivity.this);
//        builder.setIcon(R.drawable.pass);
        builder.setTitle("修改密码");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_setting, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText et_dialog_phone = (EditText)view.findViewById(R.id.et_dialog_phone);
        final EditText et_dialog_password = (EditText)view.findViewById(R.id.et_dialog_password);
        et_dialog_phone.setHint("新的密码");
        et_dialog_password.setHint("原始密码");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String newPassword = et_dialog_phone.getText().toString();
                String passwordTemp = et_dialog_password.getText().toString();

                if (passwordTemp.equals(userDataManager.getPassword())){
                    if (newPassword.equals("")){
                        Toast.makeText(mContext, "密码不能为空",Toast.LENGTH_SHORT).show();
                    }
                    else if (newPassword.length() < 6 ||newPassword.length() > 16){
                        Toast.makeText(mContext, "密码长度为6~16",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        userDataManager.setPassword(newPassword);
                        updateSQL(newPassword,11);
                    }

                }
                else {
                    Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }

    //SQL更新
    public void updateSQL(String newData,int what){

        ArrayList<String> list = new ArrayList<String>();
        list.add(userDataManager.getId()+"");
        list.add(newData);
        myOkHttp.okhttpGet(handler,UPDATE,Constant.Server.GET_PATH,list,what);

//        MyHttp.creatHttpRequest(handler,UPDATE,userDataManager.getId()+"",newData,what);
    }

    //第三方剪切
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    //第三方剪切
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            ContentResolver contentResolver = this.getContentResolver();
            Uri originalUri  = Crop.getOutput(result);
            try {
                FileUtils.inputstreamtofile(contentResolver.openInputStream(originalUri),cameraTempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            compressFile = Compressor.getDefault(this).compressToFile(cameraTempFile);
            civ_head.setImageBitmap(BitmapFactory.decodeFile(compressFile.getPath()));

            //上传头像
            myOkHttp.upLoadFile(handler,compressFile,Constant.Server.UPLOAD_HEAD_URL);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
