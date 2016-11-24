package com.sharon.allen.a18_sharon.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Allen on 2016/5/27.
 */
public class TextUtils {
    //读文件流保存到string
    public static String getTextFromStream(InputStream is){
        byte[] b = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while((len = is.read(b)) != -1){
                bos.write(b, 0, len);
            }
            //把字节数组输出流转换成字节数组，然后用字节数组构造一个字符串
            String text = new String(bos.toByteArray());
            return text;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

}
