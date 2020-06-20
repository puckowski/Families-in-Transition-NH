package com.fitnh.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.fitnh.mobile.R;

import android.content.Context;
import android.util.Log;

public class PostRequestFormatter 
{
	private final String TAG = "FIT | PostRequestFormatter";
	
	private final int STATUS_OK = 200;
	private final int STATUS_NOT_FOUND = 404;
	private final int STATUS_INTERNAL_SERVER_ERROR = 500;
	
	private Context mContext;
	
	private HttpClient mHttpClient;
	private HttpPost mHttpPost;
	
	private List<NameValuePair> mNameValuePairs;
	
	public PostRequestFormatter(final Context context) {
		Log.v(TAG, "Default constructor call");
		
		mContext = context;
		
		mHttpClient = null;
		mHttpPost = null;
		
		mNameValuePairs = new ArrayList<NameValuePair>(2);
	}
	
	public void cleanup() {
		Log.v(TAG, "Cleaning up after PostRequestFormatter");
		
		mHttpClient = null;
		mHttpPost = null;
		
		mNameValuePairs.clear();
		mNameValuePairs = null;
	}
	
	public boolean createNewRegistrationRequest(final String deviceRegistrationId) {
		if(mContext == null) {
			Log.w(TAG, "createNewRegistrationRequest() was called but mContext was null");
			Log.w(TAG, "Unable to create new registration post request");
			
			return false;
		} else if(deviceRegistrationId == null || deviceRegistrationId.isEmpty() == true) {
			Log.w(TAG, "createNewRegistrationRequest() was called but deviceRegistrationId was null or empty");
			Log.w(TAG, "Unable to create new registration post request");
			
			return false;
		}
		
		Log.v(TAG, "Creating a new registration request");
		
		final String registrationUrl = mContext.getResources().getString(R.string.gcm_registration_url);
		createRequestWithUrl(registrationUrl);
		
		final String tokenPairName = mContext.getResources().getString(R.string.gcm_request_token_pair_name);
		addNameValuePairToRequest(tokenPairName, deviceRegistrationId);
		
		final String oldTokenPairName = mContext.getResources().getString(R.string.gcm_request_old_token_pair_name);
		addNameValuePairToRequest(oldTokenPairName, "");
		
		final String osPairName = mContext.getResources().getString(R.string.gcm_request_os_pair_name);
		final String operatingSystemType = mContext.getResources().getString(R.string.gcm_operating_system_type);
		addNameValuePairToRequest(osPairName, operatingSystemType);
		
		boolean requestSuccess = executePostRequest();
		
		if(requestSuccess == true) {
			Log.v(TAG, "executePostRequest() indicates request was successful");
			Log.v(TAG, "Device has registered with site");
		} else {
			Log.w(TAG, "executePostRequest() indicates request was unsuccessful");
			Log.w(TAG, "Refer to prior log messages for more information");
		}
		
		return requestSuccess;
	}
	
	public boolean createNewUpdateRequest(final String oldRegistrationId, final String newRegistrationId) {
		if(mContext == null) {
			Log.w(TAG, "createNewUpdateRequest() was called but mContext was null");
			Log.w(TAG, "Unable to create new update post request");
			
			return false;
		} else if(oldRegistrationId == null || oldRegistrationId.isEmpty() == true) {
			Log.w(TAG, "createNewUpdateRequest() was called but oldRegistrationId was null or empty");
			Log.w(TAG, "Unable to create new update post request");
			
			return false;
		} else if(newRegistrationId == null || newRegistrationId.isEmpty() == true) {
			Log.w(TAG, "createNewUpdateRequest() was called but newRegistrationId was null or empty");
			Log.w(TAG, "Unable to create new update post request");
			
			return false;
		}
		
		Log.v(TAG, "Creating a new update request");
		
		final String registrationUrl = mContext.getResources().getString(R.string.gcm_registration_url);
		createRequestWithUrl(registrationUrl);
		
		final String tokenPairName = mContext.getResources().getString(R.string.gcm_request_token_pair_name);
		addNameValuePairToRequest(tokenPairName, newRegistrationId);
		
		final String oldTokenPairName = mContext.getResources().getString(R.string.gcm_request_old_token_pair_name);
		addNameValuePairToRequest(oldTokenPairName, oldRegistrationId);
		
		final String osPairName = mContext.getResources().getString(R.string.gcm_request_os_pair_name);
		final String operatingSystemType = mContext.getResources().getString(R.string.gcm_operating_system_type);
		addNameValuePairToRequest(osPairName, operatingSystemType);
		
		boolean requestSuccess = executePostRequest();
		
		if(requestSuccess == true) {
			Log.v(TAG, "executePostRequest() indicates request was successful");
			Log.v(TAG, "Device has updated registration with site");
		} else {
			Log.w(TAG, "executePostRequest() indicates request was unsuccessful");
			Log.w(TAG, "Refer to prior log messages for more information");
		}
		
		return requestSuccess;
	}
	
	public void createRequestWithUrl(String urlString) {
		if(urlString == null || urlString.isEmpty() == true) {
			Log.w(TAG, "createRequestWithUrl() called but urlString was null or empty");
			Log.w(TAG, "Could not create post request with URL");
			
			return;
		}
		
		Log.v(TAG, "Creating a post request with URL: " + urlString);
		
		if(urlString.startsWith("http://") == false) {
			Log.w(TAG, "Supplied URL String does not start with \"http://\"");
			Log.w(TAG, "Exceptions may occur during post request execution");
			
			Log.v(TAG, "Preappending URL String with \"http://\" now");
			urlString = "http://" + urlString;
		}
		
	    mHttpClient = new DefaultHttpClient();
	    mHttpPost = new HttpPost(urlString);
	} 
	
	public void addNameValuePairToRequest(final String objectName, final String objectValue) {
		if(objectName == null || objectName.isEmpty() == true) {
			Log.w(TAG, "addNameValuePairToRequest() called but objectName was null or empty");
			Log.w(TAG, "Could not add new BasicNameValuePair to list for post request");
			
			return;
		} else if(objectValue == null || objectName.isEmpty() == true) {
			Log.w(TAG, "addNameValuePairToRequest() called but objectName was null or empty");
			Log.w(TAG, "Could not add new BasicNameValuePair to list for post request");
			
			return;
		}
		
		if(mNameValuePairs == null) {
			Log.w(TAG, "addNameValuePairToRequest() called but mNameValuePairs was unexpectedly null");
			Log.w(TAG, "Instantiating mNameValuePairs now but other exceptions may occur");
			
			mNameValuePairs = new ArrayList<NameValuePair>(2);
		}
		
		Log.v(TAG, "Adding a new BasicNameValuePair to mNameValuePairs list");
		
		mNameValuePairs.add(new BasicNameValuePair(objectName, objectValue));
	}
	
	public boolean executePostRequest() {
		if(mHttpPost == null) {
			Log.w(TAG, "executePostRequest() was called but mHttpPost was null");
			Log.w(TAG, "Unable to execute post request");
			
			Log.d(TAG, "Perhaps createRequestWithUrl() was not called or cleanup() was called prematurely");
			
			clearNameValuePairList();
			return false;
		} else if(mHttpClient == null) {
			Log.w(TAG, "executePostRequest() was called but mHttpClient was null");
			Log.w(TAG, "Unable to execute post request");
			
			Log.d(TAG, "Perhaps createRequestWithUrl() was not called or cleanup() was called prematurely");
			
			clearNameValuePairList();
			return false;
		} else if(mNameValuePairs == null) {
			Log.w(TAG, "executePostRequest() was called but mNameValuePairs was null");
			Log.w(TAG, "Unable to execute post request");
			
			Log.d(TAG, "Perhaps cleanup() was called prematurely");
			
			clearNameValuePairList();
			return false;
		}
		
		Log.v(TAG, "Executing post request now");
		
		try {
	        mHttpPost.setEntity(new UrlEncodedFormEntity(mNameValuePairs));
	        HttpResponse httpResponse = mHttpClient.execute(mHttpPost);   
	        
	        StatusLine responseStatusLine = httpResponse.getStatusLine();
	        if(responseStatusLine == null) {
	        	Log.w(TAG, "Could not get a valid StatusLine from HttpResponse after request execution");
	        	Log.w(TAG, "Unable to determine whether or not request was successful");
	        	
	        	clearNameValuePairList();
	        	return false;
	        }
	        
	        int responseStatusCode = responseStatusLine.getStatusCode(); 
	        Log.d(TAG, "Status code returned was: " + String.valueOf(responseStatusCode));    
	        
	        if(responseStatusCode == STATUS_OK) {
	        	Log.v(TAG, "Post request was successful");
	        	
	        	clearNameValuePairList();
	        	return true;
	        } else if(responseStatusCode == STATUS_NOT_FOUND) {
	        	Log.w(TAG, "A NOT_FOUND exception has occurred during request execution");
	        	Log.w(TAG, "Request was unsuccessful");
	        	
	        	clearNameValuePairList();
	        	return false;
	        } else if(responseStatusCode == STATUS_INTERNAL_SERVER_ERROR) {
	        	Log.w(TAG, "An INTERNAL_SERVER_ERROR has occurred during request execution");
	        	Log.w(TAG, "Request was unsuccessful");
	        	
	        	clearNameValuePairList();
	        	return false;
	        } else {
	        	Log.w(TAG, "Unexpected statuc code returned from request");
	        	Log.w(TAG, "Unable to determine whether or not request was successful");

	        	clearNameValuePairList();
	        	return false;
	        }
	    } catch (ClientProtocolException clientProtocolException) {
	    	Log.e(TAG, "A ClientProtocolException has occurred");
			Log.w(TAG, "Could not complete post request with URL");
			
			clearNameValuePairList();
			return false;
	    } catch (IOException ioException) {
	    	Log.e(TAG, "An IOException has occurred");
			Log.w(TAG, "Could not complete post request with URL");
			
			clearNameValuePairList();
			return false;
	    }
	}
	
	private void clearNameValuePairList() {
		if(mNameValuePairs == null) {
			Log.w(TAG, "clearNameValuePairList() called but mNameValuePairs was null");
			Log.w(TAG, "Unable to clear mNameValuePairs");
		} else {
			Log.v(TAG, "clearNameValuePairList()");
			mNameValuePairs.clear();
		}
	}
}
