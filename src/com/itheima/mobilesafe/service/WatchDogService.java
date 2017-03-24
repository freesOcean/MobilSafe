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
 * 监测应用程序的启动
 * 
 * @author ZXJM 2016年11月10日
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
		
		// 当前activity是加锁应用，跳转到输入密码界面
	    intent = new Intent(getApplicationContext(),
				UnlockGesturePasswordActivity.class);
		// 服务是没有任务栈信息的，在服务开启activity，要指定这个activity运行的任务栈
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(WatchRunnable).start();
	}

	// 每隔0.2秒，扫描最近任务栈的栈顶，看是否有新的activity开启，如果是加锁程序，则弹出密码输入框
	private Runnable WatchRunnable = new Runnable() {
		public void run() {
			// 服务未死，看门不停
			while (flag) {
				List<RunningTaskInfo> tasks = am.getRunningTasks(1);
				RunningTaskInfo taskInfo = tasks.get(0);
				String name = taskInfo.topActivity.getPackageName();
//				System.out.println("watchDog:"+name);
				if(tempStopPackNames.contains(name)){
					//放行
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
	 * 是否放行应用的广播接收者
	 * @author ZXJM
	 * 2016年11月10日
	 */
	private class TempStopReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收放行的广播");
//			tempStopPackName = intent.getStringExtra(KEY_PACKNAME);
			if(tempStopPackNames==null){
				tempStopPackNames = new ArrayList<String>();
			}
			tempStopPackNames.add(intent.getStringExtra(KEY_PACKNAME));
		}
	}
	
	/**
	 * 程序锁数据库发生改变的广播接收者
	 * @author ZXJM
	 * 2016年11月11日
	 */
	private class AppLockDataChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收程序锁数据变化的广播");
			packNames = dao.findAll();//重新获取加锁应用程序的数据库数据
		}
		
	}
	
	/**
	 * 当锁屏时，清空放行的应用列表
	 * @author ZXJM
	 * 2016年11月12日
	 */
	public class ScreenLockReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收到屏幕关闭的广播");
			tempStopPackNames.clear();
		}
		
	}
	
	public void onDestroy() {
		Log.i("WatchDog", "dog1我挂了");
		// 启动另一个看门狗
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
