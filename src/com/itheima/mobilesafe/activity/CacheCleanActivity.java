package com.itheima.mobilesafe.activity;

import android.os.Bundle;
import android.view.View;

import com.itheima.mobilesafe.base.BaseActivity;

public class CacheCleanActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clean);
		
		
	}
	
	public void clean(View view){
//		CacheCleanActivity.class.getClassLoader().loadClass(className);
	}
	
}
