package com.joyplus.cache;

/**
 * @author yyc
 * 
 */
public class videoCacheInfo {
	private String prod_id = null;
	private String prod_value = null;
	private String prod_type = null;
	private String create_date = null;
	private String prod_subname = null;
	private String last_playtime = null;
	
	public videoCacheInfo()
	{
		//
	}
	
	public videoCacheInfo(String prod_id, String prod_value, String prod_type,
			String create_date, String prod_subname, String last_playtime) {
		super();
		this.prod_id = prod_id;
		this.prod_value = prod_value;
		this.prod_type = prod_type;
		this.create_date = create_date;
		this.prod_subname = prod_subname;
		this.last_playtime = last_playtime;
	}
	
	public String getProd_id() {
		return prod_id;
	}

	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}

	public String getProd_value() {
		return prod_value;
	}

	public void setProd_value(String prod_value) {
		this.prod_value = prod_value;
	}

	public String getProd_type() {
		return prod_type;
	}

	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getProd_subname() {
		return prod_subname;
	}

	public void setProd_subname(String prod_subname) {
		this.prod_subname = prod_subname;
	}

	public String getLast_playtime() {
		return last_playtime;
	}

	public void setLast_playtime(String last_playtime) {
		this.last_playtime = last_playtime;
	}

	@Override
	public String toString() {
		return "videoCacheInfo [prod_id=" + prod_id + ", prod_value="
				+ prod_value + ", prod_type=" + prod_type + ", create_date="
				+ create_date + ", prod_subname=" + prod_subname
				+ ", last_playtime=" + last_playtime + "]";
	}
	
}
