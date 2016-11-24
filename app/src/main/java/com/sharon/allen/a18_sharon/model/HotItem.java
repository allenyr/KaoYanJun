package com.sharon.allen.a18_sharon.model;

/**
 * Created by Allen on 2016/5/27.
 */
public class HotItem {
    private int id;
    private String mood;
    private String imageurl;
    private int thank;
    private String username;
    private String headurl;
    private String time;
    private int commentcount;
    private int userId;
    private String sex;

    public HotItem(){

    }

    public HotItem(String mood, String imageurl, int thank, String username, String headurl, String time, int commentcount, int userid,String sex) {
        this.mood = mood;
        this.imageurl = imageurl;
        this.thank = thank;
        this.username = username;
        this.headurl = headurl;
        this.time = time;
        this.commentcount = commentcount;
        this.userId = userid;
        this.sex = sex;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public int getThank() {
        return thank;
    }

    public void setThank(int thank) {
        this.thank = thank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }
}
