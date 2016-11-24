package com.sharon.allen.a18_sharon.model;

/**
 * Created by Administrator on 2016/10/15.
 */

public class Reply {
    private int id;
    private int userid;
    private String headurl;
    private String username;
    private String sex;
    private String time;
    private String type;
    private String reply;
    private int up;
    private int down;

    public Reply() {

    }

    public Reply(int userid, String headurl, String username, String sex, String time, String type, String reply, int up, int down) {
        this.userid = userid;
        this.headurl = headurl;
        this.username = username;
        this.sex = sex;
        this.time = time;
        this.type = type;
        this.reply = reply;
        this.up = up;
        this.down = down;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }
}
