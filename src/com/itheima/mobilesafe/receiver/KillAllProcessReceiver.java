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
 * ����������̵Ĺ㲥�����ߣ����������̣�widget������Ҫ������̵Ĺ㲥�󣬸ù㲥�������յ��󣬽����������
 * 
 * @author ZXJM 2016��11��1��
 */
public class KillAllProcessReceiver extends BroadcastReceiver {
	public static final String ACTION_CLEAN_PROCESS = "com.itheima.mobilesafe.clean_all";
	private static ActivityManager am;

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("�յ��㲥kill��");
		int count = 0;// ����Ľ�������
		long size = 0;// ������ڴ��С
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
			UIUtils.ShowToast("û�п��Թرյĳ���");
		} else {
			UIUtils.ShowToast("������" + count + "��Ӧ�ã��ͷ���"
					+ Formatter.formatFileSize(context, size) + "�ڴ�");
		}
	}

}
