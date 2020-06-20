package com.fitnh.mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public abstract class WakeLocker {
	private static final String TAG = "FIT | WakeLocker";
	private static final Long AQUIRE_MILLISECONDS_DURATION = 3600L; 
	
    @SuppressLint("Wakelock")
	public static void acquire(final Context context) {
    	if(context == null) {
    		Log.w(TAG, "acquire() called but Context was null");
    		return;
    	} else {
    		Log.v(TAG, "acquire()");
    	}
    	
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE); 
        PowerManager.WakeLock mWakeLocker = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |
        	PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG); 

        mWakeLocker.acquire(AQUIRE_MILLISECONDS_DURATION);
        Log.d(TAG, "Dim screen wake lock for " + String.valueOf(AQUIRE_MILLISECONDS_DURATION) + " milliseconds");
    }
}
