package com.joyplus.Video;

/**
 * @author yyc
 *
 */
public class PlayHistory {
	String prod_id = null;
	String my_index = null;
	String play_time = null;
	public PlayHistory(String prod_id,String my_index,String play_time)
	{
		this.prod_id = prod_id;
		this.my_index = my_index;
		this.play_time = play_time;
	}
	public String getProd_id() {
		return prod_id;
	}
	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}
	public String getMy_index() {
		return my_index;
	}
	public void setMy_index(String my_index) {
		this.my_index = my_index;
	}
	public String getPlay_time() {
		return play_time;
	}
	public void setPlay_time(String play_time) {
		this.play_time = play_time;
	}
}
