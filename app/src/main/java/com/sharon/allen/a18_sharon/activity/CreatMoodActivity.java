package com.sharon.allen.a18_sharon.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.dao.UserSQLite;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.DialogUtils;
import com.sharon.allen.a18_sharon.utils.FileUtils;
import com.sharon.allen.a18_sharon.utils.ImageFactory;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.SystemBarTintUtils;
import com.sharon.allen.a18_sharon.utils.TimeUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import id.zelory.compressor.Compressor;

/**
 * Created by Allen on 2016/5/28.
 */
public class CreatMoodActivity extends BaseActivity {

    private String[] items = { "拍照", "相册" };
    private String title = "选择照片";
    private static final int PHOTO_CARMERA = 1;
    private static final int PHOTO_PICK = 2;
    private static final int PHOTO_CUT = 3;
    private static final int WHAT_PUBLIC_DONE = 5;
    private static final int WHAT_UPDATE_ID = 6;

    private String PHOTO_NAME = TimeUtils.getPhotoFileName();
    private String PHOTO_NAME_NO_SUFFIX = TimeUtils.getPhotoFileNameNoSuffix();

    // 创建一个以当前系统时间为名称的文件，用于保存拍照的图片(保存到本地)
    private File cameraTempFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_IMAGE_PATH, PHOTO_NAME);
    // 创建一个以当前系统时间为名称的文件，保存压缩后的拍照图片，用于上次服务器（没有保存到本地）
    private File compressFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_ROOT_PATH, PHOTO_NAME);

    private File imageFile;

    private Button bt_creatitem_public;
    private EditText et_creatitem_mood;
    private ImageView iv_creatitem_pic;
    private UserSQLite userSQLite;

    private String time;
    private String mood;
    private String imageurl;
    private TextView tv_titlebar_title;
    private RelativeLayout rl_titlebar_back;
    private ImageView iv_titlebar_camera;
    private Context mContext;
    private UserDataManager userDataManager;

    private AlertDialog dialog;
    private MyOkHttp myOkHttp;
    private String originalPath;
    private ImageView iv_create_item_select;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_PUBLIC_DONE:
                    Toast.makeText(mContext,"发送成功",Toast.LENGTH_SHORT).show();
                    userDataManager.setMoney(userDataManager.getMoney()+5);
                    dialog.dismiss();
                    finish();
                    break;
                case MyOkHttp.UPLOAD_SUCCESS:
                    //上传心情
                    publicMood();
                    break;
                case 404:
                    Toast.makeText(CreatMoodActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };

    @Override
    public void initView() {
        setContentView(R.layout.activity_create_item);
        bt_creatitem_public = (Button) findViewById(R.id.bt_creatitem_public);
        et_creatitem_mood = (EditText) findViewById(R.id.et_creatitem_mood);
        iv_creatitem_pic = (ImageView) findViewById(R.id.iv_creatitem_pic);
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        rl_titlebar_back = (RelativeLayout) findViewById(R.id.rl_titlebar_back);
        iv_titlebar_camera = (ImageView) findViewById(R.id.iv_titlebar_camera);
        iv_create_item_select = (ImageView) findViewById(R.id.iv_create_item_select);

        tv_titlebar_title.setText("动态");
        iv_titlebar_camera.setVisibility(View.INVISIBLE);
        rl_titlebar_back.setVisibility(View.VISIBLE);
        SystemBarTintUtils.setActionBar(this);
    }

    @Override
    public void initListener() {
        bt_creatitem_public.setOnClickListener(this);
        rl_titlebar_back.setOnClickListener(this);
        iv_create_item_select.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mContext = getApplicationContext();
        userDataManager = UserDataManager.getInstance(mContext);
        myOkHttp = new MyOkHttp();
        //获取数据库对象
        userSQLite = UserSQLite.getInstance(getApplicationContext());
//        获取用户名和头像地址
//        queryNameAndHeadurlfromSQL();
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.iv_create_item_select:
                RxGalleryFinal
                        .with(mContext)
                        .image()
                        .radio()
//                        .crop()
                        .imageLoader(ImageLoaderType.GLIDE)
                        .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                //图片选择结果
                                originalPath = imageRadioResultEvent.getResult().getOriginalPath();
                                if (getPhotoSuffix(originalPath).equals("gif")){
                                    Glide.with(mContext).load(originalPath).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_creatitem_pic);
//                                    Glide.with(mContext).load(originalPath).into(iv_creatitem_pic);
                                    imageFile = new File(originalPath);
                                }else {
                                    Glide.with(mContext).load(originalPath).into(iv_creatitem_pic);
                                    imageFile = new File(originalPath);
                                    imageFile = Compressor.getDefault(mContext).compressToFile(imageFile);
                                }
                                LogUtils.i(imageRadioResultEvent.getResult().toString());
                            }
                        })
                        .openGallery();
                break;
            case R.id.bt_creatitem_public:
                if(!et_creatitem_mood.getText().toString().equals("")){
                    if(iv_creatitem_pic.getDrawable() != null){
                        loadingDialog();
                        LogUtils.i(imageFile.getPath());
                        myOkHttp.upLoadFile(handler,imageFile,Constant.Server.UPLOAD_IMG_URL);
                    }
                    else {
                        Toast.makeText(CreatMoodActivity.this,"请添加图片",Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(CreatMoodActivity.this,"请输入文字",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_titlebar_back:
                finish();
                break;
        }
    }

//    private android.content.DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
//
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            switch (which) {
//                case 0:
//                    // 调用拍照
//                    startCamera(dialog);
//                    break;
//                case 1:
//                    // 调用相册
//                    startPick(dialog);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    };

//    // 调用系统相机(适配Android7.0)
//    protected void startCamera(DialogInterface dialog) {
//        dialog.dismiss();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//            Uri imageUri = FileProvider.getUriForFile(mContext, "com.sharon.fileprovider", cameraTempFile);//通过FileProvider创建一个content类型的Uri
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
//            startActivityForResult(intent,PHOTO_CARMERA);
//        }else {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra("camerasensortype", 2); // 调用前置摄像头
//            intent.putExtra("autofocus", true); // 自动对焦
//            intent.putExtra("fullScreen", false); // 全屏
//            intent.putExtra("showActionIcons", false);
//            // 指定调用相机拍照后照片的存储路径
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraTempFile));
//            startActivityForResult(intent,PHOTO_CARMERA);
//        }
//    }

    //剪切
    protected void startPick(DialogInterface dialog) {
        dialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PHOTO_PICK);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            //相机
//            case PHOTO_CARMERA:
//                try {
//                    compressFile = Compressor.getDefault(this).compressToFile(cameraTempFile);
//                    iv_creatitem_pic.setImageBitmap(BitmapFactory.decodeFile(compressFile.getPath()));
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            //相册
//            case PHOTO_PICK:
//                if(data != null) {
//                    try {
//                        ContentResolver contentResolver = this.getContentResolver();
//                        Uri originalUri  = data.getData();
//                        FileUtils.inputstreamtofile(contentResolver.openInputStream(originalUri),cameraTempFile);
//                        compressFile = Compressor.getDefault(this).compressToFile(cameraTempFile);
//                        iv_creatitem_pic.setImageBitmap(BitmapFactory.decodeFile(compressFile.getPath()));
////                        Glide.with(mContext).load(compressFile).into(iv_creatitem_pic);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    //提交心情
    public void publicMood(){
        mood = et_creatitem_mood.getText().toString();
        imageurl = "/img/"+FileUtils.getNameFromPath(imageFile.getPath());
        time = TimeUtils.getCurrentTime();

        ArrayList<String> list = new ArrayList<String>();
        list.add(mood);
        list.add(userDataManager.getUsername());
        list.add(userDataManager.getHeadUrl());
        list.add(imageurl);
        list.add(time);
        list.add(userDataManager.getId()+"");
        list.add(userDataManager.getSex());
        myOkHttp.okhttpGet(handler,WHAT_PUBLIC_DONE,Constant.Server.GET_PATH,list,5);
    }

    private void loadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatMoodActivity.this);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        TextView tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText("正在上传");
        dialog = builder.show();

    }

    //获取文件后缀
    private String getPhotoSuffix(String path){
        String suffix = path.substring(path.lastIndexOf('.')+1);
        return suffix;
    }
}
