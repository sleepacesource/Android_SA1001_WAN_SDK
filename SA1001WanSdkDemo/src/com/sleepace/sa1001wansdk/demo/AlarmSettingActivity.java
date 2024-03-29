package com.sleepace.sa1001wansdk.demo;

import java.util.Calendar;

import com.sleepace.sa1001wansdk.demo.view.SelectTimeDialog;
import com.sleepace.sa1001wansdk.demo.view.SelectTimeDialog.TimeSelectedListener;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.CallbackType;
import com.sleepace.sdk.sa1001_wan.domain.AromaAlarm;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class AlarmSettingActivity extends BaseActivity {
	private View vStartTime, vEndTime;
	private TextView tvStime, tvEtime;
    private SelectTimeDialog timeDialog;
    
	public static AromaAlarm alarmConfig = new AromaAlarm();
	
	private boolean isStartTime;
	private byte sHour, sMin, eHour, eMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sHour = (byte) alarmConfig.getHour();
        sMin = (byte) alarmConfig.getMinute();
        Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, sHour);
		calendar.set(Calendar.MINUTE, sMin);
		calendar.add(Calendar.MINUTE, alarmConfig.getAlarmDuration());
		eHour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		eMin = (byte) calendar.get(Calendar.MINUTE);
//		SdkLog.log(TAG+" onCreate alarmConfig:" + alarmConfig+",h:"+sHour+",m:"+sMin+",eh:"+eHour+",em:"+eMin);
        
        setContentView(R.layout.activity_edit_clock);
        findView();
        initListener();
        initUI();
    }


    public void findView() {
    	super.findView();
    	vStartTime = findViewById(R.id.layout_start_time);
    }

    public void initListener() {
    	super.initListener();
    	tvRight.setOnClickListener(this);
    	vStartTime.setOnClickListener(this);
    	vEndTime.setOnClickListener(this);
    }

    public void initUI() {
    	super.initUI();
//        tvTitle.setText(R.string.set_alert_switch);
        tvRight.setText(R.string.save);
        timeDialog = new SelectTimeDialog(this);
        timeDialog.setTimeSelectedListener(timeListener);
        initTimeView();
    }
    
    private void initTimeView() {
    	tvStime.setText(String.format("%02d:%02d", sHour, sMin));
        tvEtime.setText(String.format("%02d:%02d", eHour, eMin));
    }
    
    private TimeSelectedListener timeListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(byte hour, byte minute) {
			// TODO Auto-generated method stub
			if(isStartTime) {
				sHour = hour;
				sMin = minute;
			}else {
				eHour = hour;
				eMin = minute;
			}
			initTimeView();
		}
	};

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    public void onClick(View v) {
    	super.onClick(v);
    	if(v == vStartTime) {
    		isStartTime = true;
//    		timeDialog.setLabel(getString(R.string.cancel), getString(R.string.set_start_time), getString(R.string.confirm));
    		timeDialog.setDefaultValue(sHour, sMin);
    		timeDialog.show();
    	}else if(v == vEndTime) {
    		isStartTime = false;
//    		timeDialog.setLabel(getString(R.string.cancel), getString(R.string.set_end_time), getString(R.string.confirm));
    		timeDialog.setDefaultValue(eHour, eMin);
    		timeDialog.show();
    	}else if(v == tvRight) {
    		SdkLog.log(TAG+" save sHour:" +sHour+",sMin:" + sMin+",eHour:" + eHour+",eMin:" + eMin);
    		if(MainActivity.device == null) {
    			return;
    		}
    		
    		Calendar cal1 = Calendar.getInstance();
    		cal1.set(Calendar.HOUR_OF_DAY, sHour);
    		cal1.set(Calendar.MINUTE, sMin);
    		cal1.set(Calendar.SECOND, 0);
    		
    		Calendar cal2 = Calendar.getInstance();
    		cal2.set(Calendar.HOUR_OF_DAY, eHour);
    		cal2.set(Calendar.MINUTE, eMin);
    		cal2.set(Calendar.SECOND, 0);
    		if(sHour == eHour && sMin == eMin) {
    			cal2.add(Calendar.DAY_OF_MONTH, 1);
    		}
    		
    		int time = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000);
    		if(time < 0) {
    			cal2.add(Calendar.HOUR_OF_DAY, 24);
    			time = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / 1000);
    		}
    		
    		alarmConfig.setHour(sHour);
    		alarmConfig.setMinute(sMin);
    		alarmConfig.setAlarmDuration((short) (time / 60));
    		
    		mHelper.alarmConfig(MainActivity.device.getDeviceId(), alarmConfig, new IResultCallback<Void>() {
    			@Override
    			public void onResultCallback(CallbackData<Void> cd) {
    				// TODO Auto-generated method stub
    				SdkLog.log(TAG+" onResultCallback " + cd);
    				if(cd.getCallbackType() == CallbackType.ALARM_UPDATE) {
    					if(cd.isSuccess()) {
    						finish();
    					}else {
    						
    					}
    				}
    			}
    		});
    	}
    }
    
}












