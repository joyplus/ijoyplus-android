package com.joyplus.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 建立一个数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final int version = 2; //数据库版本,默认为1
	public DBHelper(Context context) {
		super(context, "download.db", null, version);
	}

	/**
	 * 在download.db数据库下创建一个download_info表存储下载信息
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists download_info(_id integer PRIMARY KEY AUTOINCREMENT,  "
				+ "compeleteSize integer, fileSize integer, prod_id char, my_index char, url char,urlposter char,my_name char,download_state char,file_path char)");
		db.execSQL("create table if not exists video_cache(_id integer PRIMARY KEY AUTOINCREMENT,  "
				+ "prod_id char, prod_value char,prod_type char,create_date TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),prod_subname char,last_playtime,comments char)");
		db.execSQL("create table if not exists play_record(_id integer PRIMARY KEY AUTOINCREMENT,  "
				+ "prod_id char,prod_subname char,create_date TimeStamp NOT NULL DEFAULT (datetime('now','localtime')),last_playtime)");
		db.execSQL("create table if not exists search_record(_id integer PRIMARY KEY AUTOINCREMENT,  "
				+ "search_word char,create_date TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  try {
			  db.execSQL("alter table download_info add column file_path char");
			  db.execSQL("alter table video_cache add column comments char");
		  } catch (Exception e) {
			  Log.i("info","异常——————————>"+e.getMessage());
		  } 
	}
}