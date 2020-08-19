package com.sleepace.sa1001wansdk.demo.fragment;

import com.sleepace.sa1001wansdk.demo.BaseActivity.MyOnTouchListener;
import com.sleepace.sa1001wansdk.demo.DataListActivity;
import com.sleepace.sa1001wansdk.demo.DemoApp;
import com.sleepace.sa1001wansdk.demo.MainActivity;
import com.sleepace.sa1001wansdk.demo.R;
import com.sleepace.sa1001wansdk.demo.util.ActivityUtil;
import com.sleepace.sa1001wansdk.demo.util.Utils;
import com.sleepace.sa1001wansdk.demo.view.SelectValueDialog;
import com.sleepace.sa1001wansdk.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.light.domain.LightWorkStatus;
import com.sleepace.sdk.core.light.domain.SPLight;
import com.sleepace.sdk.core.light.domain.SPMusic;
import com.sleepace.sdk.core.light.interfs.IMusicManager.CycleMode;
import com.sleepace.sdk.core.light.interfs.WorkStatusListener;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.domain.SleepAidConfig;
import com.sleepace.sdk.sa1001_wan.domain.WorkStatus;
import com.sleepace.sdk.sa1001_wan.interfs.IAromaManager;
import com.sleepace.sdk.sa1001_wan.interfs.IAromaManager.AromaOptMode;
import com.sleepace.sdk.sa1001_wan.interfs.IAromaManager.AromaSpeed;
import com.sleepace.sdk.util.SdkLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SleepAidFragment extends BaseFragment {
	private View maskView;
	private TextView tvMusic, tvSleepAidTimeValue, tvSleepAidTips;
	private View vSleepAidTime;
	private EditText /*etVolume,*/ etR, etG, etB, etW, etBrightness;
	private Button /*btnSendVolume,*/ btnPlayMusic, btnSendColor, btnSendBrightness, btnCloseLight, btnSave;
	private RadioGroup rgAroma;
	private RadioButton rbFast, rbMid, rbSlow, rbClose;
	
	private SelectValueDialog valueDialog;
	
	private SleepAidConfig aidInfo = new SleepAidConfig();
	private SPMusic music = new SPMusic();
	
	private byte volume = 16;
	private boolean playing;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_sleepaid, null);
//		SdkLog.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		tvMusic = root.findViewById(R.id.tv_music_name);
//		etVolume = root.findViewById(R.id.et_volume);
		etR = root.findViewById(R.id.et_r); 
		etG = root.findViewById(R.id.et_g); 
		etB = root.findViewById(R.id.et_b); 
		etW = root.findViewById(R.id.et_w);
		etBrightness = root.findViewById(R.id.et_brightness);
//		btnSendVolume = root.findViewById(R.id.btn_volume);
		btnPlayMusic = root.findViewById(R.id.btn_music);
		btnSendColor = root.findViewById(R.id.btn_w);
		btnSendBrightness = root.findViewById(R.id.btn_brightness);
		btnCloseLight = root.findViewById(R.id.btn_close_light);
		btnSave = root.findViewById(R.id.btn_save);
		rgAroma = root.findViewById(R.id.rg_aroma_speed);
		rbFast = root.findViewById(R.id.rb_fast);
		rbMid = root.findViewById(R.id.rb_mid);
		rbSlow = root.findViewById(R.id.rb_slow);
		rbClose = root.findViewById(R.id.rb_close);
		vSleepAidTime = root.findViewById(R.id.layout_sleepaid_time);
		tvSleepAidTimeValue = root.findViewById(R.id.tv_sleepaid_time_value);
		tvSleepAidTips = root.findViewById(R.id.tv_tips_sleep_aid);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		tvMusic.setOnClickListener(this);
		vSleepAidTime.setOnClickListener(this);
//		btnSendVolume.setOnClickListener(this);
		btnPlayMusic.setOnClickListener(this);
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		rgAroma.setOnCheckedChangeListener(checkedChangeListener);
//		etVolume.addTextChangedListener(volumeWatcher);
		etG.addTextChangedListener(gWatcher);
		etBrightness.addTextChangedListener(brightnessWatcher);
		registerTouchListener(touchListener);
		mHelper.registWorkStatusListener(workStatusListener);
	}
	
	protected void initUI() {
		// TODO Auto-generated method stub
		
		aidInfo.setSleepAidDuration((byte) 45);
		
		int[] data = new int[60];
		for(int i=1;i<=data.length;i++) {
			data[i - 1] = i;
		}
		
		valueDialog = new SelectValueDialog(mActivity, data);
		valueDialog.setValueSelectedListener(valueSelectedListener);
        
		mActivity.setTitle(R.string.device);
		music.setMusicId(DemoApp.SLEEPAID_MUSIC[2][0]);
		music.setMusicName(getString(Utils.getSleepAidMusicName(music.getMusicId())));
		initMusicView();
		
//		etVolume.setText("12");
		etR.setText("255");
		etB.setText("0");
		etW.setText("0");
//		etBrightness.setText("60");
		aidInfo.setAromaSpeed(AromaSpeed.COMMON.getValue());
		
		WorkStatus workStatus = mActivity.getWorkStatus();
		if(workStatus != null ) {
			if(workStatus.getSleepAidStatus() == 1) {
				playing = workStatus.getMusicStatus() == 1;
			}
		}
		
		initMusicButtonStatus();
		initSleepAidDurationView();
//		rgAroma.setTag("ok");
//		rbMid.setChecked(true);
		
		if(MainActivity.device != null) {
			mHelper.sleepSceneConfigGet(MainActivity.device.getDeviceId(), new IResultCallback<SleepAidConfig>() {
				@Override
				public void onResultCallback(CallbackData<SleepAidConfig> cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG+" sleepSceneConfigGet cd:" + cd);
					if(cd.isSuccess()) {
						SleepAidConfig config = cd.getResult();
						aidInfo.setColorR(config.getColorR());
						aidInfo.setColorG(config.getColorG());
						aidInfo.setColorB(config.getColorB());
						aidInfo.setColorW(config.getColorW());
						aidInfo.setBrightness(config.getBrightness());
						aidInfo.setAromaSpeed(config.getAromaSpeed());
						aidInfo.setSleepAidDuration(config.getSleepAidDuration());
						
						if(isAdded()) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									etG.setText(String.valueOf(aidInfo.getColorG()));
									etBrightness.setText(String.valueOf(aidInfo.getBrightness()));
									if(aidInfo.getAromaSpeed() == IAromaManager.AromaSpeed.CLOSE.getValue()) {
										rbClose.setChecked(true);
									}else if(aidInfo.getAromaSpeed() == IAromaManager.AromaSpeed.FAST.getValue()){
										rbFast.setChecked(true);
									}else if(aidInfo.getAromaSpeed() == IAromaManager.AromaSpeed.COMMON.getValue()){
										rbMid.setChecked(true);
									}else if(aidInfo.getAromaSpeed() == IAromaManager.AromaSpeed.SLOW.getValue()){
										rbSlow.setChecked(true);
									}
									initSleepAidDurationView();
								}
							});
						}
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
	
	private WorkStatusListener workStatusListener = new WorkStatusListener() {
		@Override
		public void onWorkStatusChanged(final LightWorkStatus workStatus) {
			// TODO Auto-generated method stub
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(workStatus != null) {
						if(workStatus.getSleepAidStatus() == 1) {
							playing = workStatus.getMusicStatus() == 1;
						}else {
							playing = false;
						}
						initMusicButtonStatus();
					}
				}
			});
		}
	};
	
	private TextWatcher volumeWatcher = new TextWatcher() {
		
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
//				Utils.inputTips(etVolume, 16);
			}
		}
	};
	
	private TextWatcher gWatcher = new TextWatcher() {
		
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
				Utils.inputTips(etG, 120);
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
	
	private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onCheckedChanged tag:" + group.getTag()+",checkedId:"+checkedId);
			if(group.getTag() != null) {
				group.setTag(null);
				return;
			}
			
			AromaSpeed speed = AromaSpeed.COMMON;
			if(checkedId == R.id.rb_fast){
				speed = IAromaManager.AromaSpeed.FAST;
			}else if(checkedId == R.id.rb_mid){
				speed = IAromaManager.AromaSpeed.COMMON;
			}else if(checkedId == R.id.rb_slow){
				speed = IAromaManager.AromaSpeed.SLOW;
			}else if(checkedId == R.id.rb_close){
				speed = IAromaManager.AromaSpeed.CLOSE;
			}
			
			aidInfo.setAromaSpeed(speed.getValue());
			if(MainActivity.device != null) {
				mHelper.aromaControl(MainActivity.device.getDeviceId(), AromaOptMode.SLEEPAID, speed, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						showErrDialogIfNeed(cd);
					}
				});
			}
		}
	};
	
	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, byte value) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onValueSelected val:" + value);
			aidInfo.setSleepAidDuration(value);
			initSleepAidDurationView();
		}
	};
	
	private void initMusicView() {
		tvMusic.setText(music.getMusicName());
	}
	
	private void initSleepAidDurationView() {
		String str = Utils.getDuration(mActivity, aidInfo.getSleepAidDuration());
		SdkLog.log(TAG+" initSleepAidDurationView str:" + str+",duration:" + aidInfo.getSleepAidDuration());
		tvSleepAidTimeValue.setText(str);
		tvSleepAidTips.setText(getString(R.string.music_aroma_light_close, String.valueOf(aidInfo.getSleepAidDuration())));
	}
	
	private void initMusicButtonStatus() {
		if(playing) {
			btnPlayMusic.setText(R.string.pause);
		}else {
			btnPlayMusic.setText(R.string.play);
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initPageState(true);
	}
	
	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}
	
	private void initBtnConnectState(boolean isConnected) {
//		btnSendVolume.setEnabled(isConnected);
		btnPlayMusic.setEnabled(isConnected);
		btnSendColor.setEnabled(isConnected);
		btnSendBrightness.setEnabled(isConnected);
		btnCloseLight.setEnabled(isConnected);
		btnSave.setEnabled(isConnected);
		Utils.setRadioGroupEnable(rgAroma, isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		unregisterTouchListener(touchListener);
		mHelper.unregistWorkStatusListener(workStatusListener);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == tvMusic) {
			Intent intent = new Intent(mActivity, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_SLEEPAID_MUSIC);
			intent.putExtra("musicId", music.getMusicId());
			startActivityForResult(intent, 100);
		} else if(v == vSleepAidTime) {
			valueDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_last_time), getString(R.string.confirm), null);
			valueDialog.setDefaultValue((byte)aidInfo.getSleepAidDuration());
			valueDialog.show();
		}
		
		else if(v == btnSendColor) {
			
			if(MainActivity.device == null) {
				return;
			}
			
			if(Utils.inputTips(etR, 255)) {
				return;
			}
			if(Utils.inputTips(etG, 120)) {
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
			String strBrightness = etBrightness.getText().toString().trim();
			if(!TextUtils.isEmpty(strBrightness)) {
				brightness = (byte)(int)Integer.valueOf(strBrightness);
			}
			
			byte r = (byte)(int)Integer.valueOf(strR);
			byte g = (byte)(int)Integer.valueOf(strG);
			byte b = (byte)(int)Integer.valueOf(strB);
			byte w = (byte)(int)Integer.valueOf(strW);
			SdkLog.log(TAG+" send color r:" + strR+",g:" + strG+",b:" +strB+",w:" + strW);
			
			SPLight light = new SPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);
			
			mHelper.turnOnSleepAidLight(MainActivity.device.getDeviceId(), light, brightness, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					showErrDialogIfNeed(cd);
				}
			});
			
		}else if(v == btnSendBrightness) {
			if(MainActivity.device == null) {
				return;
			}
			
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			String strBrightness = etBrightness.getText().toString().trim();
			byte brightness = (byte)(int)Integer.valueOf(strBrightness);
			mHelper.setSleepAidLightBrightness(MainActivity.device.getDeviceId(), brightness, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					showErrDialogIfNeed(cd);
				}
			});
			
		}else if(v == btnCloseLight) {
			if(MainActivity.device == null) {
				return;
			}
			
			mHelper.turnOffSleepAidLight(MainActivity.device.getDeviceId(), new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					showErrDialogIfNeed(cd);
				}
			});
		}else if(v == btnSave) {
			
//			if(Utils.inputTips(etVolume, 16)) {
//				return;
//			}
			
			if(MainActivity.device == null) {
				return;
			}
			
			if(Utils.inputTips(etR, 255)) {
				return;
			}
			if(Utils.inputTips(etG, 120)) {
				return;
			}
			if(Utils.inputTips(etB, 255)) {
				return;
			}
			if(Utils.inputTips(etW, 255)) {
				return;
			}
			
			if(Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
//			String strVolume = etVolume.getText().toString().trim();
//			byte volume = (byte)(int)Integer.valueOf(strVolume);
			aidInfo.setVolum(volume);
			
			String strR = etR.getText().toString().trim();
			String strG = etG.getText().toString().trim();
			String strB = etB.getText().toString().trim();
			String strW = etW.getText().toString().trim();
			String strBrightness = etBrightness.getText().toString().trim();
			
			byte r = (byte)(int)Integer.valueOf(strR);
			byte g = (byte)(int)Integer.valueOf(strG);
			byte b = (byte)(int)Integer.valueOf(strB);
			byte w = (byte)(int)Integer.valueOf(strW);
			byte brightness = (byte)(int)Integer.valueOf(strBrightness);
			
			
			aidInfo.setColorR(r);
			aidInfo.setColorG(g);
			aidInfo.setColorB(b);
			aidInfo.setColorW(w);
			
			aidInfo.setBrightness(brightness);
			
			SdkLog.log(TAG+" sleepAidConfig:" + aidInfo);
			
			mActivity.showLoading();
			mHelper.sleepSceneConfigSet(MainActivity.device.getDeviceId(), (short)0, null, aidInfo, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(!isAdded()){
						return;
					}
					
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mActivity.hideLoading();
							if(cd.isSuccess()) {
								Toast.makeText(mActivity, R.string.save_succeed, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		}
		
		
//		else if(v == btnSendVolume) {
//			if(Utils.inputTips(etVolume, 16)) {
//				return;
//			}
//			
//			String strVolume = etVolume.getText().toString().trim();
//			byte volume = (byte)(int)Integer.valueOf(strVolume);
//			
//			mHelper.setSleepAidMusicVolume(volume, new IResultCallback() {
//				@Override
//				public void onResultCallback(CallbackData cd) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//		}
		
		else if(v == btnPlayMusic) {
			if(MainActivity.device == null) {
				return;
			}
			
			String deviceId = MainActivity.device.getDeviceId();
			if(!playing) {
				playMusic(deviceId);
			}else {
				mHelper.turnOffSleepAidMusic(deviceId, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						showErrDialogIfNeed(cd);
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(cd.isSuccess()) {
									playing = false;
									initMusicButtonStatus();
								}
							}
						});
					}
				});
			}
		}
	}
	
	
	private void playMusic(String deviceId) {
		
//		if(Utils.inputTips(etVolume, 16)) {
//			return;
//		}
		
//		String strVolume = etVolume.getText().toString().trim();
//		byte volume = (byte)(int)Integer.valueOf(strVolume);
		
		mHelper.turnOnSleepAidMusic(deviceId, music.getMusicId(), volume, CycleMode.LOOP, new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				showErrDialogIfNeed(cd);
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(cd.isSuccess()) {
							playing = true;
							initMusicButtonStatus();
						}
					}
				});
			}
		});
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
			int musicId = data.getIntExtra("musicId", 0);
			music.setMusicId(musicId);
			music.setMusicName(getString(Utils.getSleepAidMusicName(musicId)));
			initMusicView();
			if(playing) {
				if(MainActivity.device != null) {
					playMusic(MainActivity.device.getDeviceId());
				}
			}
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










