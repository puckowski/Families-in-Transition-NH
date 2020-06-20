package com.fitnh.mobile;

import static com.fitnh.mobile.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.fitnh.mobile.CommonUtilities.EXTRA_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnChildClickListener {
	private final String TAG = "FIT | MainActivity";
	
	private final int GROUP_INVOLVED_POSITION = 0;
	private final int GROUP_ONLINE_POSITION = 1;
	//private final int GROUP_ABOUT_POSITION = 2;
	private final int GROUP_BUSINESSES_POSITION = 2;
	private final int GROUP_SETTINGS_POSITION = 3;
	
	private final int INVOLVED_GIVING_POSITION = 0;
	private final int INVOLVED_EVENTS_POSITION = 1;
	private final int INVOLVED_VOLUNTEER_POSITION = 2;
	private final int INVOLVED_MESSAGES_POSITION = 3;
	private final int INVOLVED_REGISTER_POSITION = 4;
	
	private final int INVOLVED_MOBILE_DONATE_POSITION = 0;
	private final int INVOLVED_MOBILE_VOLUNTEER_POSITION = 1;
	private final int INVOLVED_MOBILE_GIVING_POSITION = 2;
	private final int INVOLVED_MOBILE_EVENTS_POSITION = 3;
	private final int INVOLVED_MOBILE_ABOUT_POSITION = 4;
	private final int INVOLVED_MOBILE_MESSAGES_POSITION = 5;
	private final int INVOLVED_MOBILE_REGISTER_POSITION = 6;
	
	//private final int ABOUT_LOCATION_POSITION = 0;
	//private final int ABOUT_FACEBOOK_POSITION = 1;
	//private final int ABOUT_YOUTUBE_POSITION = 2;
	//private final int ABOUT_TWITTER_POSITION = 3;
	
	private final Handler mHandler = new Handler();
	private final ThreadUtilities mThreadUtilities = new ThreadUtilities();
	private final MyBitmapFactory mBitmapFactory = new MyBitmapFactory(this);
	private final AnimationManager mAnimationManager = new AnimationManager(this);
	private final MyNotificationBuilder mNotificationBuilder = new MyNotificationBuilder(this);
	
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerList;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private int[] mCurrentFragmentPosition;

    private ArrayList<String> mPushMessageArray;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");  
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        
        mTitle = mDrawerTitle = getTitle();
        mCurrentFragmentPosition = new int[] { 1, 0 };
        
        mPushMessageArray = new ArrayList<String>();
        registerReceiver(mPushMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        
        buildNavigationDrawer();       
        if(savedInstanceState == null) {
        	displayWebpageFragment(0);
        } 
    }
    
    private void buildNavigationDrawer() {
    	Log.v(TAG, "Building the navigation drawer");
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new CustomListAdapter(this, mDrawerList, getDrawerGroupList(), getDrawerChildMasterList()));
		mDrawerList.setOnChildClickListener(this);
		mDrawerList.setOnGroupClickListener((ExpandableListView.OnGroupClickListener) new DrawerItemClickListener());
						
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  
                mDrawerLayout,         
                R.drawable.ic_drawer,  
                R.string.drawer_open, 
                R.string.drawer_close  
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            	mDrawerLayout.requestFocus();
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
                hideSoftKeyboard();
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if(keyCode == KeyEvent.KEYCODE_MENU) {
            Log.v(TAG, "onKeyDown()");    
            onMenuKeyPress();
            
            return true;
        }
        
        return super.onKeyDown(keyCode, keyEvent);
    }
    
    private void onMenuKeyPress() {
    	Log.v(TAG, "onMenuKeyPress()");
    	openNavigationDrawer();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.v(TAG, "onCreateOptionsMenu()");
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_items, menu);

    	Log.v(TAG, "Calling super.onCreateOptionsMenu()");
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	Log.v(TAG, "Calling super.onPrepareOptionsMenu()");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	Log.v(TAG, "onOptionsItemSelected()");
        if(mDrawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        
        switch(menuItem.getItemId()) {
	    	case R.id.menu_item_settings:
	    		displayPreferenceFragment();
	    		setCurrentFragmentPosition(GROUP_SETTINGS_POSITION, 0);
	    		break;
	    	case R.id.menu_item_open_drawer:
	    		openNavigationDrawer();
	    		break;
        }
        
        Log.v(TAG, "Calling super.onOptionsItemSelected()");
        return super.onOptionsItemSelected(menuItem);
    }

    private class DrawerItemClickListener implements ExpandableListView.OnGroupClickListener {
		@Override
		public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
			Log.v(TAG, "DrawerItemClickListener | onGroupClick()");
			return selectDrawerGroup(groupPosition);
		}
    }

    private boolean selectDrawerGroup(int position) {
    	Log.v(TAG, "selectDrawerGroup()");
    	if(position == GROUP_SETTINGS_POSITION) {
    		Log.d(TAG, "Group \"Settings\" is childless; return true from selectDrawerGroup() " +
    			"and call playSoundEffect() to simulate all correct behavior");
    		Log.d(TAG, "Returning false will cause an ArrayIndexOutOfBoundsException " +
    			"in DrawerItemClickListener");
    		
    		mDrawerList.playSoundEffect(android.view.SoundEffectConstants.CLICK);
    		displayPreferenceFragment();
    		setCurrentFragmentPosition(GROUP_SETTINGS_POSITION, 0);
    		
    		return true;
    	} else {
	        //displayDefaultFragment();
    	}
    	
    	return false;
    }
    
    private void displayPreferenceFragment() {
    	Log.v(TAG, "Displaying MyPreferenceFragment now");
    	MyPreferenceFragment preferenceFragment = new MyPreferenceFragment();

        finalizeFragmentTransaction(preferenceFragment);
        finalizePreferenceFragmentTransaction();
    }
    
    private void displayDefaultFragment() {
    	Log.v(TAG, "Displaying GenericFragment now");
    	GenericFragment fragment = new GenericFragment();

        finalizeFragmentTransaction(fragment);
    }

    private boolean selectDrawerChild(int groupPosition, int childPosition) {
    	Log.v(TAG, "selectDrawerChild()");
    	
    	if(selectionMatchesFragmentPosition(groupPosition, childPosition)) {
    		return true;
    	}
    	
    	if(groupPosition == GROUP_INVOLVED_POSITION) {
    		selectInvolvedFragment(childPosition);
    		setCurrentFragmentPosition(groupPosition, childPosition);
	        return true;
    	} else if(groupPosition == GROUP_ONLINE_POSITION) {
    		displayWebpageFragment(childPosition);
    		setCurrentFragmentPosition(groupPosition, childPosition);
	        return true;
    	} /*else if(groupPosition == GROUP_ABOUT_POSITION) { 
    		selectAboutFragment(childPosition);
    		setCurrentFragmentPosition(groupPosition, childPosition);
    		return true;
    	}*/ else if(groupPosition == GROUP_BUSINESSES_POSITION) {
    		displayBusinessWebpageFragment(childPosition);
    		setCurrentFragmentPosition(groupPosition, childPosition);
	        return true;
    	} else {
    		displayDefaultFragment();
    	}
    	
    	setCurrentFragmentPosition(groupPosition, childPosition);
    	return false;
    }
    
    private void displayWebpageFragment(final int childPosition) {
    	Log.v(TAG, "Displaying WebpageFragment now");
    	WebpageFragment webpageFragment = new WebpageFragment();
    	webpageFragment.setArguments(getFragmentArguments(webpageFragment, childPosition));

        finalizeFragmentTransaction(webpageFragment);
    }
    
    private void displayBusinessWebpageFragment(final int childPosition) {
    	Log.v(TAG, "Displaying BusinessWebpageFragment now");
    	BusinessWebpageFragment businessWebpageFragment = new BusinessWebpageFragment();
    	businessWebpageFragment.setArguments(getFragmentArguments(businessWebpageFragment, childPosition));

        finalizeFragmentTransaction(businessWebpageFragment);
    }
    
    private void selectAboutFragment(final int childPosition) {
    	Log.v(TAG, "selectAboutFragment()");
    	/*if(childPosition == ABOUT_LOCATION_POSITION) {
    		displayGoogleMapsFragment(childPosition);
		} else if(childPosition == ABOUT_FACEBOOK_POSITION) {
    		displayFacebookFragment(childPosition);
		} else if(childPosition == ABOUT_YOUTUBE_POSITION) {
    		displayYouTubeFragment(childPosition);
		} else if(childPosition == ABOUT_TWITTER_POSITION) {
    		displayTwitterFragment(childPosition);
		}*/
    }
    
    private void displayGoogleMapsFragment(final int childPosition) {
    	Log.v(TAG, "Displaying GoogleMapsFragment now");
    	GoogleMapsFragment googleMapsFragment = new GoogleMapsFragment();
    	googleMapsFragment.setArguments(getFragmentArguments(googleMapsFragment, childPosition));

        finalizeFragmentTransaction(googleMapsFragment);
    }
    
    private void displayFacebookFragment(final int childPosition) {
    	Log.v(TAG, "Displaying FacebookFragment now");
    	FacebookFragment facebookFragment = new FacebookFragment();
    	facebookFragment.setArguments(getFragmentArguments(facebookFragment, childPosition));

        finalizeFragmentTransaction(facebookFragment);
    }
    
    private void displayYouTubeFragment(final int childPosition) {
    	Log.v(TAG, "Displaying YouTubeFragment now");
    	YouTubeFragment youTubeFragment = new YouTubeFragment();
        youTubeFragment.setArguments(getFragmentArguments(youTubeFragment, childPosition));

        finalizeFragmentTransactionWithTag(youTubeFragment, "YouTubeFragment");
    }
    
    private void displayTwitterFragment(final int childPosition) {
    	Log.v(TAG, "Displaying TwitterFragment now");
    	TwitterFragment twitterFragment = new TwitterFragment();
        twitterFragment.setArguments(getFragmentArguments(twitterFragment, childPosition));

        finalizeFragmentTransaction(twitterFragment);
    }
    
    private void selectInvolvedFragment(final int childPosition) {
    	Log.v(TAG, "selectInvolvedFragment()");
    	
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDesktopSite = sharedPreferences.getBoolean("use_desktop_site", false);
        
        if(useDesktopSite) {
	    	if(childPosition == INVOLVED_GIVING_POSITION) {
	    		displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_EVENTS_POSITION) {
				displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_VOLUNTEER_POSITION) {
				displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_MESSAGES_POSITION) {
				displayPushMessageFragment(childPosition);
			} else if(childPosition == INVOLVED_REGISTER_POSITION) {
				displayRegisterFragment(childPosition);
			}
        } else {
	    	if(childPosition == INVOLVED_MOBILE_GIVING_POSITION) {
	    		displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_MOBILE_VOLUNTEER_POSITION) {
				displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_MOBILE_DONATE_POSITION) {
				displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_MOBILE_EVENTS_POSITION) {
				displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_MOBILE_ABOUT_POSITION) {
				displayInvolvedWebpageFragment(childPosition);
			} else if(childPosition == INVOLVED_MOBILE_MESSAGES_POSITION) {
				displayPushMessageFragment(childPosition);
			} else if(childPosition == INVOLVED_MOBILE_REGISTER_POSITION) {
				displayRegisterFragment(childPosition);
			}
        }
    }
    
    private void displayInvolvedWebpageFragment(final int childPosition) {
    	Log.v(TAG, "Displaying InvolvedWebpageFragment now");
    	InvolvedWebpageFragment involvedWebpageFragment = new InvolvedWebpageFragment();
    	involvedWebpageFragment.setArguments(getFragmentArguments(involvedWebpageFragment, childPosition));

        finalizeFragmentTransaction(involvedWebpageFragment);
    }
    
    private void displayPushMessageFragment(final int childPosition) {
    	Log.v(TAG, "Displaying PushMessageFragment now");
    	PushMessageFragment pushMessageFragment = new PushMessageFragment();
    	pushMessageFragment.setArguments(getFragmentArguments(pushMessageFragment, childPosition));

        finalizeFragmentTransactionWithTag(pushMessageFragment, "PushMessageFragment");
    }
    
    private void displayRegisterFragment(final int childPosition) {
    	Log.v(TAG, "Displaying RegisterFragment now");
    	RegisterFragment registerFragment = new RegisterFragment();
    	registerFragment.setArguments(getFragmentArguments(registerFragment, childPosition));

        finalizeFragmentTransaction(registerFragment);
    }
    
    private Bundle getFragmentArguments(final Fragment fragment, final int childPosition) {
    	Log.v(TAG, "Setting fragment arguments");
    	Bundle arguments = new Bundle();
    	
    	if(fragment instanceof InvolvedWebpageFragment) {
    		arguments.putInt(((InvolvedWebpageFragment) fragment).ARG_WEBPAGE_NUMBER, childPosition);
    	} else if(fragment instanceof TwitterFragment) {
    		arguments.putInt(((TwitterFragment) fragment).FRAGMENT_INDEX, childPosition);
    	} else if(fragment instanceof YouTubeFragment) {
    		arguments.putInt(((YouTubeFragment) fragment).FRAGMENT_INDEX, childPosition);
    	} else if(fragment instanceof FacebookFragment) {
    		arguments.putInt(((FacebookFragment) fragment).FRAGMENT_INDEX, childPosition);
    	} else if(fragment instanceof GoogleMapsFragment) {
    		arguments.putInt(((GoogleMapsFragment) fragment).FRAGMENT_INDEX, childPosition);
    	}  else if(fragment instanceof WebpageFragment) {
    		arguments.putInt(((WebpageFragment) fragment).ARG_WEBPAGE_NUMBER, childPosition);
    	} else if(fragment instanceof PushMessageFragment) {
    		arguments.putInt(((PushMessageFragment) fragment).FRAGMENT_INDEX, childPosition);
    	} else if(fragment instanceof RegisterFragment) {
    		arguments.putInt(((RegisterFragment) fragment).FRAGMENT_INDEX, childPosition);
    	} else {
    		Log.w(TAG, "No arguments were added to bundle");
    	}
    	
        return arguments;
    }
    
    private void finalizeFragmentTransactionWithTag(final Fragment fragment, final String fragmentTag) {
    	Log.v(TAG, "Finalizing fragment transaction with tag " + fragmentTag);
    	FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragmentTag).commit();
        
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    private void finalizeFragmentTransaction(final Fragment fragment) {
    	Log.v(TAG, "Finalizing fragment transaction");
    	FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    private void finalizePreferenceFragmentTransaction() {
    	Log.v(TAG, "Taking additional steps to finalize MyPreferenceFragment transaction");
    	mDrawerList.collapseGroup(GROUP_INVOLVED_POSITION);
		mDrawerList.collapseGroup(GROUP_ONLINE_POSITION);
		//mDrawerList.collapseGroup(GROUP_ABOUT_POSITION);
		mDrawerList.setItemChecked(GROUP_SETTINGS_POSITION, true);
    }
    
    private void setCurrentFragmentPosition(final int groupPosition, final int childPosition) {
    	Log.v(TAG, "Setting currently selected fragment position reference");
    	mCurrentFragmentPosition[0] = groupPosition;
    	mCurrentFragmentPosition[1] = childPosition;
    }
    
    private boolean selectionMatchesFragmentPosition(final int groupPosition, final int childPosition) {
    	Log.v(TAG, "Checking whether drawer selection is already on display");
    	if(mCurrentFragmentPosition[0] == groupPosition && mCurrentFragmentPosition[1] == childPosition) {
    		Log.w(TAG, "Selection is already on display; no Fragment change occured");		
    		mDrawerLayout.closeDrawer(mDrawerList);
    		
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private void hideSoftKeyboard() {
    	Log.v(TAG, "Hiding soft keyboard");
    	InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
      
    	View viewInFocus = this.getCurrentFocus();
    	if(viewInFocus == null) {
    		Log.d(TAG, "Could not get currently focused view");
    		Log.d(TAG, "Unable to hide soft keyboard");
    		
            return;
    	}
    	
      	inputMethodManager.hideSoftInputFromWindow(viewInFocus.getWindowToken(), 0);
    }
    
    @Override
    public void setTitle(CharSequence title) {
    	Log.v(TAG, "setTitle()");
    	
    	title = formatTitleForLowResolution(String.valueOf(title));
    	
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    private String formatTitleForLowResolution(String title) {
    	Log.v(TAG, "Checking if new action bar title needs to be formatted for a low " +
    		"resolution display");
    	Display display = getWindowManager().getDefaultDisplay();
    	
    	Point size = new Point();
    	display.getSize(size);
    	
    	int displayWidth = size.x;
    	Log.d(TAG, "Display width in pixels is: " + displayWidth);
    	
    	//Should use a constant for value 320
    	if(displayWidth < 320) {
    		Log.v(TAG, "Formatting title for a low resolution display");
    		int titleSeparator = title.indexOf("|");
    		title = "FIT " + title.substring(titleSeparator, title.length());
    	}
    	
    	return title;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onPostCreate()");
        super.onPostCreate(savedInstanceState);
        
        Log.v(TAG, "onPostCreate()");
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
    	Log.v(TAG, "Calling super.onConfigurationChanged()");
        super.onConfigurationChanged(newConfiguration);
        
        Log.v(TAG, "onConfigurationChanged()");
        mDrawerToggle.onConfigurationChanged(newConfiguration);
    }
    
    private ArrayList<String> getDrawerGroupList() {
    	Log.v(TAG, "getDrawerGroupList()");
    	return new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.drawer_item_group_titles)));
	}

	private ArrayList<Object> getDrawerChildMasterList() {
		Log.v(TAG, "getDrawerChildMasterList()");
		ArrayList<Object> masterList = new ArrayList<Object>();
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDesktopSite = sharedPreferences.getBoolean("use_desktop_site", false);
        
        if(useDesktopSite) {
        	masterList.add(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.drawer_involved_child_titles))));
        } else {
        	masterList.add(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.drawer_involved_mobile_child_titles))));
        }
        
		masterList.add(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.drawer_online_child_titles))));
		masterList.add(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.drawer_business_child_titles)))); //R.array.drawer_about_child_titles
		
		return masterList;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
		Log.v(TAG, "onChildClick()");
		return selectDrawerChild(groupPosition, childPosition);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.v(TAG, "Calling super.onActivityResult()");
		super.onActivityResult(requestCode, resultCode, intent);
		
		Log.v(TAG, "onActivityResult() called");
		if (requestCode == this.getResources().getInteger(R.integer.start_standalone_player)
				&& resultCode != RESULT_OK) {
			YouTubeInitializationResult youtubeInitializationResult = YouTubeStandalonePlayer
					.getReturnedInitializationResult(intent);
			
			if (youtubeInitializationResult.isUserRecoverableError()) {
				youtubeInitializationResult.getErrorDialog(this, 0).show();
			} else {
				String errorMessage = String.format(getString(R.string.youtube_player_error),
						youtubeInitializationResult.toString());
				
				Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
			}
		} 
	}
	
	@Override
	protected void onDestroy() {	
		Log.v(TAG, "onDestroy()");
		cleanupActivity();
		
		Log.v(TAG, "Calling super.onDestroy()");
		super.onDestroy();
	}
	
	private void cleanupActivity() {
		Log.v(TAG, "Cleaning up after MainActivity");
		try {
			unregisterReceiver(mPushMessageReceiver);;
		} catch(Exception exception) {
			Log.e(TAG, "Exception at cleanupActivity()");
			Log.e(TAG, "Couldn't unregister PushMessageReceiver");
		}
		
		mNotificationBuilder.cleanup();
		mBitmapFactory.cleanup();
		mAnimationManager.cleanup();
		
		mHandler.removeCallbacksAndMessages(null);
		mThreadUtilities.cancelAll();
		
		mPushMessageArray.clear();
		mPushMessageArray = null;
		
		mDrawerToggle = null;
	    mDrawerLayout = null;
	    mDrawerList = null;
	    
		mTitle = null;
		mDrawerTitle = null;
		mCurrentFragmentPosition = null;
	}
	
	private final BroadcastReceiver mPushMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			mPushMessageArray.add(newMessage);		

			/*  */
			/*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
	        boolean pushMessagesDisabled = sharedPreferences.getBoolean("disable_push_messages", false);
			Log.d(TAG, "SharedPreferences disable_push_messages value: " + pushMessagesDisabled);
			
	        if(pushMessagesDisabled == false) {
				GCMPluginParser gcmPluginParser = new GCMPluginParser(context);
				GCMPluginMessage gcmPluginMessage = gcmPluginParser.parseNewMessage(newMessage);
				
				if(gcmPluginMessage == null) {
					Log.w(TAG, "gcmPluginMessage is null; unable to display notification in tray");
				} else {
					mNotificationBuilder.displayNotification("FIT Update", gcmPluginMessage.getTitle());
				}
	        } else {
	        	Log.v(TAG, "Push messages are disabled in user preferences; " +
	        		"skipping creation of new Notification");
	        }*/
			/*  */
			
			String logMessage = "";
			if(newMessage.length() < 32) {
				logMessage = newMessage;
			} else {
				logMessage = newMessage.substring(0, 32) + "...";
			}
			Log.v(TAG, "PushMessageReceiver processing a new message: " + logMessage);
			
			WakeLocker.acquire(getApplicationContext());	
			Toast.makeText(getApplicationContext(), "New message: " + newMessage, Toast.LENGTH_LONG).show();
			
			PushMessageFragment pushMessageFragment = (PushMessageFragment) MainActivity.this.getFragmentManager().findFragmentByTag("PushMessageFragment");
			
			if(pushMessageFragment != null) {
				Log.v(TAG, "Pushing new messages to PushMessageFragment");
				pushMessageFragment.pushNewMessages(mPushMessageArray);
				mPushMessageArray.clear();
			}
		}
	};
	
	//@SuppressWarnings("unchecked")
	public ArrayList<String> getNewPushMessages() {
		Log.v(TAG, "getNewPushMessages()");
		
		Log.w(TAG, "Purposefully unchecked cast from Object to ArrayList<String>");
		ArrayList<String> newMessages = (ArrayList<String>) mPushMessageArray.clone();
		mPushMessageArray.clear();
		
		return newMessages;
	}
	
	public void openNavigationDrawer(Fragment requestingFragment) {
		Log.v(TAG, "openNavigationDrawer() called");
		Log.v(TAG, "Checking whether or not the requesting Fragment has permission to open drawer");
		
		if(requestingFragment instanceof PushMessageFragment) {
			Log.v(TAG, "PushMessageFragment has permission to open drawer");
			mDrawerLayout.openDrawer(Gravity.LEFT);
		} else {
			Log.w(TAG, "Unauthorized Fragment called openNavigationDrawer()");
			Log.w(TAG, "Fragment is of class type: " + requestingFragment.getClass());
		}
	}
	
	private void openNavigationDrawer() {
		Log.v(TAG, "openNavigationDrawer() called");
		
		new Handler().post(new Runnable() {
            @Override
            public void run() {
            	if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                	mDrawerLayout.closeDrawers();
                } else {
                	mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
	}
	
	public void changeDrawerListAdapter() {
		Log.v(TAG, "Changing drawer list adapter now");
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDesktopSite = sharedPreferences.getBoolean("use_desktop_site", false);
        
        if(useDesktopSite) {
        	mDrawerList.setAdapter(new CustomListAdapter(this, mDrawerList, getDrawerGroupList(), getDrawerChildMasterList()));
        } else {
        	mDrawerList.setAdapter(new CustomListAdapter(this, mDrawerList, getDrawerGroupList(), getDrawerChildMasterList()));
        }
	}
	
	public Handler getHandler() {
		Log.v(TAG, "getHandler()");
		
		return mHandler;
	}
	
	public ThreadUtilities getThreadUtilities() {
		Log.v(TAG, "getThreadUtilities()");
		
		return mThreadUtilities;
	}
	
	public MyBitmapFactory getBitmapFactory() {
		Log.v(TAG, "getBitmapFactory()");
		
		return mBitmapFactory;
	}
	
	public AnimationManager getAnimationManager() {
		Log.v(TAG, "getAnimationManager()");
		
		return mAnimationManager;
	}
}