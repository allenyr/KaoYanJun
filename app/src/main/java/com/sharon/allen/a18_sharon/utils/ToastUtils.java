package com.sharon.allen.a18_sharon.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/30.
 */
public class ToastUtils  {
    public static void Toast(Context context ,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void Toast(Context context ,int msg){
        Toast.makeText(context,msg+"",Toast.LENGTH_SHORT).show();
    }
}
