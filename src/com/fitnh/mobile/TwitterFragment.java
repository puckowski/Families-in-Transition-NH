package com.fitnh.mobile;

import com.fitnh.mobile.R;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class TwitterFragment extends Fragment {
	private final String TAG = "FIT | TwitterFragment";
	public final String FRAGMENT_INDEX = "fragment_index";
	
	private Context mContext;
    
    private String mFragmentTitle;
    private String mTwitterUsername;
    
    private ImageView mTwitterHeader;
    private Button mTwitterButton;
    private Button mWebBrowserButton;

    public TwitterFragment() {
    	Log.v(TAG, "Default constructor call");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");
    	super.onCreate(savedInstanceState);
        
    	Log.v(TAG, "onCreate()");    	
    	mContext = getActivity();
    	setCustomOverscrollColor(mContext);
    	
    	Resources resources = getResources();
    	createTitle(resources);
    	
        mTwitterUsername = resources.getString(R.string.twitter_username);
    }
    
    private void createTitle(final Resources resources) { 
    	Log.v(TAG, "Creating action bar title for TwitterFragment");
    	
    	mFragmentTitle = "Families in Transition | Twitter";
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
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.v(TAG, "onCreateView()");
        View rootView = layoutInflater.inflate(R.layout.fragment_twitter, container, false);        
        
        mTwitterHeader = (ImageView) rootView.findViewById(R.id.twitter_header);
        setupTwitterHeader();
        
        mTwitterButton = (Button) rootView.findViewById(R.id.twitter_launch);
        mTwitterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent twitterIntent = getTwitterUserIntent();
            	
            	if(twitterIntent != null) {
            		try {
            			Toast.makeText(mContext, getResources().getString(R.string.launching_twitter_message), Toast.LENGTH_SHORT).show();
            			startActivity(twitterIntent);
            		} catch(ActivityNotFoundException activityNotFoundException) {
            			Log.e(TAG, "Error in launching Twitter UserIntent");
            			Log.e(TAG, "Perhaps the Twitter application is not installed on device?");
            			
            			Toast.makeText(mContext, "The Twitter mobile app does not seem to be installed", Toast.LENGTH_LONG).show();
            		}
            	}
            }
        });
        
        mWebBrowserButton = (Button) rootView.findViewById(R.id.web_browser_launch);
        mWebBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent webBrowserIntent = getWebBrowserUserIntent();
            	
            	if(webBrowserIntent != null) {
            		try {
            			Toast.makeText(mContext, getResources().getString(R.string.launching_twitter_in_browser_message), Toast.LENGTH_SHORT).show();
            			startActivity(webBrowserIntent);
            		} catch(Exception exception) {
            			Log.e(TAG, "Error in launching Twitter web browser Intent");
            			Log.e(TAG, "Perhaps there isn't a web browser installed on device?");
            		}
            	}
            }
        });
        
        ((MainActivity) mContext).getThreadUtilities().cleanupEnvironment();
        return rootView;
    }
    
    private void setNewActionBarTitle() {
    	Log.v(TAG, "Setting a new action bar title");
    	if(mContext == null || mFragmentTitle == null) {
    		return;
    	}
    	
    	((MainActivity) mContext).setTitle(mFragmentTitle);
    }
    
    private void setupTwitterHeader() {
    	if(mContext == null) {
    		Log.w(TAG, "setupTwitterHeader() called but Context was null");
    		Log.w(TAG, "TwitterFragment header was not setup");
    		
    		return;
    	} 
    	
    	Log.v(TAG, "Setting up TwitterFragment header");
    	
    	Bitmap headerBitmap = ((MainActivity) mContext).getBitmapFactory().decodeResource(R.drawable.twitter_logo);
    	mTwitterHeader.setImageBitmap(headerBitmap);
    }
    
    private Intent getTwitterUserIntent() {
    	Log.v(TAG, "Getting a new Twitter UserIntent");
        try {
            mContext.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + mTwitterUsername));
            return twitterIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating Twitter UserIntent");
        }
        
        return null;
    }
    
    private Intent getWebBrowserUserIntent() {
    	Log.v(TAG, "Getting a new Twitter web browser Intent");
        try {
        	Intent webBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + mTwitterUsername));
        	return webBrowserIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating Twitter web browser Intent");
        }
        
        return null;
    }
    
    @Override
    public void onDestroy() {
    	Log.v(TAG, "onDestroy()");
    	cleanupFragment();
    	
    	Log.v(TAG, "Calling super.onDestroy()");
    	super.onDestroy();
    }
    
    private void cleanupFragment() {
    	Log.v(TAG, "Cleaning up after TwitterFragment");
    	mContext = null;

        mFragmentTitle = null;
        mTwitterUsername = null;
        
        mTwitterButton = null;
        mWebBrowserButton = null;
    }
}