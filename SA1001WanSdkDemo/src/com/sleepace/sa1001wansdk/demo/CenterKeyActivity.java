package com.sleepace.sa1001wansdk.demo;

import com.sleepace.sa1001wansdk.demo.util.ActivityUtil;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.domain.CenterKey;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CenterKeyActivity extends BaseActivity {
	
	private TextView vMusic, vLight, vAroma;
	private CenterKey centerKey = new CenterKey(true, true, true);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center_key);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		vMusic = findViewById(R.id.tv_music);
		vLight = findViewById(R.id.tv_light);
		vAroma = findViewById(R.id.tv_aroma);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		vMusic.setOnClickListener(this);
		vLight.setOnClickListener(this);
		vAroma.setOnClickListener(this);
	}

	public void initUI() {
		tvTitle.setText(R.string.sa_center_set);
		tvRight.setText(R.string.save);
		initCenterKeyView();
	}
	
	private void initCenterKeyView() {
		initViewCheckStatus(vMusic, centerKey.isMusicEnable());
		initViewCheckStatus(vLight, centerKey.isLightEnable());
		initViewCheckStatus(vAroma, centerKey.isAromaEnable());
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(MainActivity.device != null) {
			showLoading();
			mHelper.getCenterKey(MainActivity.device.getDeviceId(), new IResultCallback<CenterKey>() {
				@Override
				public void onResultCallback(final CallbackData<CenterKey> cd) {
					// TODO Auto-generated method stub
					if(!ActivityUtil.isActivityAlive(mActivity)) {
						return;
					}
					
					runOnUiThread(new Runnable() {
						public void run() {
							hideLoading();
							if (cd.isSuccess()) {
								centerKey = cd.getResult();
								initCenterKeyView();
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
	public void onClick(View v) {
		super.onClick(v);
		if(v == tvRight) {
			if(MainActivity.device == null) {
				return;
			}
			
			showLoading();
			mHelper.setCenterKey(MainActivity.device.getDeviceId(), centerKey.isLightEnable(), centerKey.isMusicEnable(), centerKey.isAromaEnable(), new IResultCallback() {
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
		}else if(v == vMusic) {
			if(getCheckCount() > 1 || (getCheckCount() == 1 && !centerKey.isMusicEnable())) {
				centerKey.setMusicEnable(!centerKey.isMusicEnable());
				initViewCheckStatus(vMusic, centerKey.isMusicEnable());
			}
		}else if(v == vLight) {
			if(getCheckCount() > 1 || (getCheckCount() == 1 && !centerKey.isLightEnable())) {
				centerKey.setLightEnable(!centerKey.isLightEnable());
				initViewCheckStatus(vLight, centerKey.isLightEnable());
			}
		}else if(v == vAroma) {
			if(getCheckCount() > 1 || (getCheckCount() == 1 && !centerKey.isAromaEnable())) {
				centerKey.setAromaEnable(!centerKey.isAromaEnable());
				initViewCheckStatus(vAroma, centerKey.isAromaEnable());
			}
		}
	}
	
	private int getCheckCount() {
		int count = 0;
		if(centerKey.isMusicEnable()) {
			count++;
		}
		
		if(centerKey.isLightEnable()) {
			count++;
		}
		
		if(centerKey.isAromaEnable()) {
			count++;
		}
		return count;
	}
	
	private void initViewCheckStatus(TextView v, boolean check) {
		if(check) {
			v.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
		}else {
			v.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
	}

}











