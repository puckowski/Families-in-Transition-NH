package com.fitnh.mobile;

import com.fitnh.mobile.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationManager {
	private final String TAG = "FIT | AnimationManager";
	
	public final int RIGHT_TO_LEFT = 0;
	public final int RIGHT_TO_LEFT_OUT = 1;
	public final int FADE_IN = 2;
	public final int FADE_OUT = 3;
	
	private final int[] mAnimationResources = {
		R.anim.right_to_left,
		R.anim.right_to_left_out,
		R.anim.fade_in,
		R.anim.fade_out
	};
	
	private Context mContext;
	
	public AnimationManager(final Context context) {
		Log.v(TAG, "Calling constructor AnimationManager(Context)");
		mContext = context;
	}
	
	public void cleanup() {
		Log.v(TAG, "Cleaning up after AnimationManager");
		
		mContext = null;
	}
	
	public Animation getAnimation(final int animationResource) {
		if(mContext == null) {
			Log.w(TAG, "getAnimation() called but Context was null");
			Log.w(TAG, "Returning a null value");
			
			return null;
		} else if(animationResource < 0 || animationResource > mAnimationResources.length) {
			Log.w(TAG, "getAnimation() called but animationResource was out of bounds");
			Log.w(TAG, "Returning a null value");
			
			return null;
		}
		
		Log.v(TAG, "getAnimation()");
		return (Animation) AnimationUtils.loadAnimation(mContext, mAnimationResources[animationResource]);
	}
	
	public void fadeViewIn(final View view, final int animationDuration) {
		Log.v(TAG, "fadeViewIn() for " + String.valueOf(animationDuration) + " milliseconds");
		
		((MainActivity) mContext).getHandler().post(new Runnable() {
			@Override
			public void run() {
				view.animate()
	        		.alpha(1f)
	        		.setDuration(animationDuration)
	        		.setListener(new AnimatorListenerAdapter() {
	        			@Override
	        			public void onAnimationEnd(Animator animation) {
	        				if(view != null) {
	        					view.setVisibility(View.VISIBLE);
	        				}
	        			}
	        		});
			}
		});
	}
	
	public void fadeViewOutAndHide(final View view, final int animationDuration) {
		Log.v(TAG, "fadeViewOutAndHide() for " + String.valueOf(animationDuration) + " milliseconds");
		
		((MainActivity) mContext).getHandler().post(new Runnable() {
			@Override
			public void run() {
				view.animate()
	        		.alpha(0f)
	        		.setDuration(animationDuration)
	        		.setListener(new AnimatorListenerAdapter() {
	        			@Override
	        			public void onAnimationEnd(Animator animation) {
	        				if(view != null) {
	        					view.setVisibility(View.GONE);
	        				}
	        			}
	        		});
			}
		});
	}
}
