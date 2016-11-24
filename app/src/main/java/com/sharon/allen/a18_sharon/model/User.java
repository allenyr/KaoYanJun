package com.sharon.allen.a18_sharon.model;

/**
 * Created by Allen on 2016/6/5.
 */
public class User  {
    private int id;
    private  String username;
    private  String password;
    private  String headurl;
    private  String phone;
    private  String sex;
    private  String address;
    private  int money;
    private  String signa;
    private  int messagenum;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", headurl='" + headurl + '\'' +
                ", phone='" + phone + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", money=" + money +
                ", signa='" + signa + '\'' +
                '}';
    }

    public int getMessagenum() {
        return messagenum;
    }

    public void setMessagenum(int messagenum) {
        this.messagenum = messagenum;
    }

    public String getSigna() {
        return signa;
    }

    public void setSigna(String signa) {
        this.signa = signa;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public  String getPhone() {
        return phone;
    }

    public  void setPhone(String phone) {
        this.phone = phone;
    }

    public  String getSex() {
        return sex;
    }

    public  void setSex(String sex) {
        this.sex = sex;
    }

    public  String getAddress() {
        return address;
    }

    public  void setAddress(String address) {
        this.address = address;
    }

    public  String getUsername() {
        return username;
    }

    public  void setUsername(String username) {
        this.username = username;
    }

    public  String getPassword() {
        return password;
    }

    public  void setPassword(String password) {
        this.password = password;
    }

    public  String getHeadurl() {
        return headurl;
    }

    public  void setHeadurl(String headurl) {
        this.headurl = headurl;
    }
}
