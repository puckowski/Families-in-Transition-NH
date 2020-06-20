package com.fitnh.mobile;

import java.util.List;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

public class YouTubeFragment extends Fragment {
	private final String TAG = "FIT | YouTubeFragment";
    public final String FRAGMENT_INDEX = "fragment_index";
    
    private Context mContext;
    
    private String mFragmentTitle;
    private String mYouTubeUsername;
    
    private ImageView mYouTubeHeader;
    private Button mYouTubeButton;
    private Button mWebBrowserButton;
    private ImageButton mPlayVideoButton;
    
    private int mSavedScreenOrientation;
    
    public YouTubeFragment() {
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
    	setCustomOverscrollColor(mContext);
        mYouTubeUsername = resources.getString(R.string.youtube_username);
    }
    
    private void createTitle(final Resources resources) { 
    	Log.v(TAG, "Creating action bar title for YouTubeFragment");
    	
    	mFragmentTitle = "Families in Transition | YouTube";
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
        View rootView = layoutInflater.inflate(R.layout.fragment_youtube, container, false);        
        
        mYouTubeHeader = (ImageView) rootView.findViewById(R.id.youtube_header);
        setupYouTubeHeader();
        
        mYouTubeButton = (Button) rootView.findViewById(R.id.youtube_launch);
        mYouTubeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent youTubeIntent = getYouTubeUserIntent();
            	
            	if(youTubeIntent != null) {
            		try {
            			Toast.makeText(mContext, getResources().getString(R.string.launching_youtube_message), Toast.LENGTH_SHORT).show();
            			startActivity(youTubeIntent);
            		} catch(ActivityNotFoundException activityNotFoundException) {
            			Log.e(TAG, "Error in launching YouTube UserIntent");
            			Log.e(TAG, "Perhaps the YouTube application is not installed on device?");
            			
            			Toast.makeText(mContext, "The YouTube mobile app does not seem to be installed", Toast.LENGTH_LONG).show();
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
            			Toast.makeText(mContext, getResources().getString(R.string.launching_youtube_in_browser_message), Toast.LENGTH_SHORT).show();
            			startActivity(webBrowserIntent);
            		} catch(Exception exception) {
            			Log.e(TAG, "Error in launching YouTube web browser Intent");
            			Log.e(TAG, "Perhaps there isn't a web browser installed on device?");
            		}
            	}
            }
        });
        
        mPlayVideoButton = (ImageButton) rootView.findViewById(R.id.play_fit_video_button);
        mPlayVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	mSavedScreenOrientation = mContext.getResources().getConfiguration().orientation;
            	Intent playVideoIntent = null;
            	
            	if (view == mPlayVideoButton) {
        			playVideoIntent = YouTubeStandalonePlayer.createVideoIntent(((MainActivity) mContext),
        					DeveloperKey.DEVELOPER_KEY, mContext.getString(R.string.youtube_about_video_id), 
        						0, false, false);
        		}

        		if (playVideoIntent != null) {
        			if (canResolveIntent(playVideoIntent) == true) {
        				startActivityForResult(playVideoIntent, 
        						mContext.getResources().getInteger(R.integer.start_standalone_player));
        			} else {
        				YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog(
        						((MainActivity) mContext), 
        							mContext.getResources().getInteger(R.integer.resolve_service_missing)).show();
        			}
        		}
            }
        });
        
        ((MainActivity) mContext).getThreadUtilities().cleanupEnvironment();
        return rootView;
    }
    
    public void reinstateScreenOrientation() {
    	if(mContext == null) {
    		Log.w(TAG, "reinstateScreenOrientation() called but mContext was null");
    		Log.w(TAG, "Unable to reinstate saved screen orientation");
    		
    		return;
    	}
    	
    	Log.v(TAG, "reinstateScreenOrientation() called");
    	
    	final int currentScreenOrientation = mContext.getResources().getConfiguration().orientation;
    	
    	if(currentScreenOrientation != mSavedScreenOrientation) {
    		Log.v(TAG, "Screen oorientation has been changed");
    		
    		if(mSavedScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
    			Log.v(TAG, "Resetting screen orientation to Configuration.ORIENTATION_LANDSCAPE");
    			((MainActivity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    		} else if(mSavedScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
    			Log.v(TAG, "Resetting screen orientation to Configuration.ORIENTATION_PORTRAIT");
    			((MainActivity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    		}
    	}
    }
    
    private void setNewActionBarTitle() {
    	Log.v(TAG, "Setting new action bar title");
    	if(mContext == null || mFragmentTitle == null) {
    		return;
    	}
    	
    	((MainActivity) mContext).setTitle(mFragmentTitle);
    }
    
    private boolean canResolveIntent(Intent intent) {
    	if(intent == null) {
    		Log.w(TAG, "canResolveIntent() called but intent was null");
    		Log.w(TAG, "Could not resolve intent");
    		
    		return false;
    	} else if(mContext == null) {
    		Log.w(TAG, "canResolveIntent() called but mContext was null");
    		Log.w(TAG, "Could not resolve intent");
    		
    		return false;
    	}
    	
    	Log.v(TAG, "canResolveIntent() called");
    	
		List<ResolveInfo> resolveInfo = mContext.getPackageManager().queryIntentActivities(intent, 0);
		return (resolveInfo != null && !resolveInfo.isEmpty());
	}
    
    private void setupYouTubeHeader() {
    	if(mContext == null) {
    		Log.w(TAG, "setupYouTubeHeader() called but Context was null");
    		Log.w(TAG, "YouTubeFragment header was not setup");
    		
    		return;
    	} 
    	
    	Log.v(TAG, "Setting up YouTubeFragment header");
    	
    	Bitmap headerBitmap = ((MainActivity) mContext).getBitmapFactory().decodeResource(R.drawable.youtube_logo);
    	mYouTubeHeader.setImageBitmap(headerBitmap);
    }
    
    private Intent getYouTubeUserIntent() {
    	Log.v(TAG, "Getting a new YouTube UserIntent");
        try {
            Intent youTubePlayerIntent = com.google.android.youtube.player.YouTubeIntents.createUserIntent(mContext, mYouTubeUsername);
            return youTubePlayerIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating YouTube UserIntent");
        }
        
        return null;
    }
    
    private Intent getWebBrowserUserIntent() {
    	Log.v(TAG, "Getting a new YouTube web browser Intent");
        try {
        	Intent webBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/" + mYouTubeUsername));
        	return webBrowserIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating YouTube web browser Intent");
        	Log.e(TAG, "Perhaps there is no web browser installed on device?");
        }
        
        return null;
    }
    
    @Override
    public void onResume() {
    	Log.v(TAG, "onResume()");
    	reinstateScreenOrientation();
    	
    	Log.v(TAG, "Calling super.onResume()");
    	super.onPause();
    }
    
    @Override 
    public void onPause() {
    	Log.v(TAG, "onPause()");
    	
    	Log.v(TAG, "Calling super.onPause()");
    	super.onPause();
    }
    
    @Override
    public void onStop() {
    	Log.v(TAG, "onStop()");
    	
    	Log.v(TAG, "Calling super.onStop()");
    	super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	Log.v(TAG, "onDestroy()");
    	cleanupFragment();
    	
    	Log.v(TAG, "Calling super.onDestroy()");
    	super.onDestroy();
    }
    
    private void cleanupFragment() {
    	Log.v(TAG, "Cleaning up after YouTubeFragment");
    	mContext = null;
        
    	mFragmentTitle = null;
        mYouTubeUsername = null;
        
        mYouTubeButton = null;
        mWebBrowserButton = null; 
        mPlayVideoButton = null;
    }
}