package com.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.itheima.mobilesafe.service.WatchDogService;
import com.itheima.mobilesafe.utils.ServiceUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("zzzzzz+++++++bootComplete");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			//��������������ִ������ط�
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			
			// ��ȡ֮ǰ�����SiM��Ϣ��
			String saveSim = sp.getString("sim", "")+"afu";
			
			//��ȡ��ǰ��sim����Ϣ
			String realSim = tm.getSimSerialNumber();
			
			//�Ƚ��Ƿ�һ��
			if(saveSim.equals(realSim)){
				//simû�б��������ͬһ������
			}else{
				// sim �Ѿ���� ��һ�����Ÿ���ȫ����
				System.out.println("sim �Ѿ����");
				Toast.makeText(context, "sim �Ѿ����", 1).show();
				SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, "sim changing....", null, null);
			}

		}
		
		//�������������Ź�
		Intent watchDog = new Intent(context, WatchDogService.class);
		if (!ServiceUtils.isServiceRunning(context,
				WatchDogService.SERVICE_NAME)) {
			context.startService(watchDog);
		}
		
	}

}
