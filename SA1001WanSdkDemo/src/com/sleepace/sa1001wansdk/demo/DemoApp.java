package com.sleepace.sa1001wansdk.demo;

import com.sleepace.sa1001wansdk.demo.R;
import com.sleepace.sa1001wansdk.demo.util.CrashHandler;
import com.sleepace.sdk.util.SdkLog;

import android.app.Application;

public class DemoApp extends Application {

	public static final String APP_TAG = "SA1001Sdk";

	public static final int[][] ALARM_MUSIC = { 
			{ 31051, R.string.alarm_list_1 }, 
			{ 31052, R.string.alarm_list_2 }, 
			{ 31054, R.string.alarm_list_3 }, 
			{ 31055, R.string.alarm_list_4 },
			{ 31056, R.string.alarm_list_5 }, 
			{ 31057, R.string.alarm_list_6 }, 
			{ 31058, R.string.alarm_list_7 }, 
			{ 31059, R.string.alarm_list_8 }, 
			{ 31060, R.string.alarm_list_9 } };

	public static final int[][] SLEEPAID_MUSIC = { 
			{ 31036, R.string.music_list_wind }, 
			{ 31037, R.string.music_list_sun }, 
			{ 31038, R.string.music_list_sea }, 
			{ 31039, R.string.music_list_summer },
			{ 31040, R.string.music_list_rain }, 
			{ 31041, R.string.music_list_dance }, 
			{ 31068, R.string.music_list_Nowhere },
			{ 31070, R.string.music_list_Galaxy },
			{ 31069, R.string.music_list_Purity },
			{ 31071, R.string.music_list_Journey },
			{ 31072, R.string.music_list_Here },
			{ 31045, R.string.music_list_solo }, 
			{ 31046, R.string.music_list_You },
			{ 31049, R.string.music_list_Moon },
			{ 31050, R.string.music_list_World },
			{ 31053, R.string.music_list_Baby },
			{ 31061, R.string.music_list_Lullaby },
			{ 31062, R.string.music_list_star }
	};

	private static DemoApp instance;
	
	private static long seqId = 0;
	
	public synchronized static long getSeqId() {
		seqId++;
		if(seqId >= Long.MAX_VALUE ) {
			seqId = 1;
		}
		return seqId;
//		return 100;
	}

	public static DemoApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		CrashHandler.getInstance().init(this);
		SdkLog.setLogEnable(true);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		// 低内存的时候执行
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// 程序在内存清理的时候执行
		super.onTrimMemory(level);

	}

}
