package com.sharon.allen.a18_sharon.model;

/**
 * Created by Administrator on 2016/10/12.
 */

public class Question {
    private int id;
    private int userid;
    private String username;
    private String headurl;
    private String sex;
    private String question;
    private String time;
    private String type;
    private int replycount;
    private String title;

    public Question() {

    }

    public Question(int userid, String username, String headurl,
			String sex, String question, String time, String type,
			int replycount,String title) {
		super();
		this.userid = userid;
		this.username = username;
		this.headurl = headurl;
		this.sex = sex;
		this.question = question;
		this.time = time;
		this.type = type;
		this.replycount = replycount;
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReplycount() {
		return replycount;
	}

	public void setReplycount(int replycount) {
		this.replycount = replycount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
