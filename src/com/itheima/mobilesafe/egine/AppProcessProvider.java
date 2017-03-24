package com.itheima.mobilesafe.egine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.itheima.mobilesafe.activity.R;
import com.itheima.mobilesafe.domain.ProcessInfo;

/**
 * 应用进程信息的引擎类
 * @author ZXJM
 * 2016年10月24日
 */
public class AppProcessProvider {
	public static List<ProcessInfo> getAppProcesses(Context context) {
		List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo proc : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			String packName = proc.processName;
			processInfo.setPackName(packName);
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{proc.pid});
			long memSize = memoryInfos[0].getTotalPrivateDirty()*1024;
			processInfo.setMemSize(memSize);
			try {
				
				
//				PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packName, 0);
				int flag = applicationInfo.flags;
				if((flag&ApplicationInfo.FLAG_SYSTEM)==0){
					processInfo.setUserProcess(true);
				}else{
					processInfo.setUserProcess(false);
				}
				
				String name = applicationInfo.loadLabel(pm).toString();
				processInfo.setName(name);
				
				Drawable icon = applicationInfo.loadIcon(pm);
				processInfo.setIcon(icon);
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				processInfo.setName(proc.processName);
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.default_icon));
			}
			processes.add(processInfo);
			
		}
		return  processes;
	}
}
