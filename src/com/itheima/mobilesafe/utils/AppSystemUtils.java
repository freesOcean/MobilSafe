package com.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * 获取手机某个目录的存储空间工具类
 * @author ZXJM
 * 2016年9月12日
 */
public class AppSystemUtils {
	
	/**
	 * 获取指定目录的可用空间
	 */
	public static String getAvaiSpace(Context context,String path){
		StatFs sf = new StatFs(path);
		long avaiCount = sf.getAvailableBlocks();
		int blockSize = sf.getBlockSize();
		String size = Formatter.formatFileSize(context, avaiCount*blockSize);
		return size;
	}
	
	/**
	 * 获取指定目录的空间
	 */
	public static String getTotalSpace(Context context,String path){
		StatFs sf = new StatFs(path);
		long count = sf.getBlockCount();
		int blockSize = sf.getBlockSize();
		String size = Formatter.formatFileSize(context, count*blockSize);
		return size;
	}
	
	
	
	/*******************进程相关***************/
	
	/**
	 * 获取正在运行的进程个数
	 */
	public static  int getRunningProcessCount(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
		return processes.size();
	}
	
	
	/**
	 * 获取可用的内存空间
	 * 
	 */
	public static long getaVaiMemory(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * 获取总的内存空间(此API对低版本不适用，必须是4.0以上版本)
	 * 
	 */
//	public static long getTotalMemory(Context context){
//		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new MemoryInfo();
//		activityManager.getMemoryInfo(outInfo);
//		return outInfo.totalMem;
//	}
//	
	/**
	 * 获取总的内存空间(兼容写法),返回转换为byte
	 * 
	 */
	public static long getTotalMemory2(Context context){
		
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
			String line = br.readLine();
			StringBuffer sb = new StringBuffer();
			for (char c : line.toCharArray()) {
				if(c>='0' && c<='9'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/***************************************/
	
	
}
