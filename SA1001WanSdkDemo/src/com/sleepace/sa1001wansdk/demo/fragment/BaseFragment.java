package com.sleepace.sa1001wansdk.demo.fragment;

import com.sleepace.sa1001wansdk.demo.BaseActivity.MyOnTouchListener;
import com.sleepace.sa1001wansdk.demo.MainActivity;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.sa1001_wan.SA1001Helper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	
	protected String TAG = getClass().getSimpleName();
	protected MainActivity mActivity;
	protected SA1001Helper mHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		mHelper = SA1001Helper.getInstance(mActivity);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
	}


	protected void initListener() {
		// TODO Auto-generated method stub
	}

	protected void initUI() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void registerTouchListener(MyOnTouchListener myOnTouchListener) {
		mActivity.registerTouchListener(myOnTouchListener);
    }
	
    public void unregisterTouchListener(MyOnTouchListener myOnTouchListener) {
    	mActivity.unregisterTouchListener(myOnTouchListener);
    }
    
    protected void showErrDialogIfNeed(CallbackData cd) {
    	if(isAdded() && !cd.isSuccess()) {
    		mActivity.runOnUiThread(new Runnable() {
    			@Override
    			public void run() {
    				// TODO Auto-generated method stub
    				mActivity.deviceDisconnect();
    			}
    		});
    	}
    }
	
}



