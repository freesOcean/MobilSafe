package com.itheima.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.itheima.mobilesafe.activity.R;
import com.itheima.mobilesafe.receiver.KillAllProcessReceiver;
import com.itheima.mobilesafe.receiver.WidgetPrivider;
import com.itheima.mobilesafe.utils.AppSystemUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class WidgetCleanService extends Service {
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	private ScreenOnReceiver screenOnReceiver;
	private ScreenOffReceiver screenOffReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("�յ���Ļ�����Ĺ㲥");
			startTimer();
		}
		
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("�յ���Ļ�رյĹ㲥");
			stopTimer();
		}
	}
	
	@Override
	public void onCreate() {
		screenOnReceiver = new ScreenOnReceiver();
		IntentFilter sreenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		registerReceiver(screenOnReceiver, sreenOnFilter);
		
		screenOffReceiver = new ScreenOffReceiver();
		IntentFilter sreenOnffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenOnReceiver, sreenOnffFilter);
		
		startTimer();
	}

	//��ʼ��ʱ����С�ؼ�����
	private void startTimer() {
		if (timer == null && task == null) {
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					awm = AppWidgetManager.getInstance(WidgetCleanService.this);

					ComponentName provider = new ComponentName(
							WidgetCleanService.this, WidgetPrivider.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);
					views.setTextViewText(
							R.id.process_count,
							"�������еĽ���:"
									+ AppSystemUtils
											.getRunningProcessCount(WidgetCleanService.this)
									+ "��");
					long size = AppSystemUtils
							.getaVaiMemory(WidgetCleanService.this);
					views.setTextViewText(
							R.id.process_memory,
							"�����ڴ�:"
									+ Formatter.formatFileSize(
											WidgetCleanService.this, size));
					Intent intent = new Intent();
					intent.setAction(KillAllProcessReceiver.ACTION_CLEAN_PROCESS);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							WidgetCleanService.this, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(provider, views);
					System.out.println("��ʱ���½���...");
				}
			};
			timer.schedule(task, 0, 3000);
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(screenOnReceiver);
		unregisterReceiver(screenOffReceiver);
		screenOnReceiver = null;
		screenOffReceiver = null;
		stopTimer();
	}

	//ֹͣ��ʱ����С�ؼ�����
	private void stopTimer() {
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

}
