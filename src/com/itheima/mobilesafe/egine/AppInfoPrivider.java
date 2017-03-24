package com.itheima.mobilesafe.egine;

import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * ��ȡӦ�õ���Ϣ������
 * @author ZXJM
 * 2016��9��12��
 */
public class AppInfoPrivider {
	/**
	 * ��ȡ�Ѱ�װ��Ӧ�õ���Ϣ
	 * @param context
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		//���Ƴ����嵥�ļ��ļ���
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			AppInfo appInfo = new AppInfo();
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			boolean isRom;
			boolean isSystem;
			int flags = packageInfo.applicationInfo.flags; //Ӧ�ó���ı�ǣ��൱���ύ�Ļ������Ծ�
			//google����ʦ�����㷨��
			if((flags&ApplicationInfo.FLAG_SYSTEM)== 0){
			  //����ϵͳӦ��
				isSystem = false;
			}else{
				//ϵͳӦ��
				isSystem = true;
			}
			
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//û�а�װ��sd��
				isRom = true;
			}else{
			    isRom = false;
			}
			
			appInfo.setAppName(name);
			appInfo.setPackageName(packageName);
			appInfo.setIcon(icon);
			appInfo.setSystemApp(isSystem);
			appInfo.setRom(isRom);
			appInfos.add(appInfo);
			
		}
		return appInfos;
	}
	
	
}