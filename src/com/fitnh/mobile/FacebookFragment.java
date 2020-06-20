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

public class FacebookFragment extends Fragment {
	private final String TAG = "FIT | FacebookFragment";
    public final String FRAGMENT_INDEX = "fragment_index";

    private Context mContext;
    
    private String mFragmentTitle;
    private String mFacebookUsername;
    private String mFacebookProfileId;
    
    private ImageView mFacebookHeader;
    private Button mFacebookButton;
    private Button mWebBrowserButton;
    
    public FacebookFragment() {
    	Log.v(TAG, "Default constructor call");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");
    	super.onCreate(savedInstanceState);  
    	
    	Log.v(TAG, "onCreate()");    	
    	mContext = getActivity();
    	
    	Resources resources = getResources();
    	createTitle(resources);
    	
        mFacebookUsername = resources.getString(R.string.facebook_username);
        mFacebookProfileId = resources.getString(R.string.facebook_profile_id);

        setCustomOverscrollColor(mContext);
    }
    
    private void createTitle(final Resources resources) { 
    	Log.v(TAG, "Creating action bar title for FacebookFragment");
    	
    	mFragmentTitle = "Families in Transition | Facebook";
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
        View rootView = layoutInflater.inflate(R.layout.fragment_facebook, container, false);        
        
        mFacebookHeader = (ImageView) rootView.findViewById(R.id.facebook_header);
        setupFacebookHeader();
        
        mFacebookButton = (Button) rootView.findViewById(R.id.facebook_launch);
        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent facebookIntent = getFacebookUserIntent();
            	
            	if(facebookIntent != null) {
            		try {
            			Toast.makeText(mContext, getResources().getString(R.string.launching_facebook_message), Toast.LENGTH_SHORT).show();
            			startActivity(facebookIntent);
            		} catch(ActivityNotFoundException activityNotFoundException) {
            			Log.e(TAG, "Error in launching Facebook UserIntent");
            			Log.e(TAG, "Perhaps the Facebook application is not installed on device?");
            			
            			Toast.makeText(mContext, "The Facebook mobile app does not seem to be installed", Toast.LENGTH_LONG).show();
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
            			Toast.makeText(mContext, getResources().getString(R.string.launching_facebook_in_browser_message), Toast.LENGTH_SHORT).show();
            			startActivity(webBrowserIntent);
            		} catch(Exception exception) {
            			Log.e(TAG, "Error in launching Facebook web browser Intent");
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
    
    private void setupFacebookHeader() {
    	if(mContext == null) {
    		Log.w(TAG, "setupFacebookHeader() called but Context was null");
    		Log.w(TAG, "FacebookFragment header was not setup");
    		
    		return;
    	} 
    	
    	Log.v(TAG, "Setting up FacebookFragment header");
    	
    	MyBitmapFactory bitmapFactory = new MyBitmapFactory(mContext);
    	Bitmap headerBitmap = bitmapFactory.decodeResource(R.drawable.facebook_logo);
    	
    	mFacebookHeader.setImageBitmap(headerBitmap);
    }
    
    private Intent getFacebookUserIntent() {
    	Log.v(TAG, "Getting a new Facebook UserIntent");
        try {
            mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0); 
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + mFacebookProfileId)); 
            return facebookIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating Facebook UserIntent");
        }
        
        return null;
    }
    
    private Intent getWebBrowserUserIntent() {
    	Log.v(TAG, "Getting a new Facebook web brwoser Intent");
        try {
        	Intent webBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + mFacebookUsername));
        	return webBrowserIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating Facebook web browser Intent");
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
    	Log.v(TAG, "Cleaning up after FacebookFragment");
        mContext = null;
        
        mFacebookUsername = null;
        mFacebookProfileId = null;
        
        mFacebookButton = null;
        mWebBrowserButton = null;
        
        mFacebookHeader.clearAnimation();
        mFacebookHeader = null;
    }
}