package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用信息实体类
 * @author ZXJM
 * 2016年9月12日
 */
public class AppInfo {
	private String appName; //应用程序名
	private String packageName;//应用包名
	private Drawable icon;//应用图标
	private boolean isRom;//安装位置
	private boolean systemApp;//是否是系统应用
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isRom() {
		return isRom;
	}
	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}
	public boolean isSystemApp() {
		return systemApp;
	}
	public void setSystemApp(boolean systemApp) {
		this.systemApp = systemApp;
	}
	
}
