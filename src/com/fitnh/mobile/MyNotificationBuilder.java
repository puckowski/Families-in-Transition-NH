package com.fitnh.mobile;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyNotificationBuilder 
{
	private static final String TAG = "FIT | MyNotificationBuilder";
	
	private Context mContext;
	
	private int mNotificationCount;
	
	public MyNotificationBuilder(final Context context) {
		Log.v(TAG, "Default constructor call");
		
		mContext = context;
		
		mNotificationCount = 0;
	}
	
	public void cleanup() {
		Log.v(TAG, "Cleaning up after MyNotificationBuilder");
		
		mContext = null;
		mNotificationCount = 0;
	}
	
	public void displayNotification(final String contentTitle, final String contentText) {
		if(contentTitle == null || contentTitle.isEmpty() == true) {
			Log.w(TAG, "displayNotification() called but contentTitle was null or empty");
			Log.w(TAG, "Unable to display notification");
			
			return;
		} else if(contentText == null || contentText.isEmpty() == true) {
			Log.w(TAG, "displayNotification() called but contentText was null or empty");
			Log.w(TAG, "Unable to display notification with title: " + contentTitle);
			
			return;
		} else if(mContext == null) {
			Log.w(TAG, "displayNotification() called but mContext was null");
			Log.w(TAG, "Unable to display notification with title: " + contentTitle);
			
			return;
		}
		
		Log.v(TAG, "Displaying a new notification with title: " + contentTitle);
		
		NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(mContext);

		notificationCompatBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationCompatBuilder.setContentTitle(contentTitle);
		notificationCompatBuilder.setContentText(contentText);
					
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(MainActivity.NOTIFICATION_SERVICE);
		notificationManager.notify(mNotificationCount, notificationCompatBuilder.build());
		
		mNotificationCount++;
		Log.d(TAG, "Current notification count: " + mNotificationCount);
	}
}
