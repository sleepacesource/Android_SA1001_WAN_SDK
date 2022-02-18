package com.sleepace.sa1001wansdk.demo;

import com.sleepace.sa1001wansdk.demo.fragment.ControlFragment;
import com.sleepace.sa1001wansdk.demo.fragment.LoginFragment;
import com.sleepace.sa1001wansdk.demo.fragment.SettingFragment;
import com.sleepace.sdk.core.light.domain.LightWorkStatus;
import com.sleepace.sdk.core.light.interfs.OnlineStateListener;
import com.sleepace.sdk.core.light.interfs.WorkStatusListener;
import com.sleepace.sdk.sa1001_wan.domain.WorkStatus;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.wifidevice.bean.DeviceInfo;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	
	private RadioGroup rgTab;
	private FragmentManager fragmentMgr;
	private Fragment loginFragment, controlFragment, settingFragment;
	private ProgressDialog upgradeDialog;
	private WorkStatus workStatus;
	
	//缓存数据
//	public static String deviceId = "o0zguh6yxmi5o";
	public static DeviceInfo device = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initListener();
		initUI();
	}
	
	public WorkStatus getWorkStatus() {
		return workStatus;
	}
	
	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}
	
	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		super.findView();
		rgTab = (RadioGroup) findViewById(R.id.rg_tab);
	}


	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		mHelper.registOnlineStateListener(onlineStateListener);
		mHelper.registWorkStatusListener(workStatusListener);
		rgTab.setOnCheckedChangeListener(checkedChangeListener);
	}


	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		super.initUI();
		fragmentMgr = getFragmentManager();
		loginFragment = new LoginFragment();
		controlFragment = new ControlFragment();
		settingFragment = new SettingFragment();
		
		rgTab.check(R.id.rb_login);
//		rgTab.check(R.id.rb_control);
//		rgTab.check(R.id.rb_setting);
		ivBack.setVisibility(View.GONE);
		
		upgradeDialog = new ProgressDialog(this);
		upgradeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
		upgradeDialog.setMax(100);
		upgradeDialog.setIcon(android.R.drawable.ic_dialog_info);
		upgradeDialog.setTitle(R.string.fireware_update);
		upgradeDialog.setMessage(getString(R.string.upgrading));
		upgradeDialog.setCancelable(false);
		upgradeDialog.setCanceledOnTouchOutside(false);
	}
	
	
	public void setTitle(int res){
		tvTitle.setText(res);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == ivBack){
			exit();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public void exit(){
//		mHelper.release();
		clearCache();
//		Intent intent = new Intent(this, SplashActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
		finish();
	}
	
	
	private void clearCache(){
		
	}
	
	private OnlineStateListener onlineStateListener = new OnlineStateListener() {
		@Override
		public void onlineStateChanged(byte onlineState) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onlineStateChanged:" + onlineState);
		}
	};
	
	private WorkStatusListener workStatusListener = new WorkStatusListener() {
		@Override
		public void onWorkStatusChanged(LightWorkStatus workStatus) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG+" onWorkStatusChanged:" + workStatus);
			MainActivity.this.workStatus = (WorkStatus) workStatus;
		}
	};
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			FragmentTransaction trans = fragmentMgr.beginTransaction();
			if(checkedId == R.id.rb_login){
				trans.replace(R.id.content, loginFragment);
			}else if(checkedId == R.id.rb_control){
				trans.replace(R.id.content, controlFragment);
			}else if(checkedId == R.id.rb_setting){
				trans.replace(R.id.content, settingFragment);
			}
			trans.commit();
		}
	};
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHelper.unregistOnlineStateListener(onlineStateListener);
		mHelper.unregistWorkStatusListener(workStatusListener);
	}
	
	public void showUpgradeDialog(){
		upgradeDialog.show();
	}
	
	public void setUpgradeProgress(int progress){
		upgradeDialog.setProgress(progress);
	}
	
	public void hideUpgradeDialog(){
		upgradeDialog.dismiss();
	}
	
	
	public void deviceDisconnect() {
        final Dialog dialog = new Dialog(this, R.style.myDialog);
        dialog.setContentView(R.layout.dialog_warn_tips);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvTips = (TextView) dialog.findViewById(R.id.warn_tips);
        Button btn = (Button) dialog.findViewById(R.id.warn_bt);
        
        tvTitle.setText(R.string.device_connect_fail);
        tvTips.setText(R.string.device_network);
        btn.setText(R.string.confirm);

        OnClickListener mListner = new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        btn.setOnClickListener(mListner);
        Window win = dialog.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
	
}








































