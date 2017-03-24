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
	// �����Ƿ����Զ�����
	private SettingItemView siv_update;
	private SharedPreferences sp;

	// �����Ƿ�����ʾ������
	private SettingItemView siv_show_address;
	private Intent showAddress;

	// ��������������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;

	// ���ù�������ʾ�򱳾�
	private SettingClickView scv_changebg;
	private String[] items;// ������ʾ����ɫ����
	
	//������
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
			// ��������ķ����ǿ�����
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
			scv_applock.setDesc("����");
		}else{
			scv_applock.setDesc("δ����");
		}

	}

	private void initView() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// ��ʼ�����Ƿ����Զ�����
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		boolean update = sp.getBoolean(KEY_UPDATE, false);
		if (update) {
			// �Զ������Ѿ�����
			siv_update.setChecked(true);
		} else {
			// �Զ������Ѿ��ر�
			siv_update.setChecked(false);
		}

		// ���ú����������ʾ�ؼ�
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this, AddressService.SERVICE_NAME);
		if (isServiceRunning) {
			// ��������ķ����ǿ�����
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}

		// ��������������
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);

		// ���ú�������صı���
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("��������ʾ����");
		items = UIUtils.getStringArray(R.array.address_color);
		int which = sp.getInt(KEY_ADDRESS_COLOR, 0);
		scv_changebg.setDesc(items[which]);
		
		//������
		scv_applock = (SettingClickView) findViewById(R.id.scv_applock);
		scv_applock.setTitle("������"); 
		boolean applock = SharePrefUtils.getBooleanValue(KEY_APPLOCK, false);
		if(applock){
			scv_applock.setDesc("����");
		}else{
			scv_applock.setDesc("δ����");
		}
	}

	private void registListener() {
		// �Զ�����
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// �ж��Ƿ���ѡ��
				// �Ѿ����Զ�������
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					editor.putBoolean(KEY_UPDATE, false);

				} else {
					// û�д��Զ�����
					siv_update.setChecked(true);
					editor.putBoolean(KEY_UPDATE, true);
				}
				editor.commit();
			}
		});

		// ������
		showAddress = new Intent(this, AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_show_address.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_show_address.setChecked(false);
					stopService(showAddress);
					System.out.println("ֹͣ������ʾ����");
				} else {
					// ѡ��״̬
					siv_show_address.setChecked(true);
					startService(showAddress);
					System.out.println("����������ʾ����");
				}

			}
		});

		// ������
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					// ѡ��״̬
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}

			}
		});

		// �����ط��
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int dd = sp.getInt(KEY_ADDRESS_COLOR, 0);
				// ����һ���Ի���
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				builder.setSingleChoiceItems(items, dd,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// ����ѡ�����
								Editor editor = sp.edit();
								editor.putInt(KEY_ADDRESS_COLOR, which);
								editor.commit();
								scv_changebg.setDesc(items[which]);

								// ȡ���Ի���
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("cancel", null);
				builder.show();

			}
		});

		//������
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
