package com.sleepace.sa1001wansdk.demo;

import com.sleepace.sa1001wansdk.demo.util.ActivityUtil;
import com.sleepace.sa1001wansdk.demo.util.Utils;
import com.sleepace.sa1001wansdk.demo.view.SelectTimeDialog;
import com.sleepace.sa1001wansdk.demo.view.SelectTimeDialog.TimeSelectedListener;
import com.sleepace.sa1001wansdk.demo.view.SelectValueDialog;
import com.sleepace.sa1001wansdk.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.domain.AromaAlarm;
import com.sleepace.sdk.sa1001_wan.interfs.IAromaManager.AromaSpeed;
import com.sleepace.sdk.util.SdkLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;


public class EditAlarmActivity extends BaseActivity {
	
	private View vStartTime, vRepeat, vMusic, vVolume, vSmartWake, vSnooze;
	private View smartWakeLine;
	private TextView tvStartTime, tvRepeat, tvMusic, tvVolume, tvSmartWakeTime, tvSmartWakeTips, tvSnooze, tvSnoozeTips;
	private CheckBox cbLight, cbAroma, cbSmartWake, cbSnooze;
	private Button btnPreview, btnDel;
	
	private SelectTimeDialog timeDialog;
	private SelectValueDialog volumeDialog, smartOffsetDialog, snoozeDurationDialog;
	
	private String action;
	private AromaAlarm alarm;
	private byte smartOffset = 0, snoozeLength = 0;
	
	private boolean isPreview;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        findView();
        initListener();
        initUI();
    }

    public void findView() {
    	super.findView();
    	vStartTime = findViewById(R.id.layout_start_time);
    	vRepeat = findViewById(R.id.layout_repeat);
    	vMusic = findViewById(R.id.layout_music);
    	vVolume = findViewById(R.id.layout_volume);
    	vSmartWake = findViewById(R.id.layout_smart_wake);
    	smartWakeLine = findViewById(R.id.line_smart_wake);
    	vSnooze = findViewById(R.id.layout_snooze_time);
    	tvStartTime = findViewById(R.id.tv_start_time);
    	tvRepeat = findViewById(R.id.tv_reply);
    	tvMusic = findViewById(R.id.tv_music);
    	tvVolume = findViewById(R.id.tv_volume);
    	cbLight = findViewById(R.id.cb_light);
    	cbAroma = findViewById(R.id.cb_aroma);
    	cbSmartWake = findViewById(R.id.cb_smart_wake);
    	cbSnooze = findViewById(R.id.cb_snooze);
    	tvSmartWakeTime = findViewById(R.id.tv_smart_wake);
    	tvSmartWakeTips = findViewById(R.id.tv_tips_smart_wake);
    	tvSnooze = findViewById(R.id.tv_snooze_time);
    	tvSnoozeTips = findViewById(R.id.tv_tips_snooze);
    	btnPreview = findViewById(R.id.btn_preview);
    	btnDel = findViewById(R.id.btn_del);
    }

    public void initListener() {
    	super.initListener();
    	tvRight.setOnClickListener(this);
    	vStartTime.setOnClickListener(this);
    	vRepeat.setOnClickListener(this);
    	vMusic.setOnClickListener(this);
    	vVolume.setOnClickListener(this);
    	vSmartWake.setOnClickListener(this);
    	vSmartWake.setOnClickListener(this);
    	vSnooze.setOnClickListener(this);
    	btnPreview.setOnClickListener(this);
    	btnDel.setOnClickListener(this);
    	cbLight.setOnCheckedChangeListener(onCheckedChangeListener);
    	cbAroma.setOnCheckedChangeListener(onCheckedChangeListener);
    	cbSmartWake.setOnCheckedChangeListener(onCheckedChangeListener);
    	cbSnooze.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void initUI() {
    	super.initUI();
    	tvRight.setText(R.string.save);
    	
    	action = getIntent().getStringExtra("action");
    	if("add".equals(action)) {
    		tvTitle.setText(R.string.add_alarm);
    		btnDel.setVisibility(View.INVISIBLE);
    		alarm = new AromaAlarm();
    		alarm.setAlarmId(DemoApp.getSeqId());
    		alarm.setOpen(true);
    		alarm.setHour((byte) 8);
    		alarm.setRepeat((byte) 127);
    		alarm.setMusicId(DemoApp.ALARM_MUSIC[0][0]);
    		alarm.setVolume((byte) 16);
    		alarm.setBrightness((byte) 80);
    		alarm.setAromaSpeed(AromaSpeed.COMMON.getValue());
    		alarm.setSmartOffset(smartOffset);
    		alarm.setSnoozeCount((byte)0);
    		alarm.setSnoozeLength(snoozeLength);
    	}else {
    		tvTitle.setText(R.string.edit_alarm);
    		btnDel.setVisibility(View.VISIBLE);
    		alarm = (AromaAlarm) getIntent().getSerializableExtra("alarm");
    		snoozeLength = alarm.getSnoozeLength();
    	}
        
        timeDialog = new SelectTimeDialog(this, "%02d");
        timeDialog.setTimeSelectedListener(timeListener);
        
        int[] volumes = new int[16];
        for(int i =1;i<=volumes.length;i++) {
        	volumes[i-1] = i;
        }
        volumeDialog = new SelectValueDialog(this, volumes);
        volumeDialog.setValueSelectedListener(valueSelectedListener);
        
        int[] smartOffset = new int[30];
        for(int i =1;i<=smartOffset.length;i++) {
        	smartOffset[i-1] = i;
        }
        smartOffsetDialog = new SelectValueDialog(this, smartOffset);
        smartOffsetDialog.setValueSelectedListener(valueSelectedListener);
        
        int[] data = {5,10,15};
		snoozeDurationDialog = new SelectValueDialog(this, data);
		snoozeDurationDialog.setValueSelectedListener(valueSelectedListener);
		
        initTimeView();
        initRepeatView();
        initMusicView();
        initVolumeView();
        initLightView();
        initAromaView();
        initSmartWakeView();
        initSnoozeView();
    }
    
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(buttonView == cbLight) {
				alarm.setBrightness(isChecked ? (byte) 80 : 0);
			}else if(buttonView == cbAroma) {
				alarm.setAromaSpeed(isChecked ? AromaSpeed.COMMON.getValue() : AromaSpeed.CLOSE.getValue());
			}else if(buttonView == cbSmartWake) {
				if(isChecked) {
					if(smartOffset == 0) smartOffset = 20;
					alarm.setSmartOffset(smartOffset);
				}else {
					alarm.setSmartOffset((byte)0);
				}
				initSmartWakeView();
			}else if(buttonView == cbSnooze) {
				if(isChecked) {
					alarm.setSnoozeCount((byte)1);
					if(snoozeLength == 0) {
						snoozeLength = 5;
					}
					alarm.setSnoozeLength(snoozeLength);
				}else {
					alarm.setSnoozeCount((byte)0);
					alarm.setSnoozeLength((byte) 0);
				}
				initSnoozeView();
			}
		}
	};
    
    private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, byte value) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onValueSelected val:" + value);
			if(dialog == smartOffsetDialog) {
				smartOffset = value;
				alarm.setSmartOffset(value);
				initSmartWakeView();
			}else if(dialog == snoozeDurationDialog) {
				snoozeLength = value;
				alarm.setSnoozeLength(value);
				initSnoozeView();
			}else if(dialog == volumeDialog) {
				alarm.setVolume(value);
				initVolumeView();
			}
		}
	};

    private TimeSelectedListener timeListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(byte hour, byte minute) {
			// TODO Auto-generated method stub
			alarm.setHour(hour);
			alarm.setMinute(minute);
			initTimeView();
		}
	};
	
	private void initTimeView() {
    	tvStartTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
    }
	
	private void initRepeatView() {
		tvRepeat.setText(Utils.getSelectDay(this, alarm.getRepeat()));
	}
	
	private void initMusicView() {
		int res = Utils.getAlarmMusicName(alarm.getMusicId());
//		SdkLog.log(TAG+" initMusicView mid:" + alarm.getMusicID()+",res:" + res);
		if(res > 0) {
			tvMusic.setText(res);
		}else {
			tvMusic.setText("");
		}
	}
	
	private void initVolumeView() {
		tvVolume.setText(String.valueOf(alarm.getVolume()));
	}
	
	private void initLightView() {
		cbLight.setChecked(alarm.getBrightness() > 0);
	}
	
	private void initAromaView() {
		cbAroma.setChecked(alarm.getAromaSpeed() != AromaSpeed.CLOSE.getValue());
	}
	
	private void initSmartWakeView() {
		cbSmartWake.setChecked(alarm.getSmartOffset() > 0);
		if(cbSmartWake.isChecked()) {
			vSmartWake.setVisibility(View.VISIBLE);
			tvSmartWakeTips.setVisibility(View.VISIBLE);
			smartWakeLine.setVisibility(View.VISIBLE);
			tvSmartWakeTime.setText(Utils.getDuration(this, alarm.getSmartOffset()));
			tvSmartWakeTips.setText(getString(R.string.wake_time_turn_on, ""));
		}else {
			vSmartWake.setVisibility(View.GONE);
			tvSmartWakeTips.setVisibility(View.GONE);
			smartWakeLine.setVisibility(View.GONE);
		}
	}
	
	private void initSnoozeView() {
		cbSnooze.setChecked(alarm.getSnoozeLength() > 0);
		if(cbSnooze.isChecked()) {
			vSnooze.setVisibility(View.VISIBLE);
			tvSnoozeTips.setVisibility(View.VISIBLE);
			tvSnooze.setText(Utils.getDuration(this, alarm.getSnoozeLength()));
			tvSnoozeTips.setText(getString(R.string.smart_wake_turn_on, "", String.valueOf(alarm.getSnoozeCount())));
		}else {
			vSnooze.setVisibility(View.GONE);
			tvSnoozeTips.setVisibility(View.GONE);
		}
	}
	
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void onClick(View v) {
    	super.onClick(v);
    	if(v == tvRight) {
    		if(MainActivity.device == null) {
				return;
			}
    		
    		showLoading();
    		int timestamp = (int) (System.currentTimeMillis() / 1000);
    		alarm.setTimestamp(timestamp); //单次闹钟时，需要设置当前时间戳
    		mHelper.alarmConfig(MainActivity.device.getDeviceId(), alarm, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if(cd.isSuccess()) {
								finish();
							}
						}
					});
				}
			});
    	}else if(v == vStartTime) {
    		timeDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_start_time), getString(R.string.confirm), null, null);
    		timeDialog.setDefaultValue(alarm.getHour(), alarm.getMinute());
    		timeDialog.show();
    	}else if(v == vRepeat) {
    		stopAlarmPreview();
    		Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_REPEAT);
			intent.putExtra("repeat", alarm.getRepeat());
			startActivityForResult(intent, 100);
    	}else if( v == vSmartWake) {
    		smartOffsetDialog.setLabel(getString(R.string.cancel), getString(R.string.wake_time), getString(R.string.confirm), null);
    		smartOffsetDialog.setDefaultValue(alarm.getSmartOffset());
    		smartOffsetDialog.show();
    	}else if( v == vSnooze) {
    		snoozeDurationDialog.setLabel(getString(R.string.cancel), getString(R.string.snooze_duration), getString(R.string.confirm), null);
			snoozeDurationDialog.setDefaultValue(alarm.getSnoozeLength());
			snoozeDurationDialog.show();
    	}else if(v == vMusic) {
    		stopAlarmPreview();
    		Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_ALARM_MUSIC);
			intent.putExtra("musicId", alarm.getMusicId());
			startActivityForResult(intent, 101);
    	}else if(v == vVolume) {
    		volumeDialog.setLabel(getString(R.string.cancel), getString(R.string.volume), getString(R.string.confirm), null);
    		volumeDialog.setDefaultValue(alarm.getVolume());
    		volumeDialog.show();
    	}else if(v == btnPreview) {
    		if(MainActivity.device == null) {
    			return;
    		}
    		
    		showLoading();
    		if(!isPreview) {
    			SdkLog.log(TAG+" preview alarm:" + alarm);
    			mHelper.startAlarmPreview(MainActivity.device.getDeviceId(), alarm.getVolume(), alarm.getBrightness(), AromaSpeed.value2Type(alarm.getAromaSpeed()), alarm.getMusicId(), new IResultCallback() {
    				@Override
    				public void onResultCallback(final CallbackData cd) {
    					// TODO Auto-generated method stub
    					if(!ActivityUtil.isActivityAlive(mActivity)) {
    						return;
    					}
    					
    					runOnUiThread(new Runnable() {
    						public void run() {
    							hideLoading();
    							if(cd.isSuccess()) {
    								isPreview = true;
    								initBtnPreviewLabel();
    							}
    						}
    					});
    				}
    			});
    		}else {
    			stopAlarmPreview();
    		}
    	}else if(v == btnDel) {
    		if(MainActivity.device == null) {
				return;
			}
    		
    		showLoading();
    		mHelper.delAlarm(MainActivity.device.getDeviceId(), alarm.getAlarmId(), new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if(cd.isSuccess()) {
								finish();
							}
						}
					});
				}
			});
    	}
    }
    
    @Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		stopAlarmPreview();
	}
    
    private void stopAlarmPreview() {
    	if(MainActivity.device == null) {
    		return;
    	}
    	SdkLog.log(TAG+" stopAlarmPreview isPreview:" + isPreview);
    	if(isPreview) {
			mHelper.stopAlarmPreview(MainActivity.device.getDeviceId(), new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if(cd.isSuccess()) {
								isPreview = false;
								initBtnPreviewLabel();
							}
						}
					});
				}
			});
		}
    }
    
    private void initBtnPreviewLabel() {
    	if(ActivityUtil.isActivityAlive(this)) {
    		if(isPreview) {
    			btnPreview.setText(R.string.preview_stop);
    		}else {
    			btnPreview.setText(R.string.preview_alarm);
    		}
    	}
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100) {
			if(resultCode == Activity.RESULT_OK) {
				byte repeat = data.getByteExtra("repeat", (byte)0);
				alarm.setRepeat(repeat);
				initRepeatView();
			}
		}else if(requestCode == 101) {
			if(resultCode == Activity.RESULT_OK) {
				int musicId = data.getIntExtra("musicId", 0);
				alarm.setMusicId(musicId);
				initMusicView();
			}
		}
	}
}












