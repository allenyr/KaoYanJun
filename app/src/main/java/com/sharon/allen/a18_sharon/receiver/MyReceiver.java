package com.sharon.allen.a18_sharon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.sharon.allen.a18_sharon.activity.MessageActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/9/29.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("receiver", "onReceive - " + intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String title = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);

        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//            receivingNotification(context, bundle);
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            System.out.println("收到了通知,"+"通知标题："+bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)+"," +
                    "通知内容："+bundle.getString(JPushInterface.EXTRA_ALERT));

            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            System.out.println("用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            Intent i = new Intent(context, MessageActivity.class);  //自定义打开的界面
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            Log.d("receiver", "Unhandled intent - " + intent.getAction());
        }

    }
}
