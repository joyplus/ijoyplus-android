package com.joyplus.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 一个业务类
 */
public class Dao {
	private static Dao dao = null;
	private Context context;

	private Dao(Context context) {
		this.context = context;
	}

	public static Dao getInstance(Context context) {
		if (dao == null) {
			dao = new Dao(context);
		}
		return dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DBHelper(context).getReadableDatabase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public synchronized boolean isHasInfors(String prod_id,String my_index) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from download_info where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] {prod_id,my_index});
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
	
	/**
	 * 查看数据库中是否有正在下载的数据
	 */
	public synchronized boolean isHasInforsDownloading(String download_state) {
		SQLiteDatabase database = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "select count(*)  from download_info where download_state=?";
			cursor = database.rawQuery(sql, new String[] {download_state});
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
	
	public synchronized void saveInfos(List<DownloadInfo> infos) {
		SQLiteDatabase database = getConnection();
		try {
			for (DownloadInfo info : infos) {
				String sql = "insert into download_info(compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state) values (?,?,?,?,?,?,?,?)";
				Object[] bindArgs = { info.getCompeleteSize(), info.getFileSize(),
						info.getProdId(), info.getIndex(),
						info.getUrl() ,info.getPoster(),
						info.getName(),info.getState()};
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
	
	public synchronized void InsertOneInfo(DownloadInfo info)
	{
		SQLiteDatabase database = getConnection();
		try {
				String sql = "insert into download_info(compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state) values (?,?,?,?,?,?,?,?)";
				Object[] bindArgs = { info.getCompeleteSize(), info.getFileSize(),
						info.getProdId(), info.getIndex(),
						info.getUrl() ,info.getPoster(),
						info.getName(),info.getState()};
				database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

	public synchronized List<DownloadInfo> getInfos(String prod_id,String my_index) {
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] { prod_id,my_index});
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
						cursor.getInt(1), cursor.getString(2), cursor.getString(3),
						cursor.getString(4),cursor.getString(5),
						cursor.getString(6),cursor.getString(7));
				list.add(info);
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
		return list;
	}
	
	public synchronized DownloadInfo getOneInfo(String prod_id,String my_index) {
		DownloadInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where prod_id=? and my_index=?";
			cursor = database.rawQuery(sql, new String[] { prod_id,my_index});
			while (cursor.moveToNext()) {
				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4),cursor.getString(5),
						cursor.getString(6),cursor.getString(7));
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
	
	public synchronized DownloadInfo getOneStateInfo(String download_state) {
		DownloadInfo info = null;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info where download_state=?";
			cursor = database.rawQuery(sql, new String[] { download_state});
			while (cursor.moveToNext()) {
				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4),cursor.getString(5),
						cursor.getString(6),cursor.getString(7));
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
	
	public synchronized List<DownloadInfo> getDownloadInfos()
	{
		List<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "select compeleteSize,fileSize, prod_id,my_index,url,urlposter,my_name,download_state from download_info";
			cursor = database.rawQuery(sql,null);
			while (cursor.moveToNext()) {
				DownloadInfo info = new DownloadInfo(cursor.getInt(0),
						cursor.getInt(1), cursor.getString(2), cursor.getString(3),
						cursor.getString(4),cursor.getString(5),
						cursor.getString(6),cursor.getString(7));
				list.add(info);
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
		return list;
	}
	
	public synchronized void updataInfos(int compeleteSize,String prod_id,String my_index) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set compeleteSize=? where prod_id=? and my_index=?";
			Object[] bindArgs = { compeleteSize, prod_id, my_index };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	public synchronized void updataInfoState(String download_state,String prod_id,String my_index) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "update download_info set download_state=? where prod_id=? and my_index=?";
			Object[] bindArgs = { download_state, prod_id, my_index };
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
	
	public synchronized void delete(String prod_id,String my_index) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("download_info", "prod_id=? and my_index=?", new String[] { prod_id,my_index});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}
}