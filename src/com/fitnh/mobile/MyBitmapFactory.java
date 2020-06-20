package com.fitnh.mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class MyBitmapFactory {
	private final String TAG = "FIT | MyBitmapFactory";
	
	private Context mContext;
	
	public final int HEAP_SIZE_LEVEL_16 = 0;
	public final int HEAP_SIZE_LEVEL_32 = 1;
	public final int HEAP_SIZE_LEVEL_64 = 2;
	
	private final int[] mHeapSizeArray = {
		16,
		32,
		64
	};
	
	private final int[] mPreferredWidths = {
		800,
		1024,
		1280
	};
	
	private final int[] mPreferredHeights = {
		480,
		640,
		800
	};
	
	private int mHeapSizeLevel;
	
	public MyBitmapFactory(Context context) {
		Log.v(TAG, "Calling constructor MyBitmapFactory(Context)");
		mContext = context;
		
		mHeapSizeLevel = HEAP_SIZE_LEVEL_16;
		determineHeapSizeLevel();
	}
	
	public void cleanup() {
		Log.v(TAG, "Cleaning up after MyBitmapFactory");
		
		mContext = null;
	}
	
	private void determineHeapSizeLevel() {
		Log.v(TAG, "Determining heap size level");
		
		double maximumHeapSize = Runtime.getRuntime().maxMemory();
		Log.d(TAG, "Maximum heap size in bytes: " + String.valueOf(maximumHeapSize));
		double maximumHeapSizeInKilobytes = (maximumHeapSize / 1024);
		Log.d(TAG, "Maximum heap size in kilobytes: " + String.valueOf(maximumHeapSizeInKilobytes));
		double maximumHeapSizeInMegabytes = (maximumHeapSizeInKilobytes / 1024);
		Log.d(TAG, "Maximum heap size in megabytes: " + String.valueOf(maximumHeapSizeInMegabytes));
		
		Log.d(TAG, "Current heap size level: " + String.valueOf(mHeapSizeLevel));
		int index = 0;
		while(index < mHeapSizeArray.length && maximumHeapSizeInMegabytes > mHeapSizeArray[index]) {
			index++;
			
			mHeapSizeLevel++;
			Log.d(TAG, "New heap size level: " + String.valueOf(mHeapSizeLevel));
		}
	}
	
	public int getHeapSizeLevel() {
		Log.v(TAG, "getHeapSizeLevel()");
		return mHeapSizeLevel;
	}
	
	private int calculateSampleSize(BitmapFactory.Options bitmapFactoryOptions, int preferredWidth, int preferredHeight) {
		Log.v(TAG, "Calculating sample size with BitmapFactory.Options");
		final int height = bitmapFactoryOptions.outHeight;
		final int width = bitmapFactoryOptions.outWidth;
		
        int inSampleSize = 1;
        Log.d(TAG, "Current sample size: " + String.valueOf(inSampleSize));
        
        if(height > preferredHeight || width > preferredWidth) {
        	final int halfHeight = (height / 2);
        	final int halfWidth = (width / 2);

        	while((halfHeight / inSampleSize) > preferredHeight
                && (halfWidth / inSampleSize) > preferredWidth) {
        		inSampleSize = (inSampleSize * 2);
        		Log.d(TAG, "New sample size: " + String.valueOf(inSampleSize));
        	}
        }

        return inSampleSize;
	}
	
	public Bitmap decodeResource(final int resourceId) {
		Log.v(TAG, "decodeResource()");
		Log.w(TAG, "Not checking whether or not resourceId parameter is valid");
		
		BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
		bitmapFactoryOptions.inJustDecodeBounds = true;
			
		BitmapFactory.decodeResource(mContext.getResources(), resourceId, bitmapFactoryOptions);
				
		bitmapFactoryOptions.inSampleSize = calculateSampleSize(bitmapFactoryOptions, 
			mPreferredWidths[mHeapSizeLevel], mPreferredHeights[mHeapSizeLevel]);
		bitmapFactoryOptions.inJustDecodeBounds = false;
		
		bitmapFactoryOptions.inDither = true; 
		Log.d(TAG, "BitmapFactory.Options: inDither = true");
		bitmapFactoryOptions.inPurgeable = true;
		Log.d(TAG, "BitmapFactory.Options: inPurgeable = true");
		bitmapFactoryOptions.inInputShareable = true; 
		Log.d(TAG, "BitmapFactory.Options: inInputShareable = true");
		
		bitmapFactoryOptions.inPreferredConfig = Config.RGB_565; 
		Log.d(TAG, "BitmapFactory.Options: inPreferredCongif = Config.RGB_565");
		
		return BitmapFactory.decodeResource(mContext.getResources(), resourceId, bitmapFactoryOptions);
	}
}
