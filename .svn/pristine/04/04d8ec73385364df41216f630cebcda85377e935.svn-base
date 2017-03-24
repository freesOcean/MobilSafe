package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	// 备份短信的回调接口
	public interface BackSmsCallBack {
		/**
		 * 备份之前设置进度最大值
		 * 
		 * @param max
		 */
		public abstract void beforeBackUp(int max);

		/**
		 * 备份中，设置备份的进度
		 * 
		 * @param progress
		 */
		public abstract void onBackUp(int progress);
	}

	public static void backSms(Context context, BackSmsCallBack backSmsCallBack)
			throws Exception {
		// 短信Uri
		Uri uri = Uri.parse("content://sms/");
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "address", "date",
				"type", "body" }, null, null, null);
		// 序列化短息到本地
		File file = new File(Environment.getExternalStorageDirectory(),
				"smsbackup.xml");
		FileOutputStream os = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(os, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");

		if (cursor != null && cursor.getCount() > 0) {
			// 备份回调
			serializer.attribute(null, "max", cursor.getCount() + "");
			backSmsCallBack.beforeBackUp(cursor.getCount());
			while (cursor.moveToNext()) {
				// 模拟大量短信备份时的效果
				Thread.sleep(1000);
				System.out.println("短信有数据");
				serializer.startTag(null, "sms");
				String address = cursor.getString(0);
				String date = cursor.getString(1);
				String type = cursor.getString(2);
				String body = cursor.getString(3);

				serializer.startTag(null, "address");
				serializer.text(address);
				serializer.endTag(null, "address");

				serializer.startTag(null, "date");
				serializer.text(date);
				serializer.endTag(null, "date");

				serializer.startTag(null, "type");
				serializer.text(type);
				serializer.endTag(null, "type");

				serializer.startTag(null, "body");
				serializer.text(body);
				serializer.endTag(null, "body");

				serializer.endTag(null, "sms");
				backSmsCallBack.onBackUp(cursor.getPosition());
			}
		}
		serializer.endTag(null, "smss");
		serializer.endDocument();
		cursor.close();
		os.flush();
		os.close();
	}

	/**
	 * 还原短信
	 * 
	 * @throws Exception
	 */
	public static void restoreSms(Context context, RestoreSmsCallBack callBack)
			throws Exception {
		Thread.sleep(1000);
		// 短信Uri
		Uri uri = Uri.parse("content://sms/");
		ContentResolver resolver = context.getContentResolver();
		// 1.读取xml
		XmlPullParser parser = Xml.newPullParser();
		// FileInputStream is = new FileInputStream(Environment
		// .getExternalStorageDirectory().getAbsolutePath()
		// + "/smsbackup.xml");
		File file = new File(Environment.getExternalStorageDirectory(),
				"smsbackup.xml");
		FileInputStream is = new FileInputStream(file);
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();// 获取事件类型（有开始和结束）
		System.out.println("type："+type);
		ContentValues values = null;// 要还原到数据库的数据
		int progress = 0;
		System.out.println("准备解析xml");
		while (type != XmlPullParser.END_DOCUMENT) {
			String tagName = parser.getName();// 获取当前的节点名称
			switch (type) {
			case XmlPullParser.START_TAG:
				if (tagName.equals("smss")) {
					// 获取保存的短信数量，用于更新界面进度
					String strmax = parser.getAttributeValue(null, "max");
					// 回调处理
					int max = Integer.parseInt(strmax);
					System.out.println("max："+max +"strmax:"+strmax);
					callBack.beforeRestoreSms(max);
				} else if (tagName.equals("sms")) {
					values = new ContentValues();
				} else if (tagName.equals("address")) {
					values.put("address", parser.nextText());
				} else if (tagName.equals("date")) {
					values.put("date", parser.nextText());
				} else if (tagName.equals("type")) {
					values.put("type", parser.nextText());
				} else if (tagName.equals("body")) {
					values.put("body", parser.nextText());
				}

				break;
			case XmlPullParser.END_TAG:
				if (tagName.equals("sms")) {
					// 将短信数据写到短信数据库
					resolver.insert(uri, values);
					System.out.println("插入数据");
					progress++;
					callBack.onRestoreSms(progress);
				}
			}
			System.out.println("继续循环");
			type = parser.next();// 更新事件类型
			System.out.println("新type:"+type);
		}
	}

	/*
	 * 还原短信时的回调接口
	 */
	public interface RestoreSmsCallBack {

		/**
		 * 还原短信前，设置进度条的最大位置
		 * 
		 * @param max
		 */
		public abstract void beforeRestoreSms(int max);

		/**
		 * 还原短信时，设置进度条的位置
		 * 
		 * @param progress
		 */
		public abstract void onRestoreSms(int progress);

	}

}
