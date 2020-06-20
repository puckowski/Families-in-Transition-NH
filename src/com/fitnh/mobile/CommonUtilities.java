package com.fitnh.mobile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class CommonUtilities {
    private static final String TAG = "FIT | CommonUtilities";

    public static final String SENDER_ID = "45320244779"; 

    public static final String DISPLAY_MESSAGE_ACTION = "com.fitnh.mobile.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "message";

    public static void displayMessage(Context context, String message) { 
    	if(message == null || message.isEmpty()) {
    		Log.w(TAG, "displayMessage() was called but no message was provided");
    		return;
    	}
    	
    	String logMessage = "";
    	if(message.length() < 32) {
    		logMessage = message;
    	} else {
    		logMessage = message.substring(0, 32) + "...";
    	}
    	
    	Log.v(TAG, "Displaying message: " + logMessage);
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        
        context.sendBroadcast(intent);
    }
}
