package com.fitnh.mobile;

import com.fitnh.mobile.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebpageFragment extends Fragment {
	private final String TAG = "FIT | WebpageFragment";
    public final String ARG_WEBPAGE_NUMBER = "webpage_number";
    private final int mViewAnimationDuration = 400;
    
    private Context mContext;
    
    private String mFragmentTitle;
    
    private WebView mWebView;
    private View mLoadingView;
    
    public WebpageFragment() {
    	Log.v(TAG, "Default constructor call");
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate()");
    }
    
    private void createTitle(final Resources resources) { 
    	Log.v(TAG, "Creating action bar title for WebpageFragment");
    	final int fragmentIndex = getArguments().getInt(ARG_WEBPAGE_NUMBER);
    	
    	StringBuilder titleBuilder = new StringBuilder(50);
        titleBuilder.append(resources.getString(R.string.app_name));
        titleBuilder.append(" | ");
        titleBuilder.append(resources.getStringArray(R.array.drawer_online_child_titles)[fragmentIndex]);
        
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
    	Log.v(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_webpage, container, false);              
        
        mContext = getActivity();
        setCustomOverscrollColor(mContext);
        
        Resources resources = getResources();
       	createTitle(resources);
    	
        mLoadingView = rootView.findViewById(R.id.loading_spinner);
        
        mWebView = (WebView) rootView.findViewById(R.id.webview);  
        int fragmentIndex = getArguments().getInt(ARG_WEBPAGE_NUMBER);
        setupWebView(fragmentIndex);
        
        ((MainActivity) mContext).getThreadUtilities().cleanupEnvironment();
        return rootView;
    }
    
    @SuppressLint("SetJavaScriptEnabled")
	private void setupWebView(final int fragmentIndex) {
    	Log.v(TAG, "Setting up WebView");
        
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.setWebViewClient(new WebViewClient() {
        	   public void onPageFinished(WebView webView, String url) {
        		   Log.v(TAG, "WebView page finished (url: " + url);
        	   }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
            	if(mLoadingView == null) {
            		Log.w(TAG, "WebChromeClient | onProgressChanged was called but LoadingView was null");
            		Log.w(TAG, "Unable to update progress bar");
            		
            		return;
            	}
            	
            	if(progress < 100 && mLoadingView.getVisibility() == ProgressBar.GONE) {
            		Log.v(TAG, "Loading progress incomplete; showing progress bar");
            		((MainActivity) mContext).getAnimationManager().fadeViewIn(mLoadingView, mViewAnimationDuration);
            	}
            
            	Log.v(TAG, "Updating progress bar; new progress value: " + progress);
            	((ProgressBar) mLoadingView).setProgress(progress);
            
            	if(progress == 100) {
            		Log.v(TAG, "Done loading page; hiding progress bar");
            		((MainActivity) mContext).getAnimationManager().fadeViewOutAndHide(mLoadingView, mViewAnimationDuration);
            	}
            }
        });
        
        if(mWebView.getSettings().supportZoom()) {
        	mWebView.getSettings().setBuiltInZoomControls(true);
        	mWebView.getSettings().setDisplayZoomControls(false);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean javascriptEnabled = sharedPreferences.getBoolean("javascript_enabled", true);
        
        if(javascriptEnabled) {
        	Log.w(TAG, "JavaScript is being enabled; this may introduce XSS vulnerabilities");
        	Log.w(TAG, "XSS warings are being suppressed with @SuppressLint(\"SetJavaScriptEnabled\")");
        	
        	mWebView.getSettings().setJavaScriptEnabled(true);  
        }

        if(isNetworkConnected()) {
        	String webpage = getResources().getStringArray(R.array.online_page_urls)[fragmentIndex];
        	mWebView.loadUrl(webpage);
        } else {
        	Log.v(TAG, "No active network connection detected");
        	Log.v(TAG, "Loading static webpage from assets");
        	
        	String webpage = getResources().getStringArray(R.array.static_page_urls)[fragmentIndex];            	
        	mWebView.loadUrl(webpage);
        }
    }
    
    private void setNewActionBarTitle() {
    	Log.v(TAG, "Setting a new action bar title");
    	if(mContext == null || mFragmentTitle == null) {
    		return;
    	}
    	
    	((MainActivity) mContext).setTitle(mFragmentTitle);
    }
    
    private boolean isNetworkConnected() {
    	Log.v(TAG, "Checking for active network connection");
    	if(mContext == null) {
    		return false;
    	}
    	
    	ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);	
    	return (connectivityManager.getActiveNetworkInfo() != null);
    }
    
    @Override 
    public void onDestroy() {
    	Log.v(TAG, "onDestroy()");
    	cleanupFragment();
    	
    	Log.v(TAG, "Calling super.onDestroy()");
        super.onDestroy();
    }
    
    private void cleanupFragment() {
    	Log.v(TAG, "Cleaning up after WebpageFragment");
    	mWebView.stopLoading();
    	mWebView.clearAnimation();
        mWebView.clearCache(true);
        mWebView.clearView();
        mWebView.freeMemory();
        mWebView.destroy();
        
        mLoadingView.clearAnimation();
        mLoadingView = null;
        
        mContext = null;
        mFragmentTitle = null;
    }
}