package com.sharon.allen.a18_sharon.globle;

import android.content.pm.PackageManager;
import android.os.Environment;

import com.sharon.allen.a18_sharon.utils.TimeUtils;

/**
 * Created by Allen on 2016/5/27.
 */
public class Constant {
    //接口默认变量为全局静态final修饰
    public interface Server{
        //定义是否使用本地服务器
        boolean LOCAL = false;
//        String LOCAL_PATH = "http://172.16.0.114:8080";
        String LOCAL_PATH = "http://192.168.31.114:8080";
        String SERVER_PATH = "http://119.29.170.73:8080";
        String PATH = LOCAL ? LOCAL_PATH : SERVER_PATH;

        String GET_PATH = Constant.Server.PATH+"/Web/servlet/Login?";
        String POST_PATH = Constant.Server.PATH+"/Web/servlet/Login";

        String UPLOAD_HEAD_URL =Constant.Server.PATH+"/Web/servlet/PhotoTest?what=1";
        String UPLOAD_IMG_URL =Constant.Server.PATH+"/Web/servlet/PhotoTest?what=2";

        String APP_STORE_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.sharon.allen.a18_sharon";

    }

    public interface SdCard{

        String SAVE_ROOT_PATH = "/kaoyanjun/";
        String SAVE_CACHE_PATH = "/kaoyanjun/cache";
        String SAVE_LIKE_PATH = "/kaoyanjun/like/";
        String SAVE_IMAGE_PATH = "/kaoyanjun/image/";
        String SAVE_AVATAR_PATH = "/kaoyanjun/avatar/";
        String SAVE_DOWNLOAD_PATH = "/kaoyanjun/download/";
    }



    public interface Permissions{
        int ROOT = 100;
        int ME = 1;
        int OTHER = 2;
    }

}
