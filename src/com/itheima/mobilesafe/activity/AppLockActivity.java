package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.itheima.mobilesafe.base.BaseActivity;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.egine.AppInfoPrivider;
import com.itheima.mobilesafe.service.WatchDogService;
import com.itheima.mobilesafe.ui.SlideSwitchView;
import com.itheima.mobilesafe.ui.SlideSwitchView.OnSwitchChangedListener;
import com.itheima.mobilesafe.utils.DesityUtils;
import com.itheima.mobilesafe.utils.ServiceUtils;
import com.itheima.mobilesafe.utils.SharePrefUtils;
import com.itheima.mobilesafe.utils.UIUtils;

public class AppLockActivity extends BaseActivity {
	private TextView tvLockApps;
	private TextView tvUnLockApps;
	private ListView lvAppList;
	private SlideSwitchView slideButton;
	private List<AppInfo> appInfos;
	private List<AppInfo> lockApps;
	private List<AppInfo> unLockApps;
	private List<AppInfo> unLockSysApps;//未加锁的系统程序：用于排序中间变量
	private AppListAdapter adapter;
	private TextView tvTag;
	private AppLockDao dao;
	private Intent watchDog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		watchDog = new Intent(AppLockActivity.this, WatchDogService.class);
		dao = new AppLockDao(this);
		initView();
		getAppInfo();
		registListener();
	}

	private void initView() {
		tvLockApps = (TextView) findViewById(R.id.tv_lock_count);
		tvUnLockApps = (TextView) findViewById(R.id.tv_unlock_count);
		lvAppList = (ListView) findViewById(R.id.lv_app_list);
		tvTag = (TextView) findViewById(R.id.tv_tag);
		slideButton = (SlideSwitchView) findViewById(R.id.slidebtn_lock);
		boolean isLock = SharePrefUtils.getBooleanValue(SettingActivity.KEY_APPLOCK, false);
		if(isLock){
			slideButton.setChecked(true);
			if (!ServiceUtils.isServiceRunning(getApplicationContext(),
					WatchDogService.SERVICE_NAME)) {
				startService(watchDog);
			}
		}else{
			slideButton.setChecked(false);
			if (ServiceUtils.isServiceRunning(getApplicationContext(),
					WatchDogService.SERVICE_NAME)) {
				stopService(watchDog);
			}
		}
		
	}

	/**
	 * 获取数据，刷新界面
	 */
	private void getAppInfo() {
		lockApps = new ArrayList<AppInfo>();
		unLockApps = new ArrayList<AppInfo>();
		unLockSysApps = new ArrayList<AppInfo>();
		showProcessDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				appInfos = AppInfoPrivider.getAppInfos(AppLockActivity.this);
				for (AppInfo appInfo : appInfos) {
					if (dao.findByPackName(appInfo.getPackageName())) {
						lockApps.add(appInfo);// 加锁应用
					} else {
						if(appInfo.isSystemApp()){
							unLockSysApps.add(appInfo);
						}else{
							unLockApps.add(appInfo);// 未加锁应用
						}
					}
				}
				unLockApps.addAll(unLockSysApps);
				//对未加锁的应用进程排序分类，用户程序放前面
				
				// 在主界面更新ui
				runOnUiThread(new Runnable() {
					public void run() {
						if (adapter == null) {
							adapter = new AppListAdapter();
							lvAppList.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}

						tvLockApps.setText(+ lockApps.size() + "个");
						tvUnLockApps.setText( + unLockApps.size() + "个");

						// 隐藏进度对话框
						closeProcessDialog();
					}
				});
			}
		}).start();

	}

	private void registListener() {
		// 列表滑动事件
		lvAppList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			/**
			 * 
			 * view：ListView对象 firstVisibleItem： 第一个可见条目在listview集合里面的位置。
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (unLockApps != null && lockApps != null) {
					if (firstVisibleItem > lockApps.size()) {
						tvTag.setText("未加锁程序：" + unLockApps.size());
					} else {
						if(lockApps.size()<=0){
//							return;
						}
						tvTag.setText("加锁程序：" + lockApps.size());
					}
				}
			}
		});
		
		//程序锁是否开启
		slideButton.setOnChangeListener(new OnSwitchChangedListener() {
			@Override
			public void onSwitchChange(SlideSwitchView switchView, boolean isChecked) {
				
				if(isChecked){
					UIUtils.ShowToast("程序锁开启");
					SharePrefUtils.setBooleanValue(SettingActivity.KEY_APPLOCK, true);
					startService(watchDog);
				}else{
					UIUtils.ShowToast("程序锁关闭");
					SharePrefUtils.setBooleanValue(SettingActivity.KEY_APPLOCK, false);
					stopService(watchDog);
				}
			}
		});

	}

	class AppListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return unLockApps.size() + 1 + lockApps.size() + 1;
		}

		@Override
		public AppInfo getItem(int position) {
			return appInfos.get(position);
			// return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final AppInfo app;
			if (position == 0) {// 加锁应用标签
				TextView tv = new TextView(AppLockActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				int pix = DesityUtils.dip2pix(5, getApplicationContext());
				tv.setPadding(pix, pix, pix, pix);
				tv.setTextSize(16);
				tv.setText("加锁应用：" + lockApps.size() + "个");
				return tv;

			} else if (position <= lockApps.size()) {// 加锁程序
				int newPosition = position - 1;
				app = lockApps.get(newPosition);
			} else if (position == (lockApps.size() + 1)) {// 未加锁应用标签
				TextView tv = new TextView(AppLockActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				int pix = DesityUtils.dip2pix(5, getApplicationContext());
				tv.setPadding(pix, pix, pix, pix);
				tv.setTextSize(16);
				tv.setText("未加锁应用：" + unLockApps.size() + "个");
				return tv;
			} else {// 未加锁应用
				int newPosition = position - 1 - lockApps.size() - 1;
				System.out.println("newPosition:" + newPosition);
				System.out.println("position:" + position);
				System.out.println("systemApps:" + unLockApps.size());
				System.out.println("userApps:" + lockApps.size());
				app = unLockApps.get(newPosition);
			}
			ViewHolder holder = null;
			if (convertView != null && (convertView instanceof RelativeLayout)) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				holder = new ViewHolder();
				convertView = View.inflate(AppLockActivity.this,
						R.layout.list_item_app_lock, null);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.appName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.appType = (TextView) convertView
						.findViewById(R.id.tv_location);
				holder.toggle = (ToggleButton) convertView
						.findViewById(R.id.toggle_lock);
				convertView.setTag(holder);
			}

			holder.icon.setImageDrawable(app.getIcon());
			holder.appName.setText(app.getAppName());
			if (app.isSystemApp()) {
				holder.appType.setText("系统应用");
			} else {
				holder.appType.setText("第三方应用");
			}
			if (dao.findByPackName(app.getPackageName())) {
				holder.toggle.setChecked(true);
			} else {
				holder.toggle.setChecked(false);
			}
			holder.toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					if(isChecked){
						//选中
						UIUtils.ShowToast("添加程序锁");
						dao.add(app.getPackageName());
					}else{
						UIUtils.ShowToast("取消程序锁");
						dao.delete(app.getPackageName());
					}
				}
			});
			return convertView;
		}
	}

	static class ViewHolder {
		public ImageView icon;
		public TextView appName;
		public TextView appType;
		public ToggleButton toggle;
	}

	@Override
	protected void onDestroy() {
		// 防止关闭activity时报错
		// dismissPopwindow();
		super.onDestroy();
	}

}
