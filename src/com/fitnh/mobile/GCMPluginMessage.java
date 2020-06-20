package com.fitnh.mobile;

import android.util.Log;

public class GCMPluginMessage 
{
	private final String TAG = "FIT | GCMPluginMessage";
		
	private String mTitle;
	private String mMessageId;
	private String mPostType;
	
	public GCMPluginMessage() {
		Log.v(TAG, "Default constructor call");
		
		mTitle = null;
		mMessageId = null;
		mPostType = null;
	}
	
	public void setTitle(final String title) {
		if(title == null || title.isEmpty() == true) {
			Log.w(TAG, "setTitle() called but title was null or empty");
			Log.w(TAG, "Proceeding to set title, but unexpected errors may occur");
		}
		
		Log.v(TAG, "Setting GCMPluginMessage title");
		
		mTitle = title;
	}
	
	public void setMessageId(final String messageId) {
		if(messageId == null || messageId.isEmpty() == true) {
			Log.w(TAG, "setTitle() called but title was null or empty");
			Log.w(TAG, "Proceeding to set title, but unexpected errors may occur");
		}
		
		Log.v(TAG, "Setting GCMPluginMessage message ID");
		
		mMessageId = messageId;
	}
	
	public void setPostType(final String postType) {
		if(postType == null || postType.isEmpty() == true) {
			Log.w(TAG, "setTitle() called but title was null or empty");
			Log.w(TAG, "Proceeding to set title, but unexpected errors may occur");
		}
		
		Log.v(TAG, "Setting GCMPluginMessage post type");
		
		mPostType = postType;
	}
	
	public String getTitle() {
		Log.v(TAG, "Getting GCMPluginMessage title");
		
		return mTitle;
	}
	
	public String getMessageId() {
		Log.v(TAG, "Getting GCMPluginMessage message ID");
		
		return mMessageId;
	}
	
	public String getPostType() {
		Log.v(TAG, "Getting GCMPluginMessage post type");
		
		return mPostType;
	}
}
