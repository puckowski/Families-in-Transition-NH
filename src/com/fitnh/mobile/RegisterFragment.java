package com.fitnh.mobile;

import com.fitnh.mobile.R;
import com.google.android.gcm.GCMRegistrar;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment { 
	private final String TAG = "FIT | RegisterFragment";
	private final String GCM_PREFERENCES = "gcm_preferences";
	public final String FRAGMENT_INDEX = "fragment_index";
	
    private Context mContext;
    
    private String mFragmentTitle;
    
    private ScrollView mScrollView;
    private LinearLayout mRegisterLayout;
    private EditText mNameEditText;
	private EditText mEmailEditText;
    private Button mMessagingInfoButton;
    private Button mRegisterButton;
    private Button mUnregisterButton;
    private Button mDebugShowId;
    
    public RegisterFragment() {
    	Log.v(TAG, "Calling default constructor");
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate()");
    }
    
    private void createTitle(final Resources resources) {
    	Log.v(TAG, "Creating action bar title for RegisterFragment");

    	mFragmentTitle = "Families in Transition | Registration";
	    setNewActionBarTitle();
    }
    
    private void setCustomOverscrollColor(final Context context) {
    	if(context == null) {
    		Log.w(TAG, "setCustomOverscrollColor() called but Context was null");
    		Log.w(TAG, "Could not override Android overscroll color");
    		
    		return;
    	}
    	
		Log.w(TAG, "Overriding default Android overscroll color");		 
		int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
		
		try {
			Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
			androidGlow.setColorFilter(context.getResources().getColor(R.color.fit_green_value_90), PorterDuff.Mode.MULTIPLY);
		} catch(Exception exception) {
			Log.e(TAG, "Could not identify overscroll drawable");
			Log.w(TAG, "Overscroll color will remain default color");
			
			return;
		}
		
		int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
		
		try {
			Drawable androidEdge = context.getResources().getDrawable(edgeDrawableId);
			androidEdge.setColorFilter(context.getResources().getColor(R.color.fit_green_value_90), PorterDuff.Mode.MULTIPLY);
		} catch(Exception exception) {
			Log.e(TAG, "Could not identify overscroll edge drawable");
			Log.w(TAG, "Overscroll edge color will remain default color");
			
			return;
		}
	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {   
    	Log.v(TAG, "onCreateView()");    	
        final View rootView = inflater.inflate(R.layout.fragment_gcm_register, container, false); 
        mScrollView = (ScrollView) rootView.findViewById(R.id.mainScrollView);
        mRegisterLayout = (LinearLayout) rootView.findViewById(R.id.register_layout);
        
        Log.d(TAG, "Calling ((ScrollView) parentView).requestTransparentRegion(child) to prevent screen flickering");
        mScrollView.requestTransparentRegion(mRegisterLayout);
        
        mContext = getActivity();
        setCustomOverscrollColor(mContext);
        
    	Resources resources = getResources();
    	createTitle(resources);
    	
        mNameEditText = (EditText) rootView.findViewById(R.id.name_edit_text);
        mEmailEditText = (EditText) rootView.findViewById(R.id.email_edit_text);
        mMessagingInfoButton = (Button) rootView.findViewById(R.id.push_notification_info_button);
        mRegisterButton = (Button) rootView.findViewById(R.id.push_notifications_register_button); 
        mUnregisterButton = (Button) rootView.findViewById(R.id.push_notifications_unregister_button);
        mDebugShowId = (Button) rootView.findViewById(R.id.show_gcm_id_button);
        
        setupRegisterLayout(rootView);
        
        ((MainActivity) mContext).getThreadUtilities().cleanupEnvironment();
        return rootView;
    }
    
    private boolean hasFormBeenCompleted() {
    	Log.v(TAG, "Checking if registration form has been completed");
    	if(mContext == null) {
    		return false;
    	}
    	
    	SharedPreferences sharedPreferences = mContext.getSharedPreferences(GCM_PREFERENCES, 0);
        boolean hasFormBeenCompleted = sharedPreferences.getBoolean("registration_form_complete", false);
        
        return hasFormBeenCompleted;
    }
    
    private void setupRegisterLayout(final View rootView) {
    	Log.v(TAG, "Setting up GCM registration layout");    	
    	TextView infoView = (TextView) rootView.findViewById(R.id.push_messaging_description_view);
    	infoView.setText(null);
    	infoView.setVisibility(View.GONE);
    	
    	SharedPreferences sharedPreferences = mContext.getSharedPreferences(GCM_PREFERENCES, 0);
    	String userName = sharedPreferences.getString("gcm_user_name", "");
    	String userEmail = sharedPreferences.getString("gcm_user_email", "");
    	
    	mNameEditText.setText(userName);
    	mEmailEditText.setText(userEmail);
    	
        mMessagingInfoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	TextView infoView = (TextView) rootView.findViewById(R.id.push_messaging_description_view);
            	
            	if(infoView.getText().length() == 0) {
            		infoView.setText(R.string.push_messaging_description);
            		AnimationManager animationManager = new AnimationManager(mContext);
            		infoView.setAnimation(animationManager.getAnimation(animationManager.FADE_IN));
            		infoView.setVisibility(View.VISIBLE);
            		
            		mMessagingInfoButton.setText(R.string.push_messaging_collapse_info);
            		focusOnView(mMessagingInfoButton);
            	} else {
            		infoView.setText(null);
            		infoView.setVisibility(View.GONE);
            		
            		mMessagingInfoButton.setText(R.string.push_messaging_expand_info);
            		focusOnView(mMessagingInfoButton);
            	}
            }
        });
        
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	if(hasFormBeenCompleted()) {
            		showOverwriteUserInformationDialog();
            	} else {
	            	writeUserInformation();
            	}
            }
        });
        
        mUnregisterButton = (Button) rootView.findViewById(R.id.push_notifications_unregister_button);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	GCMRegistrar.unregister(mContext); 
            	setPushNotificationsDisabled(true);
            }
        });
        
        mDebugShowId = (Button) rootView.findViewById(R.id.show_gcm_id_button);
        mDebugShowId.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	SharedPreferences sharedPreferences = mContext.getSharedPreferences("gcm_preferences", 0);
            	String registrationId = sharedPreferences.getString("gcm_registration_id", "Invalid");
            	Toast.makeText(mContext, registrationId, Toast.LENGTH_LONG).show();
            }
        });
    }
   
    private void showOverwriteUserInformationDialog() {
    	Log.v(TAG, "showOverwriteUserInformationDialog()");
    	
    	AlertDialogManager alertManager = new AlertDialogManager();
    	AlertDialog.Builder overwriteNoticeDialogBuilder = alertManager.getAlertDialogBuilder(mContext, "User Information", 
    			"A name and email have already been saved. Use the new information instead?");
    	
    	overwriteNoticeDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialogInterface, int which) {
    			writeUserInformation();
    			setPushNotificationsDisabled(false);
    		}
    	}).setNegativeButton("Cancel", null);
    	
    	alertManager.finishBuildingDialog(overwriteNoticeDialogBuilder);
    }
    
    private void writeUserInformation() {
    	Log.v(TAG, "writeUserInformation()");
    	String userName = mNameEditText.getText().toString();
		String userEmail = mEmailEditText.getText().toString();
		
		if(userName.trim().length() > 0 && userEmail.trim().length() > 0) {	
			if(!userEmail.endsWith("@gmail.com")) {
				Toast.makeText(mContext, "Please provide a Gmail address (example: user@gmail.com)", Toast.LENGTH_LONG).show();
				return;
			}
			
			SharedPreferences sharedPreferences = mContext.getSharedPreferences(GCM_PREFERENCES, 0);
		    SharedPreferences.Editor editor = sharedPreferences.edit();
		    
		    editor.putString("gcm_user_name", userName);
		    editor.putString("gcm_user_email", userEmail);
		    editor.putBoolean("registration_form_complete", true);
		    
		    editor.commit();
		    Toast.makeText(mContext, "Information saved", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "Please fill out both fields", Toast.LENGTH_SHORT).show();
		}
    }
    
    private void setPushNotificationsDisabled(final boolean notificationsDisabled) {
    	if(mContext == null) {
    		Log.w(TAG, "setPushNotificationsDisabled() called but mContext was null");
    		Log.w(TAG, "Unable to set push notifications disabled preference to: " + String.valueOf(notificationsDisabled));
    		
    		return;
    	}
    	
    	Log.v(TAG, "Setting push notifications disabled preference to: " + String.valueOf(notificationsDisabled));
    	
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    
	    editor.putBoolean("disable_push_messages", notificationsDisabled);
	    editor.commit();
    }
    
    private final void focusOnView(final View thisView) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
            	Log.v(TAG, "focusOnView()"); 
                mScrollView.smoothScrollTo(0, thisView.getTop());
            }
        });
    }
    
    private void setNewActionBarTitle() {
    	Log.v(TAG, "Setting a new action bar title");
    	if(mContext == null || mFragmentTitle == null) {
    		return;
    	}
    	
    	((MainActivity) mContext).setTitle(mFragmentTitle);
    }
    
    @Override
    public void onDestroy() {
    	Log.v(TAG, "onDestroy()");
    	cleanupFragment();
    	
    	Log.v(TAG, "Calling super.onDestroy()");
    	super.onDestroy();
    }
    
    private void cleanupFragment() {
    	Log.v(TAG, "Cleaning up after RegisterFragment");
    	mContext = null;
        mFragmentTitle = null; 
        
        mScrollView = null;
        mRegisterLayout = null;
        mNameEditText = null;
    	mEmailEditText = null;
        mMessagingInfoButton = null;
        mRegisterButton = null;
        mUnregisterButton = null;
        mDebugShowId = null;
    }
}
