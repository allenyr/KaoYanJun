package com.sharon.allen.a18_sharon.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import com.sharon.allen.a18_sharon.globle.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MyOkHttp {

    public static final int ON_ERROR = 404;
    public static final int DOWNLOAD_SUCCESS = 61;
    public static final int UPLOAD_SUCCESS = 62;
    public static final int DOWNLOAD_PROGRESS = 63;
    public static final int DOWNLOAD_ERROR = 64;
    public static int downloadProgress = 0;

    public MyOkHttp(){

    }
    //-----------------------回调------------------------
    public static interface OnOkhttpListener {
        void onProgress(int progress);
        void onSuccess(String msg);
        void onError(String msg);
    }

    private OnOkhttpListener onOkhttpListener;

    public void setOnOkhttpListener(OnOkhttpListener onOkhttpListener) {
        this.onOkhttpListener = onOkhttpListener;
    }
    //-------------------------------------------------

    public void okhttpGet(final Handler handler, final int MESSAGE_WHAT, String url, ArrayList<String> list, int what){
        final Message message = handler.obtainMessage();
        StringBuffer stringBuffer = new StringBuffer();
        if (list!=null){
            try {
                for (int i=1;i<=list.size();i++){
                    stringBuffer.append("param"+i+"="+list.get(i-1).toString()+"&");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        url = url + stringBuffer.toString()+"what="+what;
        LogUtils.i("URL="+url);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        Call mcall= mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(ON_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                message.obj = response.body().string();
                message.what = MESSAGE_WHAT;
                handler.sendMessage(message);
            }
        });

    }


    public void postAsynHttp(final Handler handler, final int MESSAGE_WHAT, String url, ArrayList<String> list, int what) {
        final Message message = handler.obtainMessage();
        OkHttpClient mOkHttpClient =new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("what",what+"");
        if (list!=null){
            for (int i=1;i<=list.size();i++){
                builder.add("param"+i,list.get(i-1));
            }
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtils.i("post获取失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LogUtils.i("post获取成功");
                message.obj = str;
                message.what = MESSAGE_WHAT;
                handler.sendMessage(message);

            }
        });
    }

    //下载文件
    //arg2:url
    //arg3:保存路径
    //arg4:保存名字
    public void downLoadFile(String url, String fileDir, String name){
//        final Message message = handler.obtainMessage();
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(fileDir, name)
                {
                    @Override
                    public void onBefore(Request request, int id)
                    {
                        Log.e("test", "onBefore");
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                        onOkhttpListener.onProgress((int) (100 * progress));
//                        downloadProgress = (int) (100 * progress);
//                        handler.sendEmptyMessage(DOWNLOAD_PROGRESS);
                        LogUtils.i("inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        LogUtils.i("test", "onError :" + e.getMessage());
                        onOkhttpListener.onError(e.getMessage());
//                        handler.sendEmptyMessage(DOWNLOAD_ERROR);
//                        downloadProgress = 0;
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        onOkhttpListener.onSuccess(file.getAbsolutePath());
//                        message.obj = file.getAbsolutePath();
//                        message.what = DOWNLOAD_SUCCESS;
//                        handler.sendMessage(message);
                        LogUtils.i("onResponse :" + file.getAbsolutePath());
//                        downloadProgress = 0;
                    }

                });

    }

    //上传文件
    public void upLoadFile(final Handler handler,File file,String url){
        OkHttpUtils.post()//
                .addFile("upload",getNameFromPath(file.getPath()), file)
                .url(url)
//                .params(params)//
//                .headers(headers)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        System.out.println(e.toString());
                        handler.sendEmptyMessage(404);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        handler.sendEmptyMessage(UPLOAD_SUCCESS);
                        System.out.println(response);
                    }
                });
    }


    //获取文件名
    public static String getNameFromPath(String path) {
        //返回“/”最后一次出现的索引
        int index = path.lastIndexOf("/");
        //返回一个新的字符串，包含字符串中从index+1后的所有字符
        String name = path.substring(index+1);
        LogUtils.i("name="+name);
        return name;
    }



}
