package com.itheima.mobilesafe.receiver;

import com.itheima.mobilesafe.service.WidgetCleanService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class WidgetPrivider extends AppWidgetProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,WidgetCleanService.class);
		context.startService(i);
		super.onReceive(context, intent);
		System.out.println("onReceive");
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onUpdate");
	}
	
	@Override
	public void onDisabled(Context context) {
		Intent i = new Intent(context,WidgetCleanService.class);
		context.stopService(i);
		super.onDisabled(context);
		System.out.println("onDisabled");
	}
	
	@Override
	public void onEnabled(Context context) {
		Intent i = new Intent(context,WidgetCleanService.class);
		context.startService(i);
		super.onEnabled(context);
		System.out.println("onEnabled");
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");
	}
	
}
