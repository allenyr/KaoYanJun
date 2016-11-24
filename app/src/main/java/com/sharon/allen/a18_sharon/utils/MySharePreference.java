package com.sharon.allen.a18_sharon.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/8/3.
 */
public class MySharePreference {
    static public void putSP(Context context, String key, int i){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,i);
        editor.commit();
    }

    static public int getSP(Context context, String key, int i){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,i);
    }

    static public void putSP(Context context, String key, boolean i){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,i);
        editor.commit();
    }

    static public boolean getSP(Context context, String key, boolean i){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,i);
    }

    static public void putSP(Context context, String key, String s){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,s);
        editor.commit();
    }

    static public String getSP(Context context, String key, String s){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",context.MODE_PRIVATE);
        return sharedPreferences.getString(key,s);
    }

    static public void putSP(Context context, String key1, String s1,String key2,String s2){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key1,s1);
        editor.putString(key2,s2);
        editor.commit();
    }

    static public void putSP(Context context, String key1, String s1,String key2,String s2,String key3,boolean b){
        SharedPreferences sharedPreferences = context.getSharedPreferences("info",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key1,s1);
        editor.putString(key2,s2);
        editor.putBoolean(key3,b);
        editor.commit();
    }

}
