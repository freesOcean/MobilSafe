package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {
	private static final String NAME = "applock.db";
	private static final int VERSION = 1;
	public AppLockDBOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table t_applock(_id integer primary key autoincrement,packname varchar(50))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
