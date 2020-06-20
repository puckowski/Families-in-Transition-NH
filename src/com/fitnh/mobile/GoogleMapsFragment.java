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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class GoogleMapsFragment extends Fragment {
	private final String TAG = "FIT | GoogleMapsFragment";
	public final String FRAGMENT_INDEX = "fragment_index";
	private final int LOCATION_FLIP_INTERVAL = 5000;
	
    private Context mContext;
    
    private String mFragmentTitle;
    
    private Button mOutfittersInfoButton;
    private Button mOutfittersThriftStoreButton;
	private Button mOutfittersBoutiqueButton;
	
	private ScrollView mScrollView;
	private ViewFlipper mLocationViewFlipper;
	private SetupViewFlipperTask mSetupViewFlipperTask;
	
    int[] mLocationResources = { R.drawable.outfitters_thrift_store,
    		R.drawable.outfitters_boutique 
    };
	
    public GoogleMapsFragment() {
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
    }
    
    private void createTitle(final Resources resources) { 
    	Log.v(TAG, "Creating action bar title for GoogleMapsFragment");
    	/*final int fragmentIndex = getArguments().getInt(FRAGMENT_INDEX);
    	
    	StringBuilder titleBuilder = new StringBuilder(50);
        titleBuilder.append(resources.getString(R.string.app_name));
        titleBuilder.append(" | ");
        titleBuilder.append(resources.getStringArray(R.array.drawer_about_child_titles)[fragmentIndex]);
        
        mFragmentTitle = titleBuilder.toString(); 
        setNewActionBarTitle();*/
    	
    	mFragmentTitle = "Families in Transition | Locations";
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
        final View rootView = layoutInflater.inflate(R.layout.fragment_google_maps, container, false);        
        
        mScrollView = (ScrollView) rootView.findViewById(R.id.mainScrollView);
        mLocationViewFlipper = (ViewFlipper) rootView.findViewById(R.id.location_view_flipper);

        mSetupViewFlipperTask = new SetupViewFlipperTask();
    	mSetupViewFlipperTask.execute();
    	
        TextView infoView = (TextView) rootView.findViewById(R.id.outfitters_thrift_store_description);
        infoView.setVisibility(View.GONE);
        
        mOutfittersInfoButton = (Button) rootView.findViewById(R.id.outfitters_info_button);
        mOutfittersInfoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	TextView infoView = (TextView) rootView.findViewById(R.id.outfitters_thrift_store_description);
            	
            	if(infoView.getText().length() == 0) {
            		infoView.setText(R.string.outfitters_thrift_store_description);
            		AnimationManager animationManager = new AnimationManager(mContext);
            		infoView.setAnimation(animationManager.getAnimation(animationManager.FADE_IN));
            		infoView.setVisibility(View.VISIBLE);
            		
            		mOutfittersInfoButton.setText(R.string.outfitters_collapse_info_label);
            		focusOnView(mOutfittersInfoButton);
            	} else {
            		infoView.setText(null);
            		infoView.setVisibility(View.GONE);
            		
            		mOutfittersInfoButton.setText(R.string.outfitters_expand_info_label);
            		focusOnView(mOutfittersInfoButton);
            	}
            }
        });
        
        mOutfittersThriftStoreButton = (Button) rootView.findViewById(R.id.outfitters_thrift_store_launch);
        mOutfittersThriftStoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Resources resources = getResources();
            	Intent googleMapsIntent = getGoogleMapsIntentForAddress(resources.getString(R.string.outfitters_thrift_store_address));
            	
            	if(googleMapsIntent != null) {
            		try {
            			Toast.makeText(mContext, resources.getString(R.string.launching_outfitters_thrift_message), Toast.LENGTH_SHORT).show();
            			startActivity(googleMapsIntent);
            		} catch(ActivityNotFoundException activityNotFoundException) {
            			Log.e(TAG, "Error in launching GoogleMaps LocationIntent");
            			Log.e(TAG, "Perhaps the Google Maps application is not installed on device?");
            		}
            	}
            }
        });
        
        mOutfittersBoutiqueButton = (Button) rootView.findViewById(R.id.outfitters_thrift_store_boutique_launch);
        mOutfittersBoutiqueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Resources resources = getResources();
            	Intent googleMapsIntent = getGoogleMapsIntentForAddress(resources.getString(R.string.outiftters_thrift_store_boutique_address));
            	
            	if(googleMapsIntent != null) {
            		try {
            			Toast.makeText(mContext, resources.getString(R.string.launching_outfitters_boutique_message), Toast.LENGTH_SHORT).show();
            			startActivity(googleMapsIntent);
            		} catch(ActivityNotFoundException activityNotFoundException) {
            			Log.e(TAG, "Error in launching GoogleMaps LocationIntent");
            			Log.e(TAG, "Perhaps the Google Maps application is not installed on device?");
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
    
    private void setupLocationViewFlipper() { 
    	if(mLocationViewFlipper == null) {
    		Log.w(TAG, "setupLocationViewFlipper() called but LocationViewFlipper was null");
    		Log.w(TAG, "Did not setup LocationViewFlipper");
    		
    		return;
    	} else if(mContext == null) {
    		Log.w(TAG, "setupLocationViewFlipper() called but Context was null");
    		Log.w(TAG, "Did not setup LocationViewFlipper");
    		
    		return;
    	}
    	
    	Log.v(TAG, "Setting up LocationViewFlipper");
    	
    	Log.v(TAG, "Getting MainActivity's MyBitmapFactory for use");
    	MyBitmapFactory bitmapFactory = ((MainActivity) mContext).getBitmapFactory();
    	
    	Log.v(TAG, "Getting MainActivity's AnimationManager for use");
    	AnimationManager animationManager = ((MainActivity) mContext).getAnimationManager();
    	
    	for(int i = 0; i < mLocationResources.length; i++) {
            ImageView imageView = new ImageView(mContext);
            
            imageView.setAdjustViewBounds(true);
            Log.d(TAG, "ImageView | setScaleType(ScaleType.CENTER_INSIDE)");
            imageView.setScaleType(ScaleType.CENTER_INSIDE); 
            
    		Bitmap locationBitmap = bitmapFactory.decodeResource(mLocationResources[i]);			
            imageView.setImageBitmap(locationBitmap); 
            mLocationViewFlipper.addView(imageView);
            
            if(i > 0) {
            	continue;
            } else {
            	Log.v(TAG, "Fading in first ImageView added to ViewFlipper");
            	imageView.setAlpha(0f);
            	animationManager.fadeViewIn(imageView, 1000);
            }
        }
        
    	mLocationViewFlipper.setInAnimation(mContext, R.anim.right_to_left);
    	mLocationViewFlipper.setOutAnimation(mContext, R.anim.right_to_left_out);
    	
    	mLocationViewFlipper.setFlipInterval(LOCATION_FLIP_INTERVAL);
        mLocationViewFlipper.setAutoStart(true);
        
        Log.v(TAG, "ImageView starting to cycle through location resources");
        mLocationViewFlipper.startFlipping();
    }
    
    private class SetupViewFlipperTask extends AsyncTask<Void, Void, Void> {
	    @Override
	    protected void onPostExecute(Void nothingToSeeHere) {
	    	Log.v(TAG, "SetupViewFlipperTask | onPostExecute()");
	    	
	    	Log.v(TAG, "Attempting to set Thread priority to THREAD_PRIORITY_MORE_FAVORABLE; " +
					"setting up our ViewFlipper is resource intensive");
	        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
	        	
	    	setupLocationViewFlipper();
	    }

		@Override
		protected Void doInBackground(Void... params) {
			Log.v(TAG, "SetupViewFlipperTask | doInBackground()");
			
			try {
				Log.d(TAG, "Delaying call to onPostExecute() by 200 milliseconds to give the rest " +
					"of the GoogleMapsFragment time to render");
				Thread.sleep(200);
			} catch(InterruptedException interruptedException) {
				Log.w(TAG, "Non-critical InterruptedException occurred in SetupViewFlipperTask");
			}
			
			return null;
		}
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
    
    private Intent getGoogleMapsIntentForAddress(String unencodedAddress) {
    	Log.v(TAG, "Getting a new GoogleMaps LocationIntent");
        try {
        	String encodedAddress = Uri.encode(unencodedAddress);
        	
        	Intent googleMapsIntent = new Intent(android.content.Intent.ACTION_VIEW, 
        			Uri.parse("geo:0,0?q=" + encodedAddress));
            return googleMapsIntent;
        } catch(Exception exception) {
        	Log.e(TAG, "Error in creating GoogleMaps LocationIntent");
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
    	Log.v(TAG, "Cleaning up after GoogleMapsFragment");
    	if(mSetupViewFlipperTask != null) {
    		mSetupViewFlipperTask.cancel(true);
    		mSetupViewFlipperTask = null;
			Log.d(TAG, "SetupViewFlipperTask cancelled successfully: " + (mSetupViewFlipperTask == null ? "true" : "false"));
		} else {
			Log.d(TAG, "SetupViewFlipperTask was null");
		}
    	
    	mContext = null;
        mFragmentTitle = null;
        
        mOutfittersInfoButton = null;
        mOutfittersThriftStoreButton = null;
    	mOutfittersBoutiqueButton = null;
    	
    	mScrollView = null;
    	
    	mLocationViewFlipper.clearAnimation();
    	mLocationViewFlipper.removeAllViews();
    	mLocationViewFlipper = null;
    }
}