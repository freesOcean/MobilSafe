package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * ����ʵ����
 * @author ZXJM
 * 2016��10��24��
 */
/**
 * @author ZXJM
 * 2016��10��24��
 */
public class ProcessInfo {
	private Drawable icon;//ͼ��
	private String name;//Ӧ������
	private String packName;//����
	private long memSize;//ռ���ڴ�
	private boolean userProcess;//�Ƿ�Ϊ�û�����
	private boolean isChecked;//�Ƿ�ѡ��
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
