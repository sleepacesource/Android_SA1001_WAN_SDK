package com.sleepace.sa1001wansdk.demo.fragment;

import com.sleepace.sa1001wansdk.demo.BaseActivity.MyOnTouchListener;
import com.sleepace.sa1001wansdk.demo.MainActivity;
import com.sleepace.sa1001wansdk.demo.R;
import com.sleepace.sa1001wansdk.demo.util.ActivityUtil;
import com.sleepace.sa1001wansdk.demo.util.Utils;
import com.sleepace.sdk.core.light.domain.LightWorkStatus;
import com.sleepace.sdk.core.light.domain.SPLight;
import com.sleepace.sdk.core.light.interfs.WorkStatusListener;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.domain.WorkStatus;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LightFragment extends BaseFragment {
	private View maskView;
	private EditText etR, etG, etB, etW, etBrightness;
	private Button btnSendColor, btnSendBrightness, btnCloseLight;
	private MainActivity mainActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_light, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		mainActivity = (MainActivity) getActivity();
		findView(root);
		initListener();
		initUI();
		return root;
	}
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		etR = root.findViewById(R.id.et_r);
		etG = root.findViewById(R.id.et_g); 
		etB = root.findViewById(R.id.et_b); 
		etW = root.findViewById(R.id.et_w);
		etBrightness = root.findViewById(R.id.et_brightness);
		btnSendColor = root.findViewById(R.id.btn_w);
		btnSendBrightness = root.findViewById(R.id.btn_brightness);
		btnCloseLight = root.findViewById(R.id.btn_close_light);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		etR.addTextChangedListener(rgbwWatcher);
		etG.addTextChangedListener(rgbwWatcher);
		etB.addTextChangedListener(rgbwWatcher);
		etW.addTextChangedListener(rgbwWatcher);
		etBrightness.addTextChangedListener(brightnessWatcher);
		registerTouchListener(touchListener);
		mHelper.registWorkStatusListener(workStatusListener);
	}
	
	private WorkStatusListener workStatusListener = new WorkStatusListener() {
		@Override
		public void onWorkStatusChanged(LightWorkStatus workStatus) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onWorkStatusChanged:" + workStatus);
			initBtnState((WorkStatus)workStatus);
		}
	};
	
	private void initBtnState(final LightWorkStatus workStatus) {
		boolean lightStatus = workStatus != null && workStatus.getLightStatus() == 1;
		initBtnState(lightStatus);
	}
	
	private void initBtnState(final boolean lightStatus) {
		if(isAdded()) {
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(lightStatus) {
						btnCloseLight.setEnabled(true);
					}else {
						btnCloseLight.setEnabled(false);
					}
				}
			});
		}
	}
	
	
	
	private MyOnTouchListener touchListener = new MyOnTouchListener() {
		@Override
		public boolean onTouch(MotionEvent ev) {
			// TODO Auto-generated method stub
			View view = mActivity.getCurrentFocus();
			ActivityUtil.hideKeyboard(ev, view, mActivity);
			return false;
		}
	};
	
	private TextWatcher rgbwWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if(!TextUtils.isEmpty(str)) {
				int rgbw = Integer.valueOf(str);
				if(rgbw > 255) {
					Toast.makeText(mActivity, R.string.input_0_255, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	private TextWatcher brightnessWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if(!TextUtils.isEmpty(str)) {
				Utils.inputTips(etBrightness, 100);
			}
		}
	};

	protected void initUI() {
		// TODO Auto-generated method stub
		etR.setText("255");
		etG.setText("104");
		etB.setText("11");
//		etW.setText("0");
//		etBrightness.setText("60");
		
		setPageEnable(true);
		initBtnState(mainActivity.getWorkStatus());
		if(mainActivity.getWorkStatus() == null) {
			if(MainActivity.device != null) {
				mHelper.queryWorkStatus(MainActivity.device.getDeviceId(), new IResultCallback<WorkStatus>() {
					@Override
					public void onResultCallback(CallbackData<WorkStatus> cd) {
						// TODO Auto-generated method stub
//						SdkLog.log(TAG+" queryWorkStatus cd:" + cd);
						if(cd.isSuccess()) {
							WorkStatus workStatus = cd.getResult();
							initBtnState(workStatus);
							mainActivity.setWorkStatus(workStatus);
						}
					}
				});
			}
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		boolean isConnected = mHelper.isConnected();
//		initPageState(isConnected);
	}
	
	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}
	
	private void initBtnConnectState(boolean isConnected) {
		btnSendColor.setEnabled(isConnected);
		btnSendBrightness.setEnabled(isConnected);
		btnCloseLight.setEnabled(isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
//			AlarmSettingActivity.alarmConfig.setEnable(isChecked);
//			getBinatoneHelper().setAlarm(AlarmSettingActivity.alarmConfig.isEnable(), AlarmSettingActivity.alarmConfig.getHour(), AlarmSettingActivity.alarmConfig.getMinute(), AlarmSettingActivity.alarmConfig.getDuration(), 3000, setAlarmCallback);
		}
	};
	
	private IResultCallback<Void> setAlarmCallback = new IResultCallback<Void>() {
		@Override
		public void onResultCallback(CallbackData<Void> cd) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onResultCallback " + cd);
			if(cd.getCallbackType() == IMonitorManager.METHOD_ALARM_SET) {
				if(cd.isSuccess()) {
					
				}else {
					
				}
			}
		}
	};
	
//	private IResultCallback<AlarmConfig> getAlarmCallback = new IResultCallback<AlarmConfig>() {
//		@Override
//		public void onResultCallback(CallbackData<AlarmConfig> cd) {
//			// TODO Auto-generated method stub
//			if(!isAdded()) {
//				return;
//			}
//			SdkLog.log(TAG+" onResultCallback " + cd);
//			if(cd.getCallbackType() == IMonitorManager.METHOD_ALARM_GET) {
//				if(cd.isSuccess()) {
//					AlarmSettingActivity.alarmConfig = cd.getResult();
//					mActivity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							cbAlarmSwitch.setChecked(AlarmSettingActivity.alarmConfig.isEnable());
//						}
//					});
//				}else {
//					
//				}
//			}
//		}
//	};
	

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		mHelper.unregistWorkStatusListener(workStatusListener);
		unregisterTouchListener(touchListener);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == btnSendColor) {
			
			if(MainActivity.device == null) {
				return;
			}
			
			if(Utils.inputTips(etR, 255)) {
				return;
			}
			if(Utils.inputTips(etG, 255)) {
				return;
			}
			if(Utils.inputTips(etB, 255)) {
				return;
			}
			if(Utils.inputTips(etW, 255)) {
				return;
			}
			
			String strR = etR.getText().toString().trim();
			String strG = etG.getText().toString().trim();
			String strB = etB.getText().toString().trim();
			String strW = etW.getText().toString().trim();
			
			byte brightness = 50;
			String strBrightness = etBrightness.getText().toString();
			if(!TextUtils.isEmpty(strBrightness)) {
				brightness = (byte)(int)Integer.valueOf(strBrightness);
			}
			
			byte r = (byte)(int)Integer.valueOf(strR);
			byte g = (byte)(int)Integer.valueOf(strG);
			byte b = (byte)(int)Integer.valueOf(strB);
			byte w = (byte)(int)Integer.valueOf(strW);
			
			SPLight light = new SPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);
			
			mHelper.turnOnColorLight(MainActivity.device.getDeviceId(), light, brightness, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					showErrDialogIfNeed(cd);
					if(cd.isSuccess()) {
						initBtnState(true);
					}
				}
			});
		}else if(v == btnSendBrightness) {
			if(MainActivity.device == null) {
				return;
			}
			
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			String strBrightness = etBrightness.getText().toString();
			byte brightness = (byte)(int)Integer.valueOf(strBrightness);
			
			mHelper.setLightBrightness(MainActivity.device.getDeviceId(), brightness, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					showErrDialogIfNeed(cd);
					if(cd.isSuccess()) {
						initBtnState(true);
					}
				}
			});
			
		}else if(v == btnCloseLight) {
			if(MainActivity.device == null) {
				return;
			}
			
			mHelper.turnOffLight(MainActivity.device.getDeviceId(), new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					showErrDialogIfNeed(cd);
					if(cd.isSuccess()) {
						initBtnState(false);
					}
				}
			});
		}
	}

//	@Override
//	public void onDetach() {
//		super.onDetach();
//		try {
//			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//			childFragmentManager.setAccessible(true);
//			childFragmentManager.set(this, null);
//		} catch (NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
//	}
	
}










