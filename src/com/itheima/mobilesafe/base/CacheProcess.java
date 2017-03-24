package com.itheima.mobilesafe.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/*
 * create by zx 2016/07/20
 * 缓存类：用于存放用户的本地数据
 */
public class CacheProcess {
	private static SharedPreferences sp; //
	private static SharedPreferences.Editor editor;
	private static CacheProcess spUtil = new CacheProcess();
	private static Context mContext;
	
	/*
	 * 保存 key，value 到本地shared_prefs.xml
	 */
	public static void setCacheValue(Context context,String key,String value){
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/*
	 * 根据key值获取保存的数据
	 */
	public static String getCacheValue(Context context,String key){
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(key, "");
	}
	
	
	
}
