package com.sharon.allen.a18_sharon.utils;

import android.util.Log;

/**
 * Created by Allen on 2016/5/14.
 */
public class LogUtils {

    public static String TAG = "LLL=================>";

    public static boolean isDebug =true;
    public static void i(String msg){
        if (isDebug){
            Log.i(TAG,msg);
        }
    }
    public static void i(int msg){
        if (isDebug){
            Log.i(TAG,msg+"");
        }
    }
    public static void i(String tag,String msg){
        if (isDebug){
            Log.i(tag,msg);
        }
    }
    public static void i(Object tag,String msg){
        if (isDebug){
            //打印类名+msg
            Log.i(tag.getClass().getSimpleName(),msg);
        }
    }
    public static void e(String tag,String msg){
        if (isDebug){
            Log.e(tag,msg);
        }
    }
}
