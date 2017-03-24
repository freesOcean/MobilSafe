package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.service.AddressService;
import com.itheima.mobilesafe.service.CallSmsSafeService;
import com.itheima.mobilesafe.service.WatchDogService;
import com.itheima.mobilesafe.ui.SettingClickView;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.ui.UnlockGesturePasswordActivity;
import com.itheima.mobilesafe.utils.ServiceUtils;
import com.itheima.mobilesafe.utils.SharePrefUtils;
import com.itheima.mobilesafe.utils.UIUtils;

public class SettingActivity extends Activity {
	public static final String KEY_APPLOCK = "applock";
	public static final String KEY_UPDATE = "update";
	public static final String KEY_ADDRESS_COLOR = "which";
	// 设置是否开启自动更新
	private SettingItemView siv_update;
	private SharedPreferences sp;

	// 设置是否开启显示归属地
	private SettingItemView siv_show_address;
	private Intent showAddress;

	// 黑名单拦截设置
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;

	// 设置归属地显示框背景
	private SettingClickView scv_changebg;
	private String[] items;// 来电显示背景色数组
	
	//程序锁
	private SettingClickView scv_applock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		registListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this, AddressService.SERVICE_NAME);

		if (isServiceRunning) {
			// 监听来电的服务是开启的
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}

		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this, CallSmsSafeService.SERVICE_NAME);
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);
		
		boolean applock = SharePrefUtils.getBooleanValue(KEY_APPLOCK, false);
		UIUtils.ShowToast("applock"+applock);
		if(applock){
			scv_applock.setDesc("开启");
		}else{
			scv_applock.setDesc("未开启");
		}

	}

	private void initView() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 初始设置是否开启自动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean(KEY_UPDATE, false);
		if (update) {
			// 自动升级已经开启
			siv_update.setChecked(true);
		} else {
			// 自动升级已经关闭
			siv_update.setChecked(false);
		}

		// 设置号码归属地显示控件
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this, AddressService.SERVICE_NAME);
		if (isServiceRunning) {
			// 监听来电的服务是开启的
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}

		// 黑名单拦截设置
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);

		// 设置号码归属地的背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		items = UIUtils.getStringArray(R.array.address_color);
		int which = sp.getInt(KEY_ADDRESS_COLOR, 0);
		scv_changebg.setDesc(items[which]);
		
		//程序锁
		scv_applock = (SettingClickView) findViewById(R.id.scv_applock);
		scv_applock.setTitle("程序锁"); 
		boolean applock = SharePrefUtils.getBooleanValue(KEY_APPLOCK, false);
		if(applock){
			scv_applock.setDesc("开启");
		}else{
			scv_applock.setDesc("未开启");
		}
	}

	private void registListener() {
		// 自动更新
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否有选中
				// 已经打开自动升级了
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					editor.putBoolean(KEY_UPDATE, false);

				} else {
					// 没有打开自动升级
					siv_update.setChecked(true);
					editor.putBoolean(KEY_UPDATE, true);
				}
				editor.commit();
			}
		});

		// 归属地
		showAddress = new Intent(this, AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_show_address.isChecked()) {
					// 变为非选中状态
					siv_show_address.setChecked(false);
					stopService(showAddress);
					System.out.println("停止来电显示服务");
				} else {
					// 选择状态
					siv_show_address.setChecked(true);
					startService(showAddress);
					System.out.println("启动来电显示服务");
				}

			}
		});

		// 黑名单
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// 变为非选中状态
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					// 选择状态
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}

			}
		});

		// 归属地风格
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int dd = sp.getInt(KEY_ADDRESS_COLOR, 0);
				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, dd,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// 保存选择参数
								Editor editor = sp.edit();
								editor.putInt(KEY_ADDRESS_COLOR, which);
								editor.commit();
								scv_changebg.setDesc(items[which]);

								// 取消对话框
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("cancel", null);
				builder.show();

			}
		});

		//程序锁
		scv_applock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in = new Intent(SettingActivity.this, UnlockGesturePasswordActivity.class);
				in.putExtra(WatchDogService.KEY_PACKNAME, getPackageName());
				startActivity(in);
			}
		});
	}
}
