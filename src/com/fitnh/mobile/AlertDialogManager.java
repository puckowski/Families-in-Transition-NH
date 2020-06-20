package com.fitnh.mobile;

import com.fitnh.mobile.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialogManager {
	private final String TAG = "FIT | AlertDialogManager";
	private final String DIVIDER_IDENTIFIER = "android:id/titleDivider";
	
	public AlertDialog.Builder getAlertDialogBuilder(final Context context, final String title, final String message) {
		if(context == null) {
			Log.w(TAG, "getAlertDialogBuilder() called but Context was null");
			Log.w(TAG, "Could not create an AlertDialog.Builder; returning null");
			
			return null;
		} else if(title == null || title.isEmpty()) {
			Log.w(TAG, "getAlertDialogBuilder() called but title was null");
			Log.w(TAG, "Could not create an AlertDialog.Builder; returning null");
			
			return null;
		} else if(message == null || message.isEmpty()) {
			Log.w(TAG, "getAlertDialogBuilder() called but message was null");
			Log.w(TAG, "Could not create an AlertDialog.Builder; returning null");
			
			return null;
		} 
		
		Log.v(TAG, "Creating an AlertDialog.Builder for an AlertDialog with title: " + title);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View titleView = layoutInflater.inflate(R.layout.custom_alert_dialog_header, null);
		((TextView) titleView.findViewById(R.id.title_view)).setText(title);
		
		View dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null);
		((TextView) dialogView.findViewById(R.id.dialog_view)).setText(message); 
		
		dialogBuilder.setCustomTitle(titleView);
		dialogBuilder.setView(dialogView); 
		
		return dialogBuilder;
	}
	
	public void finishBuildingDialog(final AlertDialog.Builder alertDialogBuilder) {
		if(alertDialogBuilder == null) {
			Log.w(TAG, "finishBuildingDialog() called but AlertDialog.Builder was null");
			Log.w(TAG, "Could not finish building AlertDialog; AlertDialog was not shown");
			
			return;
		}
		
		Log.v(TAG, "Completing build of AlertDialog");
    	
		AlertDialog alertDialog = alertDialogBuilder.create();
    	alertDialog.show();
    	
    	final Context context = alertDialog.getContext();
    	
    	styleButtons(alertDialog);
    	styleTitleDivider(context, alertDialog);
	}
	
	
	private void styleButtons(AlertDialog alertDialog) {
		if(alertDialog == null) {
			Log.w(TAG, "styleButtons() called but AlertDialog was null");
			Log.w(TAG, "AlertDialog buttons were not styled");
			
			return;
		}
		
		Log.v(TAG, "Styling AlertDialog buttons");
		
		Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
    	if(button != null) {
    		button.setBackgroundResource(R.drawable.alert_dialog_selector);
    	}
    	
    	button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
    	if(button != null) {
    		button.setBackgroundResource(R.drawable.alert_dialog_selector);
    	}
    	
    	button = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
    	if(button != null) {
    		button.setBackgroundResource(R.drawable.alert_dialog_selector);
    	}
	}
	
	private void styleTitleDivider(final Context context, AlertDialog alertDialog) {
		if(context == null) {
			Log.w(TAG, "styleTitleDivider() called but Context was null");
			Log.w(TAG, "AlertDialog title divider was not styled");
			
			return;
		} else if(alertDialog == null) {
			Log.w(TAG, "styleTitleDivider() called but AlertDialog was null");
			Log.w(TAG, "AlertDialog title divider was not styled");
			
			return;
		}
		
		Log.v(TAG, "Styling AlertDialog title divider");
		
		int dividerId = context.getResources().getIdentifier(DIVIDER_IDENTIFIER, null, null);
    	View divider = alertDialog.findViewById(dividerId);
    	
    	divider.setBackgroundColor(context.getResources().getColor(R.color.fit_green_value_90));
	}
}
