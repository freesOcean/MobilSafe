package com.itheima.mobilesafe.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.itheima.mobilesafe.ui.LockPatternUtils;

/**
 * 全局管理类
 * 
 * @author Administrator
 *
 */
public  class MyApplication extends Application {
	private static MyApplication mInstance;
	private LockPatternUtils mLockPatternUtils;
	private static Context context;
	private static Handler handler;
	private static int mainThreadId;

	public static MyApplication getInstance() {
		return mInstance;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mLockPatternUtils = new LockPatternUtils(this);
		this.context = getApplicationContext();
		this.handler = new Handler();
		this.mainThreadId = android.os.Process.myTid();
	}

	public static Handler getHandler() {
		return handler;
	}

	public static int getMainThreadId() {
		return mainThreadId;
	}

	public static Context getContext() {
		return context;
	}
	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}
}
