package com.itheima.mobilesafe.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;


import com.itheima.mobilesafe.utils.SmsUtils;
import com.itheima.mobilesafe.utils.SmsUtils.BackSmsCallBack;
import com.itheima.mobilesafe.utils.SmsUtils.RestoreSmsCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * 点击事件，进入号码归属地查询的页面
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intentv = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intentv);

	}

	/**
	 * 点击事件，备份短信
	 * 
	 * @param view
	 */
	public void smsBackup(View view) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在备份...");
		pd.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					SmsUtils.backSms(AtoolsActivity.this,
							new BackSmsCallBack() {

								@Override
								public void onBackUp(int progress) {
									pd.setProgress(progress);
								}

								@Override
								public void beforeBackUp(int max) {
									pd.setMax(max);
								}
							});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份成功", 0)
									.show();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败", 0)
									.show();
						}
					});
					e.printStackTrace();
				} finally {
					pd.dismiss();
				}
			}

		}).start();

	}

	/**
	 * 点击事件，还原短信
	 * 
	 * @param view
	 */
	public void restoreSms(View view) {
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在还原..");
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					SmsUtils.restoreSms(AtoolsActivity.this,
							new RestoreSmsCallBack() {

								@Override
								public void onRestoreSms(int progress) {
									pd.setProgress(progress);
								}

								@Override
								public void beforeRestoreSms(int max) {
									pd.setMax(max);
								}
							});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原成功", 0)
									.show();
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原失败", 0)
									.show();
						}
					});
					e.printStackTrace();
				}finally{
					pd.dismiss();
				}
			}
		}).start();
	}

}
