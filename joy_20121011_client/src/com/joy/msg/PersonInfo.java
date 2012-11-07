package com.joy.msg;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;

public class PersonInfo {
	private String name;
	private String sort_key;
	private String photo_id;
	private List<String> phone;
	private Context context;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSortKey() {
		return this.sort_key;
	}
	
	public void setSortKey(String sort_key) {
		this.sort_key = sort_key;
	}
	
	public String getPhotoId() {
		return photo_id;
	}
	
	public void setPhotoId(String photo_id) {
		this.photo_id = photo_id;
	}
	
	public List<String> getPhone() {
		return phone;
	}
	
	public void setPhone(List<String> phone) {
		this.phone = phone;
	}
	
	public Bitmap getPhoto() {
		Bitmap photo = null;
		photo = loadImageFromUrl(context, photo_id);
		return photo;
	}
	
	public PersonInfo(Context context) {
		this.context = context;
	}
	
	public synchronized static Bitmap loadImageFromUrl(Context ct,String photo_id) {  
		Bitmap d = null;
		if (photo_id == null || photo_id.equals(""))
			return d;
		try{  
			String[] projection = new String[]{ContactsContract.Data.DATA15};
			String selection = "ContactsContract.Data._ID = " + photo_id;
			Cursor cur = ct.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null); 
			cur.moveToFirst();
			byte[] contactIcon = null;
			if(cur.getBlob(cur.getColumnIndex(ContactsContract.Data.DATA15))!=null){
				contactIcon=cur.getBlob(cur.getColumnIndex(ContactsContract.Data.DATA15));
				d=BitmapFactory.decodeByteArray(contactIcon, 0, contactIcon.length); 
			}
			cur.close();
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return d;  
	}     
	
	public void addPhone(String phone){
		this.phone.add(phone);
	}
}
