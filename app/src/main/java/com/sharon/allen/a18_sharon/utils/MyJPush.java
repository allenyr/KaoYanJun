package com.sharon.allen.a18_sharon.utils;

import android.content.Context;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by Administrator on 2016/9/29.
 */

public class MyJPush {

    //设置别名
    public static void setAlias(Context context,String alias){
        TagAliasCallback callback = new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i == 0){
                    LogUtils.i("设置别名成功");
                }else {
                    LogUtils.i("设置别名失败");
                }
                LogUtils.i(s);
            }
        };

        JPushInterface.setAlias(context,alias,callback);
    }

}
