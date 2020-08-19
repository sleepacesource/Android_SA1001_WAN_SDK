package com.sleepace.sa1001wansdk.demo.fragment;

import java.lang.reflect.Field;

import com.sleepace.sa1001wansdk.demo.AlarmListActivity;
import com.sleepace.sa1001wansdk.demo.CenterKeyActivity;
import com.sleepace.sa1001wansdk.demo.R;
import com.sleepace.sa1001wansdk.demo.TimerListActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingFragment extends BaseFragment {
	private View maskView;
	private View vAlarm, vClock, vCenterKey;
	private Button btnRestore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_setting, null);
		// LogUtil.log(TAG + " onCreateView-----------");
		findView(view);
		initListener();
		initUI();
		return view;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		vAlarm = root.findViewById(R.id.tv_alarm);
		vClock = root.findViewById(R.id.tv_timer);
		vCenterKey = root.findViewById(R.id.tv_centerkey);
		btnRestore = (Button) root.findViewById(R.id.btn_restore);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		vAlarm.setOnClickListener(this);
		vClock.setOnClickListener(this);
		vCenterKey.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
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
		btnRestore.setEnabled(isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.setting);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == vAlarm) {
			Intent intent = new Intent(mActivity, AlarmListActivity.class);
			startActivity(intent);
		}else if(v == vClock) {
			Intent intent = new Intent(mActivity, TimerListActivity.class);
			startActivity(intent);
		}else if(v == vCenterKey) {
			Intent intent = new Intent(mActivity, CenterKeyActivity.class);
			startActivity(intent);
		}else if (v == btnRestore) {
//			getDeviceHelper().deviceInit(3000, new IResultCallback() {
//				@Override
//				public void onResultCallback(CallbackData cd) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
