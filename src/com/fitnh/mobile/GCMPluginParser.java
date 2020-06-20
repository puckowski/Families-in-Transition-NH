package com.fitnh.mobile;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class GCMPluginParser 
{
	private final String TAG = "FIT | GCMPluginParser";
	
	private Context mContext;
	
	public GCMPluginParser(final Context context) {
		Log.v(TAG, "Default constructor call");
		
		mContext = context;
	}
	
	public GCMPluginMessage parseNewMessage(final String messageContents) {
		if(messageContents == null || messageContents.isEmpty() == true) {
			Log.w(TAG, "parseNewMessage() called but messageContents was null or empty");
			Log.w(TAG, "Unable to parse message contents; returning null");
			
			return null;
		} else if(mContext == null) {
			Log.w(TAG, "parseNewMessage() called but mContext was null");
			Log.w(TAG, "Unable to parse message contents; returning null");
			
			return null;
		}
		
		Log.v(TAG, "Parsing a new GCM message from plugin");
		GCMPluginMessage gcmPluginMessage = new GCMPluginMessage();
		
		try {
			JSONObject jsonObject = new JSONObject(messageContents);
			
			final String messageTitle = jsonObject.getString("title");
			Log.d(TAG, "Parsed message title: " + messageTitle);
			gcmPluginMessage.setTitle(messageTitle);
			
			final String messageId = jsonObject.getString("id");
			Log.d(TAG, "Parsed message ID: " + messageId);
			gcmPluginMessage.setMessageId(messageId);
			
			final String messagePostType = jsonObject.getString("pt");
			Log.d(TAG, "Parsed message post type:" + messagePostType);
			gcmPluginMessage.setPostType(messagePostType);
		} catch (JSONException jsonException) {
			Log.e(TAG, "A JSONException has occurred while parsing GCM message; returning null");
			
			return null;
		}
		
		return gcmPluginMessage;
	}
}
