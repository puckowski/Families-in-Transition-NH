package com.fitnh.mobile;

import android.util.Log;

public class ThreadUtilities {
	private final String TAG = "FIT | ThreadUtilities";
		
	private Thread mCollectionThread;
	
	public ThreadUtilities() {
		Log.v(TAG, "Calling constructor ThreadUtilities(Context)");
		
		setupCollectionThread();
	}
	
	private void setupCollectionThread() {
		Log.v(TAG, "Setting up CollectionThread");
		
		mCollectionThread = new Thread() {
            @Override
            public void run() {
            	Log.v(TAG, "CollectionThread now running with THREAD_PRIORITY_LOWEST");
            	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
        		
            	System.gc();
                Runtime.getRuntime().gc();
                
                Log.d(TAG, "CollectionThread has finished running");
            }
        };
	}
	
	public void cancelAll() {
		Log.v(TAG, "Interrupting all Threads and setting them to null");
		
		if(mCollectionThread != null) { 
			Log.v(TAG, "Interrupting CollectionThread now");
			mCollectionThread.interrupt();
		} 

		mCollectionThread = null;
	}
	
	public void cleanupEnvironment() {
		Log.v(TAG, "cleanupEnvironment()");
		
		if(mCollectionThread.getState() == Thread.State.NEW) {
			Log.d(TAG, "Starting CollectionThread");
			mCollectionThread.start();
		} else if(mCollectionThread.getState() == Thread.State.TERMINATED) {
			Log.d(TAG, "Running CollectionThread");
			mCollectionThread.run();
		} else {
			Log.d(TAG, "CollectionThread not changed");
			Log.d(TAG, "CollectionThread's current state: " + String.valueOf(mCollectionThread.getState()));
		}
    }
}
