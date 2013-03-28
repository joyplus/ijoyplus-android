package com.joyplus.playrecord;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.joyplus.download.DBHelper;

public class playRecordDao {
	private static playRecordDao play_record = null;
	private Context context;

	private playRecordDao(Context context) {
		this.context = context;
	}

	public static playRecordDao getInstance(Context context) {
		if (play_record == null) {
			play_record = new playRecordDao(context);
		}
		return play_record;
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
	 * 增加一条播放记录
	 */
	public synchronized void InsertOneInfo(playRecordInfo info) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "insert into play_record(prod_id,prod_subname,last_playtime) values (?,?,?)";
			Object[] bindArgs = {info.getProd_id(),info.getProd_subname(),info.getLast_playtime()};
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
	 * 删除一条播放记录
	 */
	public synchronized void delete(String prod_id) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("play_record", "prod_id=?",
					new String[] { prod_id});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 更新一条详情缓存
	 */
	public synchronized void updateOneInfo(playRecordInfo info) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = null;
			sql = "update play_record set prod_subname=? and last_playtime=? where prod_id=? and prod_subname";
			Object[] bindArgs = {info.getProd_subname(),info.getLast_playtime(),info.getProd_id(),info.getProd_subname()};
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
	 * 更新subname
	 */
	public synchronized void updateOneInfoSubName(playRecordInfo info) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = null;
			sql = "update play_record set prod_subname=? where prod_id=?";
			Object[] bindArgs = {info.getProd_subname(),info.getProd_id()};
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
	 * 查找某个播放记录是否存在
	 */
	public synchronized boolean isHasInfors(String prod_id,String subname) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from play_record where prod_id=?";
			cursor = database.rawQuery(sql, new String[] { prod_id });
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
	 * 获取某一条播放记录
	 */
	public synchronized playRecordInfo getOneInfo(String prod_id,String prod_subname) {
		playRecordInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = null;
			sql = "select prod_id,prod_subname,create_date,last_playtime from play_record where prod_id=? and prod_subname=?";
			cursor = database.rawQuery(sql, new String[] { prod_id ,prod_subname});
			while (cursor.moveToNext()) {
				info = new playRecordInfo(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3));
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
	
	/*
	 * 获取某prod_id播放记录
	 */
	public synchronized playRecordInfo getProdIdInfo(String prod_id) {
		playRecordInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = null;
			sql = "select prod_id,prod_subname,create_date,last_playtime from play_record where prod_id=?";
			cursor = database.rawQuery(sql, new String[] { prod_id});
			while (cursor.moveToNext()) {
				info = new playRecordInfo(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3));
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
			String sql = "select * from play_record";
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
			String sql = "delete from play_record where create_date < datetime('now','-7 day')";
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
