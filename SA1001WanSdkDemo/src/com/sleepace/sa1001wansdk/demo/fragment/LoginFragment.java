package com.sleepace.sa1001wansdk.demo.fragment;

import java.util.HashMap;
import java.util.List;

import com.sleepace.sa1001wansdk.demo.MainActivity;
import com.sleepace.sa1001wansdk.demo.R;
import com.sleepace.sa1001wansdk.demo.util.ActivityUtil;
import com.sleepace.sdk.core.light.domain.UpgradeStatus;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.sa1001_wan.SA1001Helper;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.wifidevice.WiFiDeviceSdkHelper;
import com.sleepace.sdk.wifidevice.bean.DeviceInfo;
import com.sleepace.sdk.wifidevice.bean.UpgradeInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends BaseFragment {
	private Button btnConnect, btnUpgrade, btnBindDevice, btnUnbindDevice;
	private EditText etServerAddress, etToken, etChannelId, etDeviceId, etVersion;
	private WiFiDeviceSdkHelper wifiDeviceSdkHelper;
	private SA1001Helper mHelper;
	private ProgressDialog progressDialog;
	private ProgressDialog upgradeDialog;
	private SharedPreferences mSetting;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_login, null);
		// LogUtil.log(TAG+" onCreateView-----------");
		wifiDeviceSdkHelper = WiFiDeviceSdkHelper.getInstance(mActivity.getApplicationContext());
		mHelper = SA1001Helper.getInstance(mActivity.getApplicationContext());
		mSetting = mActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
		findView(root);
		initListener();
		initUI();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		btnConnect = (Button) root.findViewById(R.id.btn_connect);
		btnUpgrade = (Button) root.findViewById(R.id.btn_upgrade_device);
		btnBindDevice = (Button) root.findViewById(R.id.btn_bind_device);
		btnUnbindDevice = (Button) root.findViewById(R.id.btn_unbind_device);
		etServerAddress = (EditText) root.findViewById(R.id.et_server_address);
		etToken = (EditText) root.findViewById(R.id.et_token);
		etChannelId = (EditText) root.findViewById(R.id.et_channel_id);
		etDeviceId = (EditText) root.findViewById(R.id.et_device_id);
		etVersion = (EditText) root.findViewById(R.id.et_device_version);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		btnUpgrade.setOnClickListener(this);
		btnConnect.setOnClickListener(this);
		btnBindDevice.setOnClickListener(this);
		btnUnbindDevice.setOnClickListener(this);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.login_in);
		progressDialog = new ProgressDialog(mActivity);
		progressDialog.setIcon(android.R.drawable.ic_dialog_info);
		progressDialog.setMessage(getString(R.string.connect_server));
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		upgradeDialog = new ProgressDialog(mActivity);
		upgradeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条的形式为圆形转动的进度条
		upgradeDialog.setMessage(getString(R.string.upgrading, ""));
		upgradeDialog.setCancelable(false);
		upgradeDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		String serverHost = mSetting.getString("serverHost", "http://120.24.169.204:8091");
//		String serverHost = mSetting.getString("serverHost", "http://172.14.0.65:8082");
		String serverHost = mSetting.getString("serverHost", "");
		etServerAddress.setText(serverHost);
//		String token = mSetting.getString("token", "wangyong");
		String token = mSetting.getString("token", "");
		etToken.setText(token);
//		String channelId = mSetting.getString("channelId", "13700");
		String channelId = mSetting.getString("channelId", "");
		etChannelId.setText(channelId);
//		String deviceId = mSetting.getString("deviceId", MainActivity.deviceId);
//		etDeviceId.setText(deviceId);
//		String version = mSetting.getString("version", "1.57");
//		String version = mSetting.getString("version", "");
//		etVersion.setText(version);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Editor editor = mSetting.edit();
		String serverHost = etServerAddress.getText().toString().trim();
		if (!TextUtils.isEmpty(serverHost)) {
			editor.putString("serverHost", serverHost);
		}
		String token = etToken.getText().toString().trim();
		if (!TextUtils.isEmpty(token)) {
			editor.putString("token", token);
		}
		String channelId = etChannelId.getText().toString().trim();
		if (!TextUtils.isEmpty(channelId)) {
			editor.putString("channelId", channelId);
		}
//		String deviceId = etDeviceId.getText().toString().trim();
//		if (!TextUtils.isEmpty(deviceId)) {
//			editor.putString("deviceId", deviceId);
//		}
//		String version = etVersion.getText().toString().trim();
//		if (!TextUtils.isEmpty(version)) {
//			editor.putString("version", version);
//		}
		editor.commit();
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
		if (v == btnConnect) {
			final String host = etServerAddress.getText().toString().trim();
			final String token = etToken.getText().toString().trim();
			final String channelId = etChannelId.getText().toString().trim();
			if(TextUtils.isEmpty(host) || TextUtils.isEmpty(token) || TextUtils.isEmpty(channelId)) {
				Toast.makeText(mActivity, R.string.complete, Toast.LENGTH_SHORT).show();
				return;
			}
			
			WiFiDeviceSdkHelper.initServerHost(host);
			progressDialog.show();
			mHelper.authorize(channelId, token, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					if(cd.isSuccess()) {
						if (ActivityUtil.isActivityAlive(mActivity)) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (cd.isSuccess()) {
										Toast.makeText(mActivity, R.string.connection_succeeded, Toast.LENGTH_SHORT).show();
										getBindedDevice();
									} else {
										progressDialog.dismiss();
										Toast.makeText(mActivity, R.string.confirm_correctly, Toast.LENGTH_SHORT).show();
									}
								}
							});
						}
					}else {
						if (ActivityUtil.isActivityAlive(mActivity)) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									progressDialog.dismiss();
									Toast.makeText(mActivity, R.string.confirm_correctly, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				}
			});
		} else if (v == btnUpgrade) {
			String deviceId = etDeviceId.getText().toString().trim();
			if (TextUtils.isEmpty(deviceId) || MainActivity.device == null) {
				return;
			}
			checkUpdate(deviceId);
		} else if (v == btnBindDevice) {
			progressDialog.show();
			final String deviceId = etDeviceId.getText().toString().trim();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("deviceId", deviceId);
			args.put("leftRight", 0);
			wifiDeviceSdkHelper.bindDevice(args, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG + " bindDevice cd:" + cd);
					if (ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								if (cd.isSuccess()) {
									Toast.makeText(mActivity, R.string.bind_account_success, Toast.LENGTH_SHORT).show();
									getBindedDevice();
								} else {
									Toast.makeText(mActivity, R.string.bind_fail, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			});
		} else if (v == btnUnbindDevice) {
			progressDialog.show();
			String deviceId = etDeviceId.getText().toString().trim();
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("deviceId", deviceId);
			args.put("leftRight", 0);
			wifiDeviceSdkHelper.unbindDevice(args, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG + " unbindDevice cd:" + cd);
					if (ActivityUtil.isActivityAlive(mActivity)) {
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								if (cd.isSuccess()) {
									Toast.makeText(mActivity, R.string.unbind_success, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(mActivity, R.string.unbind_failed, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			});
		}
	}
	
	private void getBindedDevice() {
		wifiDeviceSdkHelper.getBindedDevice(new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				SdkLog.log(TAG + " getBindedDevice cd:" + cd);
				if (ActivityUtil.isActivityAlive(mActivity)) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							progressDialog.dismiss();
							if(cd.isSuccess()) {
								List<DeviceInfo> list = (List<DeviceInfo>) cd.getResult();
								int size = list == null ? 0 : list.size();
								for(int i=0; i<size; i++) {
									DeviceInfo device = list.get(i);
									String deviceId = device.getDeviceId();
									String deviceName = device.getDeviceName();
									short deviceType = (short) device.getDeviceType();
									float deviceVersion = device.getDeviceVersion();
									String ext = device.getExt();
									DeviceType dType = DeviceType.getDeviceType(deviceType);
									if(DeviceType.isNoxSAW(dType)) {
										MainActivity.device = device;
										etDeviceId.setText(deviceId);
										etVersion.setText(String.valueOf(deviceVersion));
										break;
									}else {
										
									}
								}
							}
						}
					});
				}
			}
		});
	}
	
	private void checkUpdate(final String deviceId) {
		upgradeDialog.show();
		HashMap<String, Object> args = new HashMap<String, Object>();
		args.put("deviceId", deviceId);
		args.put("leftRight", 0);
		wifiDeviceSdkHelper.getlastFirmwareVersion(args, new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				SdkLog.log(TAG + " getlastFirmwareVersion cd:" + cd);
				if (ActivityUtil.isActivityAlive(mActivity)) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(cd.isSuccess()) {
								List<UpgradeInfo> list = (List<UpgradeInfo>) cd.getResult();
								int size = list == null ? 0 : list.size();
								UpgradeInfo upgradeInfo = null;
								boolean hasNewVersion = false;
								for(int i=0; i<size; i++) {
									upgradeInfo = list.get(i);
									short deviceType = (short) upgradeInfo.getDeviceType();
									DeviceType dType = DeviceType.getDeviceType(deviceType);
									if(DeviceType.isNoxSAW(dType)) {
										if(Float.valueOf(MainActivity.device.getDeviceVersion()) < upgradeInfo.getDeviceVersion()) {
											hasNewVersion = true;
										}
										break;
									}
								}
								
								if(!hasNewVersion) {
									upgradeDialog.dismiss();
									Toast.makeText(mActivity, R.string.latest_version, Toast.LENGTH_SHORT).show();
									return;
								}
								
								short curVersion = (short) (MainActivity.device.getDeviceVersion() * 100);
								short targetVersion = (short) (upgradeInfo.getDeviceVersion() * 100);
								byte upgradeMode = getUpgradeMode(MainActivity.device.getExt(), upgradeInfo.getExt());
								
								SdkLog.log(TAG + " upgradeDevice deviceId:" + deviceId+",curVersion:"+curVersion+",targetVersion:"+targetVersion+
										",upgradeMode:"+upgradeMode+",url:" + upgradeInfo.getUrl());
								
								mHelper.upgradeDevice(deviceId, deviceId, DeviceType.DEVICE_TYPE_NOX_SAW.getType(), curVersion, targetVersion, upgradeMode, upgradeInfo.getUrl(), new IResultCallback<UpgradeStatus>() {
									@Override
									public void onResultCallback(final CallbackData<UpgradeStatus> cd) {
										// TODO Auto-generated method stub
										if (ActivityUtil.isActivityAlive(mActivity)) {
											mActivity.runOnUiThread(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													short callbackType = cd.getCallbackType();
													if(callbackType == IDeviceManager.METHOD_UPGRADE) {
														if (!cd.isSuccess()) {
															upgradeDialog.dismiss();
															Toast.makeText(mActivity, R.string.up_failed, Toast.LENGTH_SHORT).show();
														}
													}else if(callbackType == IDeviceManager.METHOD_UPGRADE_PROGRESS) {
														if (cd.isSuccess()) {
															UpgradeStatus upgradeStatus = cd.getResult();
															int progress = upgradeStatus.getProgress();
															upgradeDialog.setProgress(progress);
															if (progress == 100) {
																upgradeDialog.dismiss();
																Toast.makeText(mActivity, R.string.up_success, Toast.LENGTH_SHORT).show();
															}
														}else {
															upgradeDialog.dismiss();
															Toast.makeText(mActivity, R.string.up_failed, Toast.LENGTH_SHORT).show();
														}
													}
												}
											});
										}
									}
								});
								
							}else {
								upgradeDialog.dismiss();
							}
						}
					});
				}
			}
		});
	}

	private byte getUpgradeMode(String deviceExt, String upgradeExt) {
		if (TextUtils.isEmpty(deviceExt) || TextUtils.isEmpty(upgradeExt)) {
			return 3;
		}
		String[] curExts = deviceExt.split(";");
		String[] newExts = upgradeExt.split(";");
		if (curExts.length != 3 || newExts.length != 3) {
			return 3;
		} else if (!newExts[2].equals(curExts[2])) {
			return 3;
		} else if (!newExts[1].equals(curExts[1])) {
			return 2;
		} else {
			return 1;
		}
	}
}
