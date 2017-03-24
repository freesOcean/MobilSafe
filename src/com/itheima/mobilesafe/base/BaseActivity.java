package com.itheima.mobilesafe.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import com.itheima.mobilesafe.activity.R;
/*
 * activity的基类
 */
public abstract class BaseActivity extends Activity {
	
		private Dialog processDialog;
		private boolean isCancel;//进度条是否消失
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
			super.onCreate(savedInstanceState);
//			initView();
//			registListener();
		}
		
		
//		//父类中的抽象方法，用于初始化控件
//		protected abstract void initView();
//
//		//父类中的抽象方法，用于处理事件监听
//		protected abstract void registListener();
		
		/**
		 * 开启自定义进度条条
		 * @param isFinish 取消当前进度条后是否结束当前activity
		 */
		public void showProcessDialog(){
			processDialog = new Dialog(this, R.style.process_dialog);
			processDialog.setContentView(R.layout.progress_dialog);
			processDialog.setCancelable(true);
			processDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
						isCancel = true;
				}
			});
			processDialog.show();
			
		}
		
		public void closeProcessDialog(){
			if(processDialog!=null){
				processDialog.dismiss();
			}
		}
		
}
