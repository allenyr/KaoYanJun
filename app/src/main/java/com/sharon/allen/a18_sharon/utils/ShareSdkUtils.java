package com.sharon.allen.a18_sharon.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.sharon.allen.a18_sharon.bean.UserDataManager;
import com.sharon.allen.a18_sharon.globle.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Administrator on 2016/10/26.
 */

public class ShareSdkUtils {

    public static final int SHARESDK_CALLBACK_SUCCESS = 70;

    //分享
    public static void showShare(final Context context, String title, final String url, final String text, final UserDataManager userDataManager, final Handler handler, final int addMoney){
        OnekeyShare share = new OnekeyShare();

        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        share.setTheme(OnekeyShareTheme.CLASSIC);
        //在自动授权时可以禁用SSO方式
        share.disableSSOWhenAuthorize();
        //微信分享不显示text(除了分享文本)
        share.setText(text);
        //text是分享文本，所有平台都需要这个字段
        share.setTitle(title);
        //设置网页跳转url，微信好友和朋友圈中使用
        share.setUrl(url);
        //设置网页跳转url，qq好友和空间使用
        share.setTitleUrl(url);
        //设置分享显示的图标，QQ和微信都用
        share.setImagePath(Environment.getExternalStorageDirectory()+ Constant.SdCard.SAVE_DOWNLOAD_PATH+"share_logo.png");
        //QZone分享完之后返回应用时提示框上显示的名称
        share.setSite(title);
        //QZone分享参数
        share.setSiteUrl(url);
        //分享时的位置
        share.setVenueName(title);
        //分享时的位置描述
        share.setVenueDescription("This is a beautiful place!");

        //设置微信朋友圈分享的title为text（朋友圈text不显示）
        share.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if(platform.getName().equalsIgnoreCase(WechatMoments.NAME)){
                    paramsToShare.setTitle(text);
                }
            }
        });

        share.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                LogUtils.i("分享成功");
                if (addMoney == 0){
                    ToastUtils.Toast(context,"分享成功");
                }else {
                    ToastUtils.Toast(context,"分享成功,积分+"+addMoney);
                }
                userDataManager.setMoney(userDataManager.getMoney()+addMoney);
//                //+积分
                ArrayList<String> list = new ArrayList<String>();
                list.add(userDataManager.getId()+"");
                list.add(addMoney+"");
                MyOkHttp myOkHttp = new MyOkHttp();
                myOkHttp.okhttpGet(handler,SHARESDK_CALLBACK_SUCCESS,Constant.Server.GET_PATH,list,24);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
//                LogUtils.i("分享失败");
                ToastUtils.Toast(context,"分享失败");
                LogUtils.i(throwable.toString());
            }

            @Override
            public void onCancel(Platform platform, int i) {
//                LogUtils.i("分享取消");
                ToastUtils.Toast(context,"分享取消");
            }
        });

        // 启动分享
        share.show(context);

    }
}
