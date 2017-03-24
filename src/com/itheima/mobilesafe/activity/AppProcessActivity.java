package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.base.BaseActivity;
import com.itheima.mobilesafe.domain.ProcessInfo;
import com.itheima.mobilesafe.egine.AppProcessProvider;
import com.itheima.mobilesafe.utils.AppSystemUtils;
import com.itheima.mobilesafe.utils.UIUtils;

public class AppProcessActivity extends BaseActivity implements OnClickListener {

	private TextView runningProcess;
	private TextView info;
	private TextView flag;
	private ListView lvProcessList;
	private int runningCount;// 正在运行的进程数
	private long aviMem; // 可用内存
	private long totalMem;// 总内存
	private List<ProcessInfo> allProcessInfos;
	private List<ProcessInfo> userProcessInfos;
	private List<ProcessInfo> sysProcessInfos;
	private ProcessListAdapter mAdapter;

	private Button btSelect;
	private Button btClean;
	private Button btSelectOther;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_process);
		initView();
		fillData();
		registListener();
	}

	private void initView() {
		runningProcess = (TextView) findViewById(R.id.tv_running_process);
		info = (TextView) findViewById(R.id.tv_process_info);
		flag = (TextView) findViewById(R.id.tv_tag);
		lvProcessList = (ListView) findViewById(R.id.lv_process_list);
		btSelect = (Button) findViewById(R.id.bt_select_all);
		btClean = (Button) findViewById(R.id.bt_clean);
		btSelectOther = (Button) findViewById(R.id.bt_select_other);
	}

	private void registListener() {
		// 列表滑动事件
		lvProcessList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (sysProcessInfos != null && userProcessInfos != null) {
					if (firstVisibleItem > userProcessInfos.size()) {
						flag.setText("系统进程：" + sysProcessInfos.size() + "个");
					} else {
						flag.setText("用户进程：" + userProcessInfos.size() + "个");
					}
				}
			}
		});
		// 列表点击事件
		lvProcessList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProcessInfo processInfo;
				if (position == 0) {// 用户进程标签
					return;
				} else if (position == userProcessInfos.size() + 1) {// 系统进程标签
					return;
				} else if (position < userProcessInfos.size() + 1) {// 用户进程
					int newPosition = position - 1;
					processInfo = userProcessInfos.get(newPosition);
				} else {// 系统进程
					int newPosition = position - 1 - 1
							- userProcessInfos.size();
					processInfo = sysProcessInfos.get(newPosition);
				}
				//当前应用不允许点击
				if(processInfo.getPackName().equals(getPackageName())){
					return;
				}	
				processInfo.setChecked(!processInfo.isChecked());
				mAdapter.notifyDataSetChanged();
			}
		});
		// 全选按钮
		btSelect.setOnClickListener(this);
		// 反选按钮
		btSelectOther.setOnClickListener(this);
		// 一键清理
		btClean.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			UIUtils.ShowToast("全选");
			for (ProcessInfo p : allProcessInfos) {// 由于userProcessInfos和sysProcessInfos中存放的对象
													// 都是来自于allProcessInfos，都是同一个引用，内存地址没变
				if(p.getPackName().equals(getPackageName())){
					continue;
				}									
				p.setChecked(true);
			}
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.bt_select_other:
			UIUtils.ShowToast("反选");
			for (ProcessInfo p : allProcessInfos) {
				if(p.getPackName().equals(getPackageName())){
					continue;
				}
				p.setChecked(!p.isChecked());
			}
			mAdapter.notifyDataSetChanged();

			break;
		case R.id.bt_clean:
			List<ProcessInfo> checkedList = new ArrayList<ProcessInfo>();
			ActivityManager am = (ActivityManager) this
					.getSystemService(this.ACTIVITY_SERVICE);
			int count = 0;
			long mem = 0;
			for (ProcessInfo p : allProcessInfos) {
				if (p.isChecked()) {
					String packName = p.getPackName();
					am.killBackgroundProcesses(packName);
					checkedList.add(p);
					if(p.isUserProcess()){
						userProcessInfos.remove(p);
					}else{
						sysProcessInfos.remove(p);
					}
					count++;
					mem+=p.getMemSize();
				}
			}
//			allProcessInfos.removeAll(checkedList);
			mAdapter.notifyDataSetChanged();
			
			runningCount-=count;
			aviMem+=mem;
			runningProcess.setText(runningCount + "个");
			info.setText(Formatter.formatFileSize(this, aviMem) + "/"
					+ Formatter.formatFileSize(this, totalMem));
			UIUtils.ShowToast("清理了"+count+"个应用，释放了"+Formatter.formatFileSize(UIUtils.getContext(), mem)+"的内存");
			break;
		}
	}

	private void fillData() {
		setTitle();
		userProcessInfos = new ArrayList<ProcessInfo>();
		sysProcessInfos = new ArrayList<ProcessInfo>();
		showProcessDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				allProcessInfos = AppProcessProvider
						.getAppProcesses(AppProcessActivity.this);
				for (ProcessInfo pro : allProcessInfos) {
					if (pro.isUserProcess()) {
						userProcessInfos.add(pro);
					} else {
						sysProcessInfos.add(pro);
					}
				}
				UIUtils.RunOnUIThread(new Runnable() {

					@Override
					public void run() {
						if (mAdapter == null) {
							mAdapter = new ProcessListAdapter();
							lvProcessList.setAdapter(mAdapter);
						} else {
							mAdapter.notifyDataSetChanged();
						}
						closeProcessDialog();
					}
				});
			}
		}).start();
	}

	private void setTitle() {
		runningCount = AppSystemUtils.getRunningProcessCount(this);
		runningProcess.setText(runningCount + "个");
		aviMem = AppSystemUtils.getaVaiMemory(this);
		totalMem = AppSystemUtils.getTotalMemory2(this);
		System.out.println("总内存："+totalMem/1024/1024);
		info.setText(Formatter.formatFileSize(this, aviMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}

	class ProcessListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userProcessInfos.size() + 1 + sysProcessInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ProcessInfo processInfo;
			if (position == 0) {
				TextView tv = new TextView(AppProcessActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				int pix = UIUtils.dip2px(5);
				tv.setPadding(pix, pix, pix, pix);
				tv.setText("用户进程：" + userProcessInfos.size() + "个");
				tv.setTextSize(16);
				return tv;
			} else if (position == userProcessInfos.size() + 1) {
				TextView tv = new TextView(AppProcessActivity.this);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				int pix = UIUtils.dip2px(5);
				tv.setPadding(pix, pix, pix, pix);
				tv.setText("系统进程：" + sysProcessInfos.size() + "个");
				tv.setTextSize(16);
				return tv;
			} else if (position <= userProcessInfos.size()) {
				int newPosition = position - 1;
				processInfo = userProcessInfos.get(newPosition);
			} else {
				int newPosition = position - userProcessInfos.size() - 1 - 1;
				processInfo = sysProcessInfos.get(newPosition);
			}
			ViewHolder holder = null;
			if (convertView != null && (convertView instanceof LinearLayout)) {
				Log.i("zhangxu", "复用view");
				holder = (ViewHolder) convertView.getTag();
			} else {
				Log.i("zhangxu", "创建新的view");
				holder = new ViewHolder();
				convertView = UIUtils.inflate(R.layout.list_item_process);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.memSize = (TextView) convertView
						.findViewById(R.id.tv_memsize);
				holder.ck = (CheckBox) convertView.findViewById(R.id.ck_clean);

				convertView.setTag(holder);

			}
			holder.icon.setImageDrawable(processInfo.getIcon());
			holder.name.setText(processInfo.getName());
			holder.memSize.setText("占用内存："
					+ Formatter.formatFileSize(AppProcessActivity.this,
							processInfo.getMemSize()));
			if (processInfo.isChecked()) {
				holder.ck.setChecked(true);
			} else {
				holder.ck.setChecked(false);
			}
			
			if(processInfo.getPackName().equals(getPackageName())){
				holder.ck.setVisibility(View.INVISIBLE);
			}else{
				holder.ck.setVisibility(View.VISIBLE);//一定要加，涉及到复用
			}
			
			return convertView;
		}

		@Override
		public ProcessInfo getItem(int position) {
			return allProcessInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	static class ViewHolder {
		public ImageView icon;
		public TextView name;
		public TextView memSize;
		public CheckBox ck;
	}

}
