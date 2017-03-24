package com.itheima.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.ui.UnlockGesturePasswordActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

/**
 * ���Ӧ�ó��������
 * 
 * @author ZXJM 2016��11��10��
 */
public class WatchDogService extends Service {
	public static final String SERVICE_NAME = "com.itheima.mobilesafe.service.WatchDogService";
	public static final String ACTION_TEMP_STOP = "com.cattsoft.mobile.action_temp_stop";
	public static final String ACTION_APPLOCK_DATACHANG = "com.cattsoft.mobile.action_applock_data_changed";
	public static final String KEY_PACKNAME = "packName";
	public static final String ACTION_START_WATCHDOG = "com.cattsoft.mobilesafe.keep.start_service";
	private List<String> packNames;
	private AppLockDao dao;
	private boolean flag = true;
	private ActivityManager am;
	private List<String> tempStopPackNames;
	
	private TempStopReceiver stopReceiver;
	private AppLockDataChangedReceiver dataChangeReceiver;
	private ScreenLockReceiver screenLockReceiver;
	private Intent intent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		dao = new AppLockDao(getApplicationContext());
		packNames = dao.findAll();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tempStopPackNames = new ArrayList<String>();
		
		stopReceiver = new TempStopReceiver();
		registerReceiver(stopReceiver, new IntentFilter(ACTION_TEMP_STOP));
		dataChangeReceiver = new AppLockDataChangedReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter(WatchDogService.ACTION_APPLOCK_DATACHANG));
		screenLockReceiver = new ScreenLockReceiver();
		registerReceiver(screenLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		// ��ǰactivity�Ǽ���Ӧ�ã���ת�������������
	    intent = new Intent(getApplicationContext(),
				UnlockGesturePasswordActivity.class);
		// ������û������ջ��Ϣ�ģ��ڷ�����activity��Ҫָ�����activity���е�����ջ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(WatchRunnable).start();
	}

	// ÿ��0.2�룬ɨ���������ջ��ջ�������Ƿ����µ�activity����������Ǽ��������򵯳����������
	private Runnable WatchRunnable = new Runnable() {
		public void run() {
			// ����δ�������Ų�ͣ
			while (flag) {
				List<RunningTaskInfo> tasks = am.getRunningTasks(1);
				RunningTaskInfo taskInfo = tasks.get(0);
				String name = taskInfo.topActivity.getPackageName();
//				System.out.println("watchDog:"+name);
				if(tempStopPackNames.contains(name)){
					//����
				}else if (packNames.contains(name)) {
					
					intent.putExtra(KEY_PACKNAME,
							name);
					startActivity(intent);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	};

	/**
	 * �Ƿ����Ӧ�õĹ㲥������
	 * @author ZXJM
	 * 2016��11��10��
	 */
	private class TempStopReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���շ��еĹ㲥");
//			tempStopPackName = intent.getStringExtra(KEY_PACKNAME);
			if(tempStopPackNames==null){
				tempStopPackNames = new ArrayList<String>();
			}
			tempStopPackNames.add(intent.getStringExtra(KEY_PACKNAME));
		}
	}
	
	/**
	 * ���������ݿⷢ���ı�Ĺ㲥������
	 * @author ZXJM
	 * 2016��11��11��
	 */
	private class AppLockDataChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���ճ��������ݱ仯�Ĺ㲥");
			packNames = dao.findAll();//���»�ȡ����Ӧ�ó�������ݿ�����
		}
		
	}
	
	/**
	 * ������ʱ����շ��е�Ӧ���б�
	 * @author ZXJM
	 * 2016��11��12��
	 */
	public class ScreenLockReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("���յ���Ļ�رյĹ㲥");
			tempStopPackNames.clear();
		}
		
	}
	
	public void onDestroy() {
		Log.i("WatchDog", "dog1�ҹ���");
		// ������һ�����Ź�
		Intent i = new Intent(getApplicationContext(), WatchDogService2.class);
		startService(i);
		flag = false;
//		tempStopPackName = null;
		tempStopPackNames.clear();
		tempStopPackNames = null;
		unregisterReceiver(stopReceiver);
		stopReceiver = null;
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
		unregisterReceiver(screenLockReceiver);
		screenLockReceiver = null;
		
	};
}
