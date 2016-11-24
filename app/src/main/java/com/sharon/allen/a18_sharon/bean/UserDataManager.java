package com.sharon.allen.a18_sharon.bean;

import android.content.Context;

import com.sharon.allen.a18_sharon.utils.MySharePreference;

/**
 * Created by Administrator on 2016/10/10.
 */

public class UserDataManager  {
    private int id;
    private String  username;
    private String  password;
    private String  headUrl;
    private String  phone;
    private String  sex;
    private String  address;
    private int  money;
    private String  signa;
    private int  messagenum;

    private Context context;
    private static UserDataManager instance;

    UserDataManager(Context context){
        this.context = context;
    }

    //单例模式
    public static UserDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserDataManager(context);
        }
        return instance;
    }

    public int getId() {
        id = MySharePreference.getSP(context,"id",0);
        return id;
    }

    public void setId(int id) {
        MySharePreference.putSP(context,"id",id);
        this.id = id;
    }

    public String getUsername() {
        username = MySharePreference.getSP(context,"username","");
        return username;
    }

    public void setUsername(String username) {
        MySharePreference.putSP(context,"username",username);
        this.username = username;
    }

    public String getHeadUrl() {
        headUrl = MySharePreference.getSP(context,"headUrl","");
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        MySharePreference.putSP(context,"headUrl",headUrl);
        this.headUrl = headUrl;
    }

    public String getPassword() {
        password = MySharePreference.getSP(context,"password","");
        return password;
    }

    public void setPassword(String password) {
        MySharePreference.putSP(context,"password",password);
        this.password = password;
    }

    public String getSex() {
        sex = MySharePreference.getSP(context,"sex","");
        return sex;
    }

    public void setSex(String sex) {
        MySharePreference.putSP(context,"sex",sex);
        this.sex = sex;
    }

    public String getPhone() {
        phone = MySharePreference.getSP(context,"phone","");
        return phone;
    }

    public void setPhone(String phone) {
        MySharePreference.putSP(context,"phone",phone);
        this.phone = phone;
    }

    public String getAddress() {
        address = MySharePreference.getSP(context,"address","");
        return address;
    }

    public void setAddress(String address) {
        MySharePreference.putSP(context,"address",address);
        this.address = address;
    }

    public int getMoney() {
        money = MySharePreference.getSP(context,"money",0);
        return money;
    }

    public void setMoney(int money) {
        MySharePreference.putSP(context,"money",money);
        this.money = money;
    }

    public String getSigna() {
        signa = MySharePreference.getSP(context,"signa","");
        return signa;
    }

    public void setSigna(String signa) {
        MySharePreference.putSP(context,"signa",signa);
        this.signa = signa;
    }

    public int getMessagenum() {
        messagenum = MySharePreference.getSP(context,"messagenum",0);
        return messagenum;
    }

    public void setMessagenum(int messagenum) {
        MySharePreference.putSP(context,"messagenum",messagenum);
        this.messagenum = messagenum;
    }

}
