package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 进程实体类
 * @author ZXJM
 * 2016年10月24日
 */
/**
 * @author ZXJM
 * 2016年10月24日
 */
public class ProcessInfo {
	private Drawable icon;//图标
	private String name;//应用名称
	private String packName;//包名
	private long memSize;//占用内存
	private boolean userProcess;//是否为用户进程
	private boolean isChecked;//是否被选中
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isUserProcess() {
		return userProcess;
	}
	public void setUserProcess(boolean userProcess) {
		this.userProcess = userProcess;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	@Override
	public String toString() {
		return "ProcessInfo [name=" + name + ", packName=" + packName
				+ ", memSize=" + memSize + ", userProcess=" + userProcess
				+ ", isChecked=" + isChecked + "]";
	}
	
	
	
}
