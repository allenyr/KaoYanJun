package com.sharon.allen.a18_sharon.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.sharon.allen.a18_sharon.globle.Constant;

import java.io.File;

/**
 * Created by Administrator on 2016/8/6.
 */
public class MyXutils {

    public static final int UPLOAD_SUCCESS = 55;
    public static final int UPLOAD_ERROR = 56;
    public static final int DOWNLOAD_SUCCESS = 57;
    public static final int DOWNLOAD_ERROR = 58;

    //保存头像
    public static void downLoadFile(String url, String path, final Handler handler){
        HttpUtils utils = new HttpUtils();
        utils.download(url, //目标文件的网址
                path, //指定存储的路径和文件名
                true, //是否支持断点续传
                true, //如果响应头中包含文件名，下载完成后自动重命名
                new RequestCallBack<File>() {
                    //下载完成后调用
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        LogUtils.i(responseInfo.result.getPath());
//                        Toast.makeText(contex,"保存成功",Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                    }
                    //下载失败调用
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        LogUtils.i(msg);
//                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(DOWNLOAD_ERROR);
                    }

                    //下载过程中不断调用
                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
//                        pb.setMax((int) total);
//                        pb.setProgress((int) current);
//                        tv_progress.setText(current * 100 / total + "%");
                    }
                });
    }

    //上传
    public static void upLoadFile(HttpUtils httpUtils, File file, final Handler handler){
        RequestParams params=new RequestParams();
        params.addBodyParameter(file.getPath().replace("/", ""), file);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constant.Server.UPLOAD_HEAD_URL,params,new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException e, String msg) {
//                ToastUtils.Toast(getApplicationContext(),"上次失败");
                handler.sendEmptyMessage(UPLOAD_ERROR);
                LogUtils.i(msg);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
//                ToastUtils.Toast(getApplicationContext(),"上次成功");
                handler.sendEmptyMessage(UPLOAD_SUCCESS);
                LogUtils.i(responseInfo.result);
            }
        });
    }

}
