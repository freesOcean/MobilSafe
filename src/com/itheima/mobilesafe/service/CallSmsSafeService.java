package com.itheima.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
/**
 * 监听短信和电话：黑名单的短信终止广播
 * 黑名单的电话：通过反射调用隐藏的API挂断电话，并利用
 * @author ZXJM
 * 2016年10月17日
 */
public class CallSmsSafeService extends Service {
	public static final String SERVICE_NAME = "com.itheima.mobilesafe.service.CallSmsSafeService";
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();
		IntentFilter filter =  new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver,filter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
	
	/**
	 * 内部广播接收者：监听黑名单发送的短信
	 * @author ZXJM
	 * 2016年9月6日
	 */
	private class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"内部广播接受者， 短信到来了");
			//检查发件人是否是黑名单号码，设置短信拦截全部拦截。
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj:objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result)||"3".equals(result)){
					Log.i(TAG,"拦截短信");
					abortBroadcast();
				}
				//演示代码。
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					//你的头发票亮的很  语言分词技术。
					Log.i(TAG,"拦截发票短信");
					abortBroadcast();
				}
			}
		}
	}
	
	
	/**
	 * 电话状态的监听器
	 * @author ZXJM
	 * 2016年9月6日
	 */
	private class MyListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING://零响状态。
				String result = dao.findMode(incomingNumber);
				if("1".equals(result)||"3".equals(result)){
					Log.i(TAG,"挂断电话。。。。");
					//观察通话记录的数据库，删除通话记录
					Uri uri = Uri.parse("content://call_log/calls/");
					ContentResolver resolver = getContentResolver();
					resolver.registerContentObserver(uri, true, new CallLogOberver(new Handler(),resolver,incomingNumber));
					endCall();
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	//通话记录的内容观察者
	class CallLogOberver extends ContentObserver{
		private ContentResolver resolver;
		private String incomingNumber;
		public CallLogOberver(Handler handler,ContentResolver resolver,String number) {
			super(handler);
			this.resolver = resolver;
			this.incomingNumber = number;
		}
	
		/**
		 * 当通话记录的数据库发生变化时回调
		 */
		@Override
		public void onChange(boolean selfChange) {
			//通话记录的Uri
			Uri uri = Uri.parse("content://call_log/calls/");
			resolver.delete(uri, "number=?", new String[]{incomingNumber});
		}
	}
	
	/**
	 * 利用反射机制，调用隐藏的API挂断电话
	 */
	public void endCall() {
//		IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			//加载servicemanager的字节码
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Class clazz2  = Class.forName("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class); 
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
