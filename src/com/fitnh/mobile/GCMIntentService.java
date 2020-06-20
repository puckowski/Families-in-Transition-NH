package com.fitnh.mobile;

import static com.fitnh.mobile.CommonUtilities.SENDER_ID;
import static com.fitnh.mobile.CommonUtilities.displayMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.fitnh.mobile.R;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "FIT | GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
    	if(context == null || registrationId == null || registrationId.isEmpty()) {
    		Log.w(TAG, "onRegistered() called but Context or registration ID were null");
    		Log.w(TAG, "Unable to proceed to server registration");
    		
    		return;
    	}

    	Log.v(TAG, "Device registered with registration ID: " + registrationId);
        displayMessage(context, "Your device registered with GCM");
        
        SharedPreferences sharedPreferences = getSharedPreferences("gcm_preferences", 0);
		final String userName = sharedPreferences.getString("gcm_user_name", "");
		final String userEmail = sharedPreferences.getString("gcm_user_email", "");
		final String userRegistrationId = sharedPreferences.getString("gcm_registration_id", null);
		
		Log.d(TAG, "Calling ServerUtilities.register() with name: " + userName + " and " +
			"email: " + userEmail); 
        ServerUtilities.register(context, userName, userEmail, registrationId, userRegistrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
    	/*if(context == null || registrationId == null || registrationId.isEmpty()) {
    		Log.w(TAG, "onUnregistered() called but Context or registration ID were null");
    		Log.w(TAG, "Unable to unregister with server");
    		
    		return;
    	}*/
    	
        Log.v(TAG, "Unregistering device now");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
    	if(context == null || intent == null) {
    		Log.w(TAG, "onMessage() called but Context or Intent were null");    		
    		return;
    	}
    	
        Log.v(TAG, "Received a new push message");
        
        /*  */
        /*MyNotificationBuilder mNotificationBuilder = new MyNotificationBuilder(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean pushMessagesDisabled = sharedPreferences.getBoolean("disable_push_messages", false);
		Log.d(TAG, "SharedPreferences disable_push_messages value: " + pushMessagesDisabled);
		
        if(pushMessagesDisabled == false) {
			GCMPluginParser gcmPluginParser = new GCMPluginParser(context);
			GCMPluginMessage gcmPluginMessage = gcmPluginParser.parseNewMessage(newMessage);
			
			if(gcmPluginMessage == null) {
				Log.w(TAG, "gcmPluginMessage is null; unable to display notification in tray");
			} else {
				mNotificationBuilder.displayNotification("FIT Update", gcmPluginMessage.getTitle());
			}
        } else {
        	Log.v(TAG, "Push messages are disabled in user preferences; " +
        		"skipping creation of new Notification");
        }*/
        /*  */
        
        String messageTitle = intent.getExtras().getString("title");
        String messagePostType = intent.getExtras().getString("pt");
        String messageId = intent.getExtras().getString("id");
        
        /*  */
        if(messageTitle == null) {
        	Log.e(TAG, "Push message appears to be empty");
        	messageTitle = "error";
        }
        /*  */
        
        displayMessage(context, messageTitle);
        generateNotification(context, messageTitle);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
    	if(context == null) {
    		Log.w(TAG, "onDeletedMessages() called but Context was null");    		
    		return;
    	}
    	
        Log.v(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        
        displayMessage(context, message);
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
    	if(context == null || errorId == null || errorId.isEmpty()) {
    		Log.w(TAG, "onError() called but Context or error ID were null");    		
    		return;
    	}
    	
        Log.v(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
    	if(context == null || errorId == null || errorId.isEmpty()) {
    		Log.w(TAG, "onRecoverableError() called but Context or error ID were null");    		
    		return false;
    	}
    	
        Log.v(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        
        Log.v(TAG, "Calling super.onRecoverableError()");
        return super.onRecoverableError(context, errorId);
    }

    private static void generateNotification(Context context, String message) {
    	if(context == null || message == null || message.isEmpty()) {
    		Log.w(TAG, "generateNotification() called but Context or message were null");    		
    		return;
    	}
    	
    	String logMessage = "";
    	if(message.length() < 32) {
    		logMessage = message;
    	} else {
    		logMessage = message.substring(0, 32) + "...";
    	}
    	Log.v(TAG, "Generating a new notification with message: " + logMessage);
    	
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        notification.defaults |= Notification.DEFAULT_SOUND;
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "sound_file_name.mp3");
        
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      
    }
}
