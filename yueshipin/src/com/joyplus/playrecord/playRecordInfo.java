package com.joyplus.playrecord;

/**
 * @author yyc
 *
 */
public class playRecordInfo {
	
	private String prod_id;
	private String prod_subname;
	private String create_date;
	private String last_playtime;
	
	public playRecordInfo()
	{
		
	}
	
	public playRecordInfo(String prod_id, String prod_subname,
			String create_date, String last_playtime) {
		super();
		this.prod_id = prod_id;
		this.prod_subname = prod_subname;
		this.create_date = create_date;
		this.last_playtime = last_playtime;
	}

	public String getProd_id() {
		return prod_id;
	}

	public void setProd_id(String prod_id) {
		this.prod_id = prod_id;
	}

	public String getProd_subname() {
		return prod_subname;
	}

	public void setProd_subname(String prod_subname) {
		this.prod_subname = prod_subname;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public String getLast_playtime() {
		return last_playtime;
	}

	public void setLast_playtime(String last_playtime) {
		this.last_playtime = last_playtime;
	}

	@Override
	public String toString() {
		return "playRecordInfo [prod_id=" + prod_id + ", prod_subname="
				+ prod_subname + ", create_date=" + create_date
				+ ", last_playtime=" + last_playtime + "]";
	}
	
}
