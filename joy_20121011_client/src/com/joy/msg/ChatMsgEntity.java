package com.joy.msg;

import android.R.integer;

public class ChatMsgEntity {
	private int head;
	private String name;
    private String date;
    private String URL;
    private String time;
    private String how;
    
    public int gethead() {
        return head;
    }
    public void sethead(int head) {
        this.head = head;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String gettime() {
        return time;
    }
    public void settime(String text) {
        this.time = text;
    }
    public String getURL() {
        return URL;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }
    public String gethow() {
        return how;
    }
    public void sethow(String how) {
        this.how = how;
    }
    
	public ChatMsgEntity() {
    }

    public ChatMsgEntity(int head,String name, String date, String URL,String time,String how) {
        super();
        this.head=head;
        this.name = name;
        this.date = date;
        this.URL=URL;
        this.time = time;
        this.how=how;
    }
}
