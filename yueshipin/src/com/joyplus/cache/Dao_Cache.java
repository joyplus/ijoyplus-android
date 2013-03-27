package com.joyplus.cache;

import java.util.ArrayList;
import java.util.List;

import com.joyplus.download.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Dao_Cache {

	private static Dao_Cache dao_cache = null;
	private Context context;

	private Dao_Cache(Context context) {
		this.context = context;
	}

	public static Dao_Cache getInstance(Context context) {
		if (dao_cache == null) {
			dao_cache = new Dao_Cache(context);
		}
		return dao_cache;
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
	 * 增加一条详情缓存
	 */
	public synchronized void InsertOneInfo(videoCacheInfo info) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "insert into video_cache(prod_id,prod_value, prod_type,create_date,prod_subname,last_playtime) values (?,?,?,?,?,?)";
			Object[] bindArgs = { info.getProd_id(), info.getProd_value(),
					info.getProd_type(), info.getClass(),
					info.getProd_subname(), info.getLast_playtime() };
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
	 * 删除一条详情缓存
	 */
	public synchronized void delete(String prod_id) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("video_cache", "prod_id=?",
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
	public synchronized void updateOneInfo(videoCacheInfo info) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = null;
			
			if(info.getProd_type().equalsIgnoreCase("1"))
			{
				sql = "update video_cache set last_playtime=? where prod_id=?";
				Object[] bindArgs = {info.getLast_playtime(),info.getProd_id()};
				database.execSQL(sql, bindArgs);
			}
			else
			{
				sql = "update video_cache set prod_subname=? and last_playtime=? where prod_id=? and prod_subname=?";
				Object[] bindArgs = {info.getProd_subname(),info.getLast_playtime(),info.getProd_id()};
				database.execSQL(sql, bindArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	/*
	 * 查找某个详情缓存是否存在
	 */
	public synchronized boolean isHasInfors(String prod_id) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from video_cache where prod_id=?";
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
	 * 获取某一条缓存记录
	 */
	public synchronized videoCacheInfo getOneInfo(String prod_id) {
		videoCacheInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = null;
			sql = "select prod_id,prod_value, prod_type,create_date,prod_subname,last_playtime from video_cache where prod_id=?";
			cursor = database.rawQuery(sql, new String[] { prod_id});
			while (cursor.moveToNext()) {
				info = new videoCacheInfo(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4),
						cursor.getString(5));
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
	 * 增加一条播放记录
	 * 可以重复
	 */
	
	/*
	 * 删除一条播放记录
	 * 可以重复
	 */
	/*
	 * 更新一条播放记录
	 */
	
	/*
	 * 获取一条播放记录
	 */

	/*
	 * 获取数据库中总的条数
	 */
	public synchronized int getCount() {
		SQLiteDatabase database = getConnection();
		try {
			Cursor cursor = null;
			String sql = "select * from video_cache";
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
			database.delete("video_cache", "", null);
			String sql = "delete from video_cahce where create_date < datetime('now','-7 day')";
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
