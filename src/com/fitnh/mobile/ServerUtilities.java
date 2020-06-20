package com.fitnh.mobile;

import static com.fitnh.mobile.CommonUtilities.displayMessage;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.fitnh.mobile.R;
import com.google.android.gcm.GCMRegistrar;

public final class ServerUtilities {
	private static final String TAG = "FIT | ServerUtilities";
	private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random mRandom = new Random();

    public static void register(final Context context, String name, String email, final String registrationId, final String oldRegistrationId) {
    	if(context == null) {
    		Log.w(TAG, "register() called but Context was null");
    		Log.w(TAG, "Unable to proceed to server registration");
    		
    		return;
    	} else if(name == null || name.isEmpty()) {
    		Log.w(TAG, "register() called but name was null");
    		Log.w(TAG, "Unable to proceed to server registration");
    		
    		return;
    	} else if(email == null || email.isEmpty()) {
    		Log.w(TAG, "register() called but email was null");
    		Log.w(TAG, "Unable to proceed to server registration");
    		
    		return;
    	} else if(registrationId == null || registrationId.isEmpty()) {
    		Log.w(TAG, "register() called but registrationId was null");
    		Log.w(TAG, "Unable to proceed to server registration");
    		
    		return;
    	} 
    	
        Log.v(TAG, "Registering device with registration ID: " + registrationId);
        
        long backoff = BACKOFF_MILLI_SECONDS + mRandom.nextInt(1000);
        
        PostRequestFormatter postRequestFormatter = new PostRequestFormatter(context);
        boolean registrationSuccess = false;
        
        for(int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.v(TAG, "Attempting to register device (attempt " + i + ")");

			displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
			
			if(oldRegistrationId != null) {
				registrationSuccess = postRequestFormatter.createNewUpdateRequest(oldRegistrationId, registrationId);
			} else {
				registrationSuccess = postRequestFormatter.createNewRegistrationRequest(registrationId);
			}

			if (registrationSuccess == true) {
				GCMRegistrar.setRegisteredOnServer(context, registrationSuccess);

				String message = context.getString(R.string.server_registered);
				CommonUtilities.displayMessage(context, message);

				return;
			}

			Log.e(TAG, "Failed to register device on attempt " + i + ""); // with
																			// IOException");

			if (i == MAX_ATTEMPTS) {
				break;
			}

			try {
				Log.d(TAG, "Sleeping for " + backoff + " milliseconds before retrying to register");
				Thread.sleep(backoff);
			} catch (InterruptedException interruptedException) {
				Log.e(TAG, "Thread was interrupted; aborting remaining registration retries");
				
				return;
			}

			backoff *= 2;
        }
        
        String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
    }

    public static void unregister(final Context context, final String registrationId) {
    	/*if(context == null) {
    		Log.w(TAG, "unregister() called but Context was null");
    		Log.w(TAG, "Unable to proceed with unregistering device");
    		
    		return;
    	} else if(registrationId == null || registrationId.isEmpty()) {
    		Log.w(TAG, "unregister() called but registration ID was null");
    		Log.w(TAG, "Unable to proceed with unregistering device");
    		
    		return;
    	}*/
    	
        Log.v(TAG, "Unregistering device with registration ID: " + registrationId);
    	
    	/*  */
    	//boolean serverUnregisterSuccess = postRequestFormatter.createNewUnregisterRequest()
    	GCMRegistrar.setRegisteredOnServer(context, false); //serverUnregisterSuccess
        
        String message = context.getString(R.string.server_unregistered);
        CommonUtilities.displayMessage(context, message);
        
        //Error
        //String message = context.getString(R.string.server_unregister_error, ioException.getMessage());
        //CommonUtilities.displayMessage(context, message);
        /*  */
    }
}
