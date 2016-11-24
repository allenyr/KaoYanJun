package com.sharon.allen.a18_sharon.model;

public class Message {
	private int id;
	private int senderid;
	private int receiverid;
	private String sendername;
	private String senderimg;
	private String sendersex;
	private String type;
	private String detail;
	private String time;
	private String imgurl;

	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Message(int id, int senderid, int receiverid, String sendername, String senderimg, String sendersex, String type, String detail, String time, String imgurl) {
		this.id = id;
		this.senderid = senderid;
		this.receiverid = receiverid;
		this.sendername = sendername;
		this.senderimg = senderimg;
		this.sendersex = sendersex;
		this.type = type;
		this.detail = detail;
		this.time = time;
		this.imgurl = imgurl;
	}

	public String getSendersex() {
		return sendersex;
	}

	public void setSendersex(String sendersex) {
		this.sendersex = sendersex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSenderid() {
		return senderid;
	}

	public void setSenderid(int senderid) {
		this.senderid = senderid;
	}

	public int getReceiverid() {
		return receiverid;
	}

	public void setReceiverid(int receiverid) {
		this.receiverid = receiverid;
	}

	public String getSendername() {
		return sendername;
	}

	public void setSendername(String sendername) {
		this.sendername = sendername;
	}

	public String getSenderimg() {
		return senderimg;
	}

	public void setSenderimg(String senderimg) {
		this.senderimg = senderimg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
}
