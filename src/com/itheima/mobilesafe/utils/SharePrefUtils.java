package com.itheima.mobilesafe.utils;

import com.itheima.mobilesafe.base.MyApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePrefUtils {
	public static SharedPreferences sp;
	
	
	private static  void initSharedPre(){
		if(sp==null){
			sp = getContext(). getSharedPreferences("config", getContext().MODE_PRIVATE);
		}
	}
	
	private static  Context getContext() {
        return MyApplication.getContext();
    }
	
	public static void setStringValue(String key,String value){
		if(sp==null){
			initSharedPre();
		}
		//保存选择参数
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void setBooleanValue(String key,boolean value){
		if(sp==null){
			initSharedPre();
		}
		//保存选择参数
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void setIntValue(String key,int value){
		if(sp==null){
			initSharedPre();
		}
		//保存选择参数
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static String getStringValue(String key,String defValue){
		if(sp==null){
			initSharedPre();
		}
		String value = sp.getString(key, defValue);
		return value;
	}
	
	public static boolean getBooleanValue(String key,boolean defValue){
		if(sp==null){
			initSharedPre();
		}
		boolean value = sp.getBoolean(key, defValue);
		return value;
	}
	
	public static int getIntValue(String key,int defValue){
		if(sp==null){
			initSharedPre();
		}
		int value = sp.getInt(key, defValue);
		return value;
	}
	
	
}
