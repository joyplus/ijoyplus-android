package com.joyplus.playrecord;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.joyplus.download.DBHelper;

public class searchRecordDao {
	private static searchRecordDao search_record = null;
	private Context context;

	private searchRecordDao(Context context) {
		this.context = context;
	}

	public static searchRecordDao getInstance(Context context) {
		if (search_record == null) {
			search_record = new searchRecordDao(context);
		}
		return search_record;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DBHelper(context).getReadableDatabase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}
	
	/*
	 * 增加一条搜索记录
	 */
	public synchronized void InsertOneInfo(String search_word) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "insert into search_record(search_word) values (?)";
			Object[] bindArgs = {search_word};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 删除一条搜索历史记录
	 */
	public synchronized void delete(String search_word) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("search_record", "prod_id=?",
					new String[] { search_word});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 更新一条历史记录
	 */
	public synchronized void updateOneInfo(String search_word) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = null;
			sql = "update search_record set where search_word=?";
			Object[] bindArgs = {search_word};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	/*
	 * 查找某个搜索记录是否存在
	 */
	public synchronized boolean isHasInfors(String search_word) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from search_record where search_word=?";
			cursor = database.rawQuery(sql, new String[] { search_word });
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return count == 0;
	}
	
	/*
	 * 获取某搜索记录
	 */
	public synchronized String[] getProdIdInfo() {
		String[] info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = null;
			sql = "select search_word from search_record";
			cursor = database.rawQuery(sql, null);
			List<String> list = new ArrayList<String>();
			while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return info;
	}
	
	public synchronized int getCount() {
		SQLiteDatabase database = getConnection();
		try {
			Cursor cursor = null;
			String sql = "select * from search_record";
			cursor = database.rawQuery(sql, null);
			if (cursor != null) {
				return cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
		return 0;
	}

	/*
	 * 删除一周前的数据
	 */
	public synchronized void delete() {
		SQLiteDatabase database = getConnection();
		try {
//			database.delete("play_record", "", null);
			String sql = "delete from search_record where create_date < datetime('now','-7 day')";
			database.execSQL(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
}
