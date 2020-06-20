package com.fitnh.mobile;

import com.fitnh.mobile.R;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GenericFragment extends Fragment {
	private final String TAG = "FIT | GenericFragment";
	private Context mContext;
	
    private String mFragmentTitle;
    
    public GenericFragment() {
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
    }
    
    private void createTitle(final Resources resources) {
    	Log.v(TAG, "Creating action bar title for GenericFragment");
    	StringBuilder titleBuilder = new StringBuilder(50);
        titleBuilder.append(getResources().getString(R.string.app_name));
        
        mFragmentTitle = titleBuilder.toString();
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
        View rootView = layoutInflater.inflate(R.layout.fragment_generic, container, false);        
        
        Log.w(TAG, "GenericFragment is meant to be used for prototyping only");
        
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
    
    @Override 
    public void onDestroy() {
    	Log.v(TAG, "onDestroy()");
    	cleanupFragment();
    	
    	Log.v(TAG, "Calling super.onDestroy()");
        super.onDestroy();
    }
    
    private void cleanupFragment() {
    	Log.v(TAG, "Cleaning up after GenericFragment");
    	mContext = null;
    	mFragmentTitle = null;
    }
}