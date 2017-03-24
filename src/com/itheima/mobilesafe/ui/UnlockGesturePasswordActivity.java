package com.itheima.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.activity.AppLockActivity;
import com.itheima.mobilesafe.activity.AppManagerActivity;
import com.itheima.mobilesafe.activity.R;
import com.itheima.mobilesafe.base.MyApplication;
import com.itheima.mobilesafe.service.WatchDogService;
import com.itheima.mobilesafe.ui.LockPatternView.Cell;

public class UnlockGesturePasswordActivity extends Activity {
	
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private LockPatternView mLockPatternView;
	private TextView mHeadTextView;
	private ImageView icon;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private Animation mShakeAnim;
	private Toast mToast;

	// ���洫�ݵ�����
	private Intent intent;
	private String packName;//�ĸ�Ӧ�û����˳���������
	
	private PackageManager pm;
	private Drawable appIcon;

	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}
		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);
		intent = getIntent();
		packName = intent.getStringExtra(WatchDogService.KEY_PACKNAME);
	    pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packName, 0);
			appIcon = applicationInfo.loadIcon(pm);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		initView();
	}

	private void initView() {
		icon = (ImageView) findViewById(R.id.gesturepwd_unlock_face);
		icon.setImageDrawable(appIcon);
		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(false);
		
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
	}

	
	
	@Override
	protected void onResume() {
		super.onResume();

		if (!MyApplication.getInstance().getLockPatternUtils()
				.savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();//�ں�̨���������н�����ǰactivity
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	
	

	//
	public void onBackPressed() {
		// ����Ǵ���ʿ���ý�����룬����������
		if (packName.equals(getPackageName())) {
			super.onBackPressed();
		} else {
			//�ص�����(��С��)
			/**
			 *  <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.MONKEY"/>
            </intent-filter>
			 */
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addCategory("android.intent.category.MONKEY");
			startActivity(intent);
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (MyApplication.getInstance().getLockPatternUtils()
					.checkPattern(pattern)) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);
				if (packName.equals(UnlockGesturePasswordActivity.this
						.getPackageName())) {
					//�����ý�����룬�������������ý���
					Intent intent = new Intent(
							UnlockGesturePasswordActivity.this,
							AppLockActivity.class);
					// ���µ�Activity
					startActivity(intent);
				}else{
					//֪ͨ���Ź�����
					Intent intent = new Intent();
					intent.setAction(WatchDogService.ACTION_TEMP_STOP);
					intent.putExtra(WatchDogService.KEY_PACKNAME, packName);
					sendBroadcast(intent);
					finish();
				}
				showToast("�����ɹ�");
				finish();
			} else {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("����5��������룬��30�������");
						mHeadTextView.setText("������󣬻�����������" + retry + "��");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showToast("���볤�Ȳ�����������");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};
	
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};
	
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " �������");
					} else {
						mHeadTextView.setText("�������������");
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}

			}.start();
		}
	};

}
