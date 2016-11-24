package com.sharon.allen.a18_sharon.model;

public class Comment {
	
	private int id;
	private int userid;
	private String headurl;
	private String username;
	private String comment;
	private String time;
	private String sex;

	public Comment(int userid,String headurl, String username,
			String comment, String time,String sex) {
		super();
		this.userid = userid;
		this.headurl = headurl;
		this.username = username;
		this.comment = comment;
		this.time = time;
		this.sex = sex;
	}

	public Comment() {
		super();
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
