package com.fitnh.mobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class TimeFormatter {
	private final String TAG = "FIT | TimeFormatter";
	private final String TIME_STAMP_FORMAT = "MM/dd h:mm a"; 

	private final Locale DEFAULT_LOCALE = Locale.US;
		
	public TimeFormatter() {
		Log.v(TAG, "Calling constructor TimeFormatter(Context)");
	}
	
	public String getTimeStamp() {
		Log.v(TAG, "getTimeStamp() with default locale: Locale.US");
		
		try {
	        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_STAMP_FORMAT, DEFAULT_LOCALE);
	        String currentTimeStamp = dateFormat.format(new Date()); 

	        return currentTimeStamp;
	    } catch(Exception exception) {
	        Log.e(TAG, "Could not create time stamp with SimpleDateFormat: " + TIME_STAMP_FORMAT);
	        Log.v(TAG, "Returning empty String; not null");
	        
	        return "";
	    } 
	}
}
