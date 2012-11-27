package com.joy.msg;
/**
 * 
 * @author Administrator
 *
 */

public class ChatMsgEntity {
	private String prod_id;
	private String head_url;
	private String name;
    private String who;
    private String what;
    private String img_url;
    private String time;
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getHead_url() {
		return head_url;
	}
	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWho() {
		return who;
	}
	public void setWho(String who) {
		this.who = who;
	}
	public String getWhat() {
		return what;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ChatMsgEntity(String prod_id, String head_url, String name,
			String who, String what, String img_url, String time) {
		super();
		this.prod_id = prod_id;
		this.head_url = head_url;
		this.name = name;
		this.who = who;
		this.what = what;
		this.img_url = img_url;
		this.time = time;
	}
	public ChatMsgEntity() {
		super();
		// TODO Auto-generated constructor stub
	}


	
}
