package com.sharon.allen.a18_sharon.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String getTimeGap(String dateStart,String dateStop) throws InterruptedException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        try {
            //parse：String转Date
            //format:Date转String
            Date begin = simpleDateFormat.parse(dateStart);
            Date end = simpleDateFormat.parse(dateStop);
            long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒

            long year = between/(24*3600*365);
            long month = between/(24*3600*30);
            long day=between/(24*3600);
            long hour=between%(24*3600)/3600;
            long minute=between%3600/60;
            long second=between%60/60;

            if (year >= 1){
                return year + "年前";
            } else if(month >= 1){
                return month + "月前";
            }else if (day >= 1) {
                return day + "天前";
            } else if (hour >= 1) {
                return hour + "小时前";
            } else if (minute >= 1) {
                return minute + "分钟前";
            } else {
                return "刚刚";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //得到当前时间
    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        //Date转String
        return sdf.format(date);
    }

    //得到当前时间
    public static String getCurrentTimeBaseDay() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //Date转String
        return sdf.format(date);
    }

    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".jpeg";
    }

    // 使用系统当前日期加以调整作为照片的名称
    public static String getPhotoFileNameNoSuffix() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return sdf.format(date) ;
    }

    //倒计时（时间差）
    public static long countdown(String nowDate, String futureDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date now = df.parse(nowDate);
        Date future = df.parse(futureDate);
        long time=future.getTime()-now.getTime();
//        long day=time/(24*60*60*1000);
//        long hour=(time/(60*60*1000)-day*24);
//        long min=((time/(60*1000))-day*24*60-hour*60);
//        long s=(time/1000-day*24*60*60-hour*60*60-min*60);
//        LogUtils.i(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
        return time ;
    }

    //倒计时（时间差）
    public static String countdownForString(String nowDate, String futureDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date now = df.parse(nowDate);
        Date future = df.parse(futureDate);
        long time = future.getTime() - now.getTime();
        long day = time / (24 * 60 * 60 * 1000);
        long hour = (time / (60 * 60 * 1000) - day * 24);
        long min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        return ("考研倒计时:" + day + "天" + hour + "小时" + min + "分" + s + "秒");

    }
}