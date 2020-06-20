package com.fitnh.mobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
 
public class ConnectionDetector {
	private static final String TAG = "FIT | ConnectionDetector";
	 
    private Context mContext;
 
    public ConnectionDetector(Context context) {
    	Log.v(TAG, "Calling ConnectionDetector(Context) constructor");
    	mContext = context;
    }
 
    public boolean isConnectingToInternet() {
    	if(mContext == null) {
    		Log.w(TAG, "isConnectingToInternet() was called but Context is null");
    		Log.w(TAG, "Unable to determine network state");
    		
    		return false;
    	}
    	
    	Log.v(TAG, "Checking for an active internet connection");
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
          
        if(connectivityManager != null) {
        	NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
              
        	if(networkInfo != null) {
        		for(int i = 0; i < networkInfo.length; i++) {
        			if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        	}
        }
         
        return false;
    }
}
