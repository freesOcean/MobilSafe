package com.itheima.mobilesafe.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

import com.itheima.mobilesafe.activity.R;
/*
 * activity�Ļ���
 */
public abstract class BaseActivity extends Activity {
	
		private Dialog processDialog;
		private boolean isCancel;//�������Ƿ���ʧ
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ������
			super.onCreate(savedInstanceState);
//			initView();
//			registListener();
		}
		
		
//		//�����еĳ��󷽷������ڳ�ʼ���ؼ�
//		protected abstract void initView();
//
//		//�����еĳ��󷽷������ڴ����¼�����
//		protected abstract void registListener();
		
		/**
		 * �����Զ����������
		 * @param isFinish ȡ����ǰ���������Ƿ������ǰactivity
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
