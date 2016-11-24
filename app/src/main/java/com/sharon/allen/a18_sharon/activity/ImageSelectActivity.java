package com.sharon.allen.a18_sharon.activity;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sharon.allen.a18_sharon.R;
import com.sharon.allen.a18_sharon.base.BaseActivity;
import com.sharon.allen.a18_sharon.globle.Constant;
import com.sharon.allen.a18_sharon.utils.LogUtils;
import com.sharon.allen.a18_sharon.utils.MyOkHttp;
import com.sharon.allen.a18_sharon.utils.TimeUtils;
import com.sharon.allen.a18_sharon.utils.ToastUtils;

import java.io.File;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import id.zelory.compressor.Compressor;

/**
 * Created by Allen on 2016/11/16.
 */

public class ImageSelectActivity extends BaseActivity {

    private Button bt_select;
    private Context context;
    private ImageView iv_image_select;
    private Button bt_image_send;
    private String originalPath;
    private MyOkHttp myOkHttp;
    private static final int WHAT_SUCCWSS = 1;

    private String PHOTO_NAME = TimeUtils.getPhotoFileName();
    private File cameraTempFile;
//    private File compressFile = new File(Environment.getExternalStorageDirectory()+Constant.SdCard.SAVE_ROOT_PATH, PHOTO_NAME);

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MyOkHttp.UPLOAD_SUCCESS:
                    ToastUtils.Toast(context,"上传成功");
                break;
            }
        }
    };


    @Override
    public void initView() {
        setContentView(R.layout.activity_selecter);
        bt_select = (Button) findViewById(R.id.bt_select);
        bt_image_send = (Button) findViewById(R.id.bt_image_send);
        iv_image_select = (ImageView) findViewById(R.id.iv_image_select);
    }

    @Override
    public void initListener() {
        bt_select.setOnClickListener(this);
        bt_image_send.setOnClickListener(this);
        iv_image_select.setOnClickListener(this);
    }

    @Override
    public void initData() {
        context = ImageSelectActivity.this;
        myOkHttp = new MyOkHttp();
        //设置主题
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()){
            case R.id.bt_select:
                RxGalleryFinal
                        .with(context)
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
                                    Glide.with(context).load(originalPath).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_image_select);
                                }else {
                                    Glide.with(context).load(originalPath).into(iv_image_select);
                                }
                                LogUtils.i(imageRadioResultEvent.getResult().toString());
                            }
                        })
                        .openGallery();
                break;
            case R.id.bt_image_send:
                cameraTempFile = new File(originalPath);
                myOkHttp.upLoadFile(handler,cameraTempFile,Constant.Server.UPLOAD_IMG_URL);
                break;
        }
    }

    //获取文件后缀
    private String getPhotoSuffix(String path){
        String suffix = path.substring(path.lastIndexOf('.')+1);
        return suffix;
    }
}
