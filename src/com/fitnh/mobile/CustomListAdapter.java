package com.fitnh.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class CustomListAdapter extends BaseExpandableListAdapter {
	private final String TAG = "FIT | CustomListAdapter";
	private final ExpandableListView mDrawerList;
	
	private final Activity mActivity;
	private final LayoutInflater mLayoutInflater;
	private final TextView mEmptyView;
	
	private final ArrayList<String> mGroupList; 
	private final ArrayList<Object> mChildMasterList;
	private ArrayList<String> mChildList;

	private int mLastExpandedGroupPosition;

	public CustomListAdapter(Context context, final ExpandableListView drawerList, 
			ArrayList<String> groupList, ArrayList<Object> childMasterList) {
		Log.v(TAG, "Constructor call CustomListAdapter(Context, final ExpandableListView, " + 
			"ArrayList<String>, ArrayList<Object>)");
		
		mDrawerList = drawerList;
		mEmptyView = new TextView(context);
		
		if(context instanceof MainActivity) {
			mActivity = ((MainActivity) context);
			mLayoutInflater = mActivity.getLayoutInflater();
		} else {
			Log.w(TAG, "Unexpected Context, expected instanceof MainActivity");
			
			mActivity = null;
			mLayoutInflater = null;
		}
		
		mGroupList = groupList;
		mChildMasterList = childMasterList;
		
		setCustomOverscrollColor(context);
		accountForEmptyDrawerGroups();
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
	
	private void accountForEmptyDrawerGroups() {
		Log.v(TAG, "accountForEmptyDrawerGroups()");
		ArrayList<Object> emptyList = new ArrayList<Object>();
		
		while(mChildMasterList.size() < mGroupList.size()) {
			mChildMasterList.add(emptyList);
		}
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Log.v(TAG, "getChild()");
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Log.v(TAG, "getChildId()");
		return 0;
	}

	//@SuppressWarnings("unchecked")
	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if(mActivity == null || mLayoutInflater == null) {
			Log.w(TAG, "getChildView() called but Activity or LayoutInflater was null");
			Log.w(TAG, "Returning EmptyView");
			
			return mEmptyView;
		}
		
		//Log.v(TAG, "getChildView() with @SuppressWarnings(\"unchecked\")");
		Log.v(TAG, "getChildView() without @SuppressWarnings(\"unchecked\")"); 
		
		if(convertView == null) {
			convertView = (TextView) mLayoutInflater.inflate(R.layout.drawer_list_child_item, null);//textView2; 
		}
		
		final TextView textView = (TextView) convertView;
		
		mChildList = (ArrayList<String>) mChildMasterList.get(groupPosition);
		
		StringBuilder childViewLabelBuilder = new StringBuilder(30);
		childViewLabelBuilder.append(mActivity.getResources().getString(R.string.drawer_list_bullet_character));
		childViewLabelBuilder.append(" ");
		childViewLabelBuilder.append(mChildList.get(childPosition));
		
		textView.setText(childViewLabelBuilder.toString());
		convertView.setTag(mChildList.get(childPosition));
        
		return convertView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getChildrenCount(int groupPosition) {
		Log.v(TAG, "getChildrenCount() with @SuppressWarnings(\"unchecked\")");
		return ((ArrayList<String>) mChildMasterList.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		Log.v(TAG, "getGroup()");
		return null;
	}

	@Override
	public int getGroupCount() {
		Log.v(TAG, "getGroupCount()");
		
		if(mGroupList == null) {
			return 0;
		} else {
			return mGroupList.size();
		}
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		Log.v(TAG, "onGroupCollapsed()");
		
		Log.v(TAG, "Calling super.onGroupCollapsed()");
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		Log.v(TAG, "onGroupExpanded()");
        if(groupPosition != mLastExpandedGroupPosition) {
            mDrawerList.collapseGroup(mLastExpandedGroupPosition);
        }

        mDrawerList.setItemChecked(groupPosition, true);
        mLastExpandedGroupPosition = groupPosition;
        
        Log.v(TAG, "Calling super.onGroupExpanded()");
        super.onGroupExpanded(groupPosition);   
	}

	@Override
	public long getGroupId(int groupPosition) {
		Log.v(TAG, "getGroupId()");
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if(mActivity == null || mLayoutInflater == null) {
			Log.w(TAG, "getGroupView() called but Activity or LayoutInflater was null");
			Log.w(TAG, "Returning EmptyView");
			
			return mEmptyView;
		}
		
		Log.v(TAG, "getGroupView()");
		
		if(convertView == null) {
			convertView = (TextView) mLayoutInflater.inflate(R.layout.drawer_list_group_item, null);
		}
		
		String groupViewLabel = mGroupList.get(groupPosition);
		((TextView) convertView).setText(groupViewLabel);
		convertView.setTag(groupViewLabel);	
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		Log.v(TAG, "hasStableIds()");
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		Log.v(TAG, "isChildSelectable()");
		return true;
	}
}
