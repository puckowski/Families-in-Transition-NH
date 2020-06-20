package com.fitnh.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class MyPreferenceFragment extends PreferenceFragment
	implements OnSharedPreferenceChangeListener {
	private final String TAG = "FIT | MyPreferenceFragment";
	public final String FRAGMENT_INDEX = "fragment_index";
	
	private Context mContext;
	
	private String mFragmentTitle;
		
    public MyPreferenceFragment() {
    	Log.v(TAG, "Calling default constructor now");
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preferenceKey) {
    	Log.v(TAG, "onSharedPreferenceChanged()");
    	
    	if(preferenceKey.equals("use_desktop_site")) {
    		Log.v(TAG, "Calling changeDrawerListAdapter() from MyPreferenceFragment");
    		((MainActivity) mContext).changeDrawerListAdapter();
    	}
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate()");
        addPreferencesFromResource(R.layout.fragment_settings); 
        
        mContext = getActivity();
        
        Resources resources = getResources();
        createTitle(resources);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        setCustomOverscrollColor(mContext);
        ((MainActivity) mContext).getThreadUtilities().cleanupEnvironment();
    }
    
    private void createTitle(final Resources resources) {
    	Log.v(TAG, "Creating action bar title for MyPreferenceFragment");
    	
    	mFragmentTitle = "Families in Transition | Settings";
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
    	Log.v(TAG, "Cleaning up after MyPreferenceFragment");
    	mContext = null;
    	mFragmentTitle = null;
    }
}