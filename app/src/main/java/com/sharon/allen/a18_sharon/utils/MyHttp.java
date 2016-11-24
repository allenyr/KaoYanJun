package com.sharon.allen.a18_sharon.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sharon.allen.a18_sharon.globle.Constant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/1.
 */
public class MyHttp {

    private static final int DELAYTIME =8000;

    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT,final int what){
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                Message message = handler.obtainMessage();
                String path = null;
                path = Constant.Server.GET_PATH
                                + "what="+what;
                try{
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(DELAYTIME);
                    connection.setReadTimeout(DELAYTIME);
                    if(connection.getResponseCode() == 200){
                        InputStream inputStream = connection.getInputStream();
                        String text = TextUtils.getTextFromStream(inputStream);
                        message.obj = text;
                        message.what = MESSAGE_WHAT;
                        handler.sendMessage(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(404);
                }
            }
        };
        thread.start();
    }

//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1, final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,final String param2, final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                Message message = handler.obtainMessage();
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    handler.sendEmptyMessage(404);
//                    e.printStackTrace();
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,
//                                        final String param2,final String param3, final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param3=" + URLEncoder.encode(param3+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,
//                                        final String param2,final String param3,final String param4, final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param3=" + URLEncoder.encode(param3+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param4=" + URLEncoder.encode(param4+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,
//                                        final String param2, final String param3, final String param4, final String param5, final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param3=" + URLEncoder.encode(param3+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param4=" + URLEncoder.encode(param4+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param5=" + URLEncoder.encode(param5+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,
//                                        final String param2, final String param3, final String param4, final String param5, final String param6,final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param3=" + URLEncoder.encode(param3+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param4=" + URLEncoder.encode(param4+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param5=" + URLEncoder.encode(param5+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param6=" + URLEncoder.encode(param6+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,
//                                        final String param2, final String param3, final String param4,
//                                        final String param5, final String param6, final String param7,final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param3=" + URLEncoder.encode(param3+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param4=" + URLEncoder.encode(param4+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param5=" + URLEncoder.encode(param5+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param6=" + URLEncoder.encode(param6+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param7=" + URLEncoder.encode(param7+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
//
//    public static void creatHttpRequest(final Handler handler, final int MESSAGE_WHAT, final String param1,
//                                        final String param2, final String param3, final String param4,
//                                        final String param5, final String param6, final String param7,
//                                        final String param8, final String param9, final String param10,final int what){
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String path = null;
//                try {
//                    path = Constant.Server.GET_PATH
//                            + "param1=" + URLEncoder.encode(param1+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param2=" + URLEncoder.encode(param2+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param3=" + URLEncoder.encode(param3+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param4=" + URLEncoder.encode(param4+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param5=" + URLEncoder.encode(param5+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param6=" + URLEncoder.encode(param6+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param7=" + URLEncoder.encode(param7+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param8=" + URLEncoder.encode(param8+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param9=" + URLEncoder.encode(param9+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&param10=" + URLEncoder.encode(param10+"", "UTF-8")  //提交非中文，可以用，可不用编码
//                            + "&what="+what;
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try{
//                    URL url = new URL(path);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(DELAYTIME);
//                    connection.setReadTimeout(DELAYTIME);
//                    if(connection.getResponseCode() == 200){
//                        InputStream inputStream = connection.getInputStream();
//                        String text = TextUtils.getTextFromStream(inputStream);
//                        Message message = handler.obtainMessage();
//                        message.obj = text;
//                        message.what = MESSAGE_WHAT;
//                        handler.sendMessage(message);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(404);
//                }
//            }
//        };
//        thread.start();
//    }
}
