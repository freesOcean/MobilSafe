package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.db.AppLockDBOpenHelper;
import com.itheima.mobilesafe.service.WatchDogService;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;
	public AppLockDao(Context context) {
		this.context = context;
		helper = new AppLockDBOpenHelper(context);
	}

	/**
	 * 查看是否该应用在应用所数据库中
	 * 
	 * @param packName
	 * @return
	 */
	public boolean findByPackName(String packName) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from t_applock where packname = ?",
					new String[] { packName });
			// db.query("t_applock", new String[]{"packname"}, "packname=?", new
			// String[]{packName}, null, null, null);
			if (cursor.moveToFirst()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	/**
	 * 查找所有添加程序锁的包名
	 * @return
	 */
	public List<String> findAll(){
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select packname from t_applock", null);
			while(cursor.moveToNext()){
				String packName = cursor.getString(0);
				list.add(packName);
			}
		}
		return list;
	}
	
	/**
	 * 添加开启程序锁应用到数据库
	 * 
	 * @param packName
	 */
	public void add(String packName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			String sql = "insert into t_applock(packname) values(?)";
			db.execSQL(sql, new String[] { packName });
			db.close();
			Intent intent = new Intent();
			intent.setAction(WatchDogService.ACTION_APPLOCK_DATACHANG);
			context.sendBroadcast(intent);
		}
	}
	
	/**
	 * 从程序锁数据库中移除应用
	 * @param packName
	 */
	public void delete(String packName){
		SQLiteDatabase db = helper.getWritableDatabase();
		if(db.isOpen()){
			String sql = "delete from t_applock where packname=?";
			db.execSQL(sql, new String[]{packName});
			db.close();
			Intent intent = new Intent();
			intent.setAction(WatchDogService.ACTION_APPLOCK_DATACHANG);
			context.sendBroadcast(intent);
		}
	}

}
