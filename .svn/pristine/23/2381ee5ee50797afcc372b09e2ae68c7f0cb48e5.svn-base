package com.itheima.mobilesafe.utils;

import android.content.Context;

/**
 * 适配工具类
 * @author ZXJM
 * @date 2016年8月29日
 * 像素 =dip * 设备密度 ;
 */
public class DesityUtils {
	
	/**
	 * 将dip转为像素
	 * @param dip
	 * @param context
	 * @return
	 */
	public static int dip2pix(float dip,Context context){
		float density = context.getResources().getDisplayMetrics().density;
		System.out.println("设备密度：+++++++++++"+density);
		int pix = (int) (dip*density+0.5f); //四舍五入的一种方法
		return pix;
	}
	
	/**
	 * 将像素转换为dp
	 * @param pix
	 * @param context
	 * @return
	 */
	public static float pix2dip(int pix,Context context){
		//获取设备密度
		float density = context.getResources().getDisplayMetrics().density;
		float dip = pix/density;
		return dip;
	}
	
	
}
