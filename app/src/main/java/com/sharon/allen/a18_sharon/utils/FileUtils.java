package com.sharon.allen.a18_sharon.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by Allen on 2016/11/14.
 */

public class FileUtils {

    //inputstream转file
    public static void inputstreamtofile(InputStream ins, File file){
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        }catch (Exception e){
            e.printStackTrace();
        }
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

    //获取文件后缀
    public static String getFileSuffix(String path){
        String suffix = path.substring(path.lastIndexOf('.')+1);
        return suffix;
    }


}
