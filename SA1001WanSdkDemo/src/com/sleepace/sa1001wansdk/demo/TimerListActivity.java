package com.sleepace.sa1001wansdk.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sleepace.sa1001wansdk.demo.util.ActivityUtil;
import com.sleepace.sa1001wansdk.demo.util.Utils;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.domain.AromaTimer;
import com.sleepace.sdk.util.SdkLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimerListActivity extends BaseActivity {
	private View dataView;
	private TextView tvTips;
	private ListView listView;
	private LayoutInflater inflater;
	private TimerAdapter adapter;
	private List<AromaTimer> list = new ArrayList<AromaTimer>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		listView = (ListView) findViewById(R.id.list);
		dataView = findViewById(R.id.layout_data);
		tvTips = findViewById(R.id.tv_tips);
	}

	public void initListener() {
		super.initListener();
		ivRight.setOnClickListener(this);
		listView.setOnItemClickListener(onItemClickListener);
	}

	public void initUI() {
		inflater = getLayoutInflater();
		tvTitle.setText(R.string.sa_timer_on);
		ivRight.setImageResource(R.drawable.device_btn_add_nor);
		
//		AromaTimer timer = new AromaTimer();
//		timer.setOpen(false);
//		timer.setContinueTime((short) 60);
//		list.add(timer);
		
		adapter = new TimerAdapter();
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		if(MainActivity.device != null) {
			showLoading();
			mHelper.getAromaTimerList(MainActivity.device.getDeviceId(), new IResultCallback<List<AromaTimer>>() {
				@Override
				public void onResultCallback(final CallbackData<List<AromaTimer>> cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if (cd.isSuccess()) {
								list.clear();
								list.addAll(cd.getResult());
								adapter.notifyDataSetChanged();
							}
						}
					});
				}
			});
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == RESULT_OK) {
			AromaTimer timer = (AromaTimer) data.getSerializableExtra("timer");
			String action = data.getStringExtra("action");
			if("delete".equals(action)) {
				Iterator<AromaTimer> itor = list.iterator();
				while(itor.hasNext()) {
					if(itor.next().getSeqId() == timer.getSeqId()) {
						itor.remove();
						break;
					}
				}
			}else {
				boolean exists = false;
				for(AromaTimer at : list) {
					if(at.getSeqId() == timer.getSeqId()) {
						exists = true;
						at.setStartHour(timer.getStartHour());
						at.setStartMin(timer.getStartMin());
						at.setContinueTime(timer.getContinueTime());
						break;
					}
				}
				
				if(!exists) {
					list.add(timer);
				}
			}
			
			adapter.notifyDataSetChanged();
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AromaTimer timer = adapter.getItem(position);
			Intent intent = new Intent(mActivity, EditTimerActivity.class);
			intent.putExtra("action", "edit");
			intent.putExtra("timer", timer);
			startActivityForResult(intent, 100);
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v == ivRight) {
			if(list.size() >= 5) {
				Toast.makeText(this, R.string.more_5, Toast.LENGTH_SHORT).show();
			}else {
				Intent intent = new Intent(this, EditTimerActivity.class);
				intent.putExtra("action", "add");
				startActivityForResult(intent, 100);
			}
		}
	}
	
	
	class TimerAdapter extends BaseAdapter {
		
		TimerAdapter(){
		}

		class ViewHolder {
			TextView tvTime;
			TextView tvDuration;
			CheckBox cbSwitch;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public AromaTimer getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_clock_item, null);
				holder = new ViewHolder();
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tvDuration = (TextView) convertView.findViewById(R.id.tv_continue);
				holder.cbSwitch = (CheckBox) convertView.findViewById(R.id.cb_swtich);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final AromaTimer item = getItem(position);
			holder.tvTime.setText(String.format("%02d:%02d", item.getStartHour(), item.getStartMin()));
			String duration = getString(R.string.sa_last_time) + ":" + Utils.getDuration(mActivity, item.getContinueTime());
			holder.tvDuration.setText(duration);
			holder.cbSwitch.setTag(item.getSeqId());
			holder.cbSwitch.setChecked(item.isOpen());
			
			holder.cbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
//					SdkLog.log(TAG+" alarm open changed:" + isChecked+",tag:" + buttonView.getTag()+",item:" + item);
					if(buttonView.getTag() != null) {
						long seqId = Long.valueOf(buttonView.getTag().toString());
						if(seqId != item.getSeqId() || isChecked == item.isOpen()) {
							return;
						}
					}
					
					SdkLog.log(TAG+" timer open changed:" + isChecked);
					item.setOpen(isChecked);
					if(MainActivity.device != null) {
						mHelper.editAromaTimer(MainActivity.device.getDeviceId(), item, new IResultCallback() {
							@Override
							public void onResultCallback(CallbackData cd) {
								// TODO Auto-generated method stub
								
							}
						});
					}
				}
			});
			
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			if(getCount() > 0) {
				dataView.setVisibility(View.VISIBLE);
				tvTips.setVisibility(View.GONE);
				super.notifyDataSetChanged();
			}else {
				dataView.setVisibility(View.GONE);
				tvTips.setVisibility(View.VISIBLE);
				tvTips.setText(R.string.sa_no_timer);
			}
		}

	}

}




