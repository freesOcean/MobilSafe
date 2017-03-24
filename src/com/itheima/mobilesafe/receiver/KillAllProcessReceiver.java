package com.itheima.mobilesafe.receiver;

import java.util.List;

import com.itheima.mobilesafe.activity.R;
import com.itheima.mobilesafe.utils.UIUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Debug.MemoryInfo;
import android.text.format.Formatter;
import android.widget.RemoteViews;

/**
 * 监听清理进程的广播接收者，当其他进程（widget）发出要清理进程的广播后，该广播接收者收到后，进行清理操作
 * 
 * @author ZXJM 2016年11月1日
 */
public class KillAllProcessReceiver extends BroadcastReceiver {
	public static final String ACTION_CLEAN_PROCESS = "com.itheima.mobilesafe.clean_all";
	private static ActivityManager am;

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("收到广播kill：");
		int count = 0;// 清理的进程数量
		long size = 0;// 清理的内存大小
		am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (RunningAppProcessInfo proc : processes) {
			String packName = proc.processName;
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{proc.pid});
			long memSize = memoryInfos[0].getTotalPrivateDirty()*1024;
			size+=memSize;
			count += 1;
			am.killBackgroundProcesses(packName);
		}
		if (count == 0 || size == 0) {
			UIUtils.ShowToast("没有可以关闭的程序！");
		} else {
			UIUtils.ShowToast("清理了" + count + "个应用，释放了"
					+ Formatter.formatFileSize(context, size) + "内存");
		}
	}

}
