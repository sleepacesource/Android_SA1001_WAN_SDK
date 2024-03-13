package com.sleepace.sa1001wansdk.demo.fragment;

import com.sleepace.sa1001wansdk.demo.MainActivity;
import com.sleepace.sa1001wansdk.demo.R;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.constants.AromaOptMode;
import com.sleepace.sdk.sa1001_wan.constants.AromaSpeed;
import com.sleepace.sdk.sa1001_wan.domain.WorkStatus;
import com.sleepace.sdk.sa1001_wan.interfs.WorkStatusListener;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AromaFragment extends BaseFragment {
	private View maskView;
	private RadioGroup rgAroma;
	private RadioButton rbFast, rbMid, rbSlow;
	private Button btnCloseAroma;
	private MainActivity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_aroma, null);
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
		rgAroma = root.findViewById(R.id.rg_aroma_speed);
		rbFast = root.findViewById(R.id.rb_fast);
		rbMid = root.findViewById(R.id.rb_mid);
		rbSlow = root.findViewById(R.id.rb_slow);
		btnCloseAroma = root.findViewById(R.id.btn_close_aroma);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		rgAroma.setOnCheckedChangeListener(checkedChangeListener);
		btnCloseAroma.setOnClickListener(this);
		mHelper.registWorkStatusListener(workStatusListener);
	}
	
	private WorkStatusListener workStatusListener = new WorkStatusListener() {
		@Override
		public void onWorkStatusChanged(WorkStatus workStatus) {
			// TODO Auto-generated method stub
//			SdkLog.log(TAG+" onWorkStatusChanged:" + workStatus);
			initBtnState((WorkStatus)workStatus);
		}
	};
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			Object tag = group.getTag();
			AromaSpeed speed = AromaSpeed.CLOSE;
			if(checkedId == R.id.rb_fast){
				speed = AromaSpeed.FAST;
			}else if(checkedId == R.id.rb_mid){
				speed = AromaSpeed.COMMON;
			}else if(checkedId == R.id.rb_slow){
				speed = AromaSpeed.SLOW;
			}
			
			SdkLog.log(TAG+" aroma onCheckedChanged tag:"+tag+",checkedId:"+checkedId+",speed:" + speed);
			if(tag != null) {
				group.setTag(null);
				return;
			}
			
			final AromaSpeed as = speed;
			if(MainActivity.device != null) {
				mHelper.aromaControl(MainActivity.device.getDeviceId(), AromaOptMode.CONTROL, speed, new IResultCallback() {
					@Override
					public void onResultCallback(CallbackData cd) {
						// TODO Auto-generated method stub
						showErrDialogIfNeed(cd);
						if(isAdded() && cd.isSuccess()) {
							mainActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(as == AromaSpeed.CLOSE) {
//										btnCloseAroma.setEnabled(false);
//										rgAroma.check(-1);
									}else {
										btnCloseAroma.setEnabled(true);
									}
								}
							});
						}
					}
				});
			}
			
		}
	};


	protected void initUI() {
		// TODO Auto-generated method stub
		if(mainActivity.getWorkStatus() == null || true) {
			if(MainActivity.device != null) {
				mHelper.queryWorkStatus(MainActivity.device.getDeviceId(), new IResultCallback<WorkStatus>() {
					@Override
					public void onResultCallback(CallbackData<WorkStatus> cd) {
						// TODO Auto-generated method stub
						SdkLog.log(TAG+" queryWorkStatus cd:" + cd);
						if(cd.isSuccess()) {
							WorkStatus workStatus = cd.getResult();
							initBtnState(workStatus);
							mainActivity.setWorkStatus(workStatus);
						}
					}
				});
			}
		}else {
			initBtnState(mainActivity.getWorkStatus());
		}
	}
	
	private void initBtnState(final WorkStatus workStatus) {
		if(isAdded()) {
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					rgAroma.setTag("ok");
					if(workStatus != null && workStatus.getAromaSpeed() != 0) {
						btnCloseAroma.setEnabled(true);
						if(workStatus.getAromaSpeed() == AromaSpeed.FAST.getValue()) {
							rbFast.setChecked(true);
						}else if(workStatus.getAromaSpeed() == AromaSpeed.COMMON.getValue()) {
							rbMid.setChecked(true);
						}else if(workStatus.getAromaSpeed() == AromaSpeed.SLOW.getValue()) {
							rbSlow.setChecked(true);
						}
					}else {
						btnCloseAroma.setEnabled(false);
						rgAroma.check(-1);
					}
				}
			});
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initPageState(true);
	}
	
	private void initPageState(boolean isConnected) {
		//initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}
	
	private void initBtnConnectState(boolean isConnected) {
		btnCloseAroma.setEnabled(isConnected);
//		Utils.setRadioGroupEnable(rgAroma, isConnected);
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
		mHelper.unregistWorkStatusListener(workStatusListener);
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v == btnCloseAroma) {
			SdkLog.log(TAG+" onClick close aroma-------");
			if(MainActivity.device != null) {
				mHelper.aromaControl(MainActivity.device.getDeviceId(), AromaOptMode.CONTROL, AromaSpeed.CLOSE, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						showErrDialogIfNeed(cd);
					}
				});
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










