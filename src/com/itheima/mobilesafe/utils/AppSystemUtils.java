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
 * ��ȡ�ֻ�ĳ��Ŀ¼�Ĵ洢�ռ乤����
 * @author ZXJM
 * 2016��9��12��
 */
public class AppSystemUtils {
	
	/**
	 * ��ȡָ��Ŀ¼�Ŀ��ÿռ�
	 */
	public static String getAvaiSpace(Context context,String path){
		StatFs sf = new StatFs(path);
		long avaiCount = sf.getAvailableBlocks();
		int blockSize = sf.getBlockSize();
		String size = Formatter.formatFileSize(context, avaiCount*blockSize);
		return size;
	}
	
	/**
	 * ��ȡָ��Ŀ¼�Ŀռ�
	 */
	public static String getTotalSpace(Context context,String path){
		StatFs sf = new StatFs(path);
		long count = sf.getBlockCount();
		int blockSize = sf.getBlockSize();
		String size = Formatter.formatFileSize(context, count*blockSize);
		return size;
	}
	
	
	
	/*******************�������***************/
	
	/**
	 * ��ȡ�������еĽ��̸���
	 */
	public static  int getRunningProcessCount(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
		return processes.size();
	}
	
	
	/**
	 * ��ȡ���õ��ڴ�ռ�
	 * 
	 */
	public static long getaVaiMemory(Context context){
		ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		activityManager.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * ��ȡ�ܵ��ڴ�ռ�(��API�ԵͰ汾�����ã�������4.0���ϰ汾)
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
	 * ��ȡ�ܵ��ڴ�ռ�(����д��),����ת��Ϊbyte
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
