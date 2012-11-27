package com.joy.msg;


public class ChatMsgEntity {
	private String head;
	private String name;
    private String date;
    private String URL;
    private String time;
    private String how;
    private String name1;
    
    public String gethead() {
        return head;
    }
    public void sethead(String head) {
        this.head = head;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName1() {
        return name1;
    }
    public void setName1(String name1) {
        this.name1 = name1;
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

    public ChatMsgEntity(String head,String name, String date, String URL,String time,String how,String name1) {
        super();
        this.head=head;
        this.name = name;
        this.date = date;
        this.URL=URL;
        this.time = time;
        this.how=how;
        this.name1 = name1;
    }
}
