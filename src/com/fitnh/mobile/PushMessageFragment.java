package com.fitnh.mobile;

import static com.fitnh.mobile.CommonUtilities.SENDER_ID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.fitnh.mobile.R;
import com.google.android.gcm.GCMRegistrar;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PushMessageFragment extends Fragment {
	private final String TAG = "FIT | PushMessageFragment";
	private final String GCM_PREFERENCES = "gcm_preferences";
	public final String FRAGMENT_INDEX = "fragment_index";
	private final int mCrossFadeAnimationDuration = 2000;
    
    private Context mContext;
    
    private String mFragmentTitle;
    
    private View mLoadingView;
    private ListView mListView;
    private Button mClearMessagesButton;
    
    private ArrayAdapter<String> mListViewAdapter;
    private ArrayList<String> mPushMessageArray;
    
    private ReadPushMessagesTask mReadPushMessagesTask;
    private AsyncTask<Void, Void, Void> mRegistrationTask;
    private Thread mServerThread;
    
    public PushMessageFragment() {
    	Log.v(TAG, "Calling default constructor");
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "Calling super.onCreate()");
        super.onCreate(savedInstanceState);
        
        Log.v(TAG, "onCreate()");
        mPushMessageArray = new ArrayList<String>();
    }
    
    private void createTitle(final Resources resources) {
    	Log.v(TAG, "Creating action bar title for PushMessageFragment");
    	
    	mFragmentTitle = "Families in Transition | Push Messages";
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
        View rootView = inflater.inflate(R.layout.fragment_push_message, container, false);         
        
        mContext = getActivity();
        setCustomOverscrollColor(mContext);
        
    	Resources resources = getResources();
    	createTitle(resources);
    	
    	mLoadingView = rootView.findViewById(R.id.loading_spinner);
        mListView = (ListView) rootView.findViewById(R.id.listview);
        
        setupListView();
        if(isRegistrationFormComplete()) {
        	setupAndStartRegistrationTask();
        } else {
        	((MainActivity) mContext).openNavigationDrawer(this);
        	showIncompleteFormDialog();
        }
        
        mClearMessagesButton = (Button) rootView.findViewById(R.id.clear_messages_butoon);
        mClearMessagesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	final int messageCount = mPushMessageArray.size();
            	
            	mPushMessageArray.clear();
            	mListViewAdapter.clear();
            	File file = new File(mContext.getFilesDir(), mContext.getString(R.string.saved_push_messages_file));
            	
            	Toast.makeText(mContext, "Messages cleared", Toast.LENGTH_SHORT).show();
            	Log.d(TAG, "All push messages cleared (" + String.valueOf(messageCount) + " messages)");
            }
        });
        
        ((MainActivity) mContext).getThreadUtilities().cleanupEnvironment();
        return rootView;
    }
    
    //@SuppressWarnings("unchecked")
	private void setupListView() {
    	Log.v(TAG, "Setting up the push message ListView");
    	//Log.w(TAG, "@SuppressWarnings(\"unchecked\") added for ReadPushMessagesTask; type safety errors may occur");
        
        mListViewAdapter = new ArrayAdapter<String>(mContext, R.layout.push_message_list_item);
        mListView.setAdapter(mListViewAdapter);
      
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.v(TAG, "Long click on child at position: " + position + " in ListView");
                showRemoveMessageDialog(position);
                
                return true;
            }
        }); 
        
        mReadPushMessagesTask = new ReadPushMessagesTask();
        mReadPushMessagesTask.execute();
    }
    
	private void showRemoveMessageDialog(final int childPosition) {
		if(childPosition < 0 || childPosition > (mPushMessageArray.size() - 1)) {
			Log.w(TAG, "showRemoveMessageDialog() called but childPosition was out of bounds");
			return;
		}
		
		Log.v(TAG, "showRemoveMessageDialog()");
		AlertDialogManager alertManager = new AlertDialogManager();
    	AlertDialog.Builder removeNoticeDialogBuilder = alertManager.getAlertDialogBuilder(mContext, "Remove Message", 
    			"Delete this message permanently?");
    	
    	removeNoticeDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialogInterface, int which) {
    			mListViewAdapter.remove(mPushMessageArray.get(childPosition));
    			mPushMessageArray.remove(childPosition);
    			mListViewAdapter.notifyDataSetChanged();
    			
    			Toast.makeText(mContext, "Message removed", Toast.LENGTH_LONG).show();
    			Log.d(TAG, "Removed child as position: " + childPosition + " in ListView");
    		}
    	}).setNegativeButton("No", null);
    	
    	alertManager.finishBuildingDialog(removeNoticeDialogBuilder);
	}
    
    /*private void pullNewMessages() {
    	Log.v(TAG, "Pulling new message data from MainActivity");
    	ArrayList<String> newMessages = ((MainActivity) mContext).getNewPushMessages();
    	Log.d(TAG, "Pulled " + newMessages.size() + " new message(s)");
    	
    	newMessages = prependTimeStampToAll(newMessages);
    	
    	mPushMessageArray.addAll(newMessages);
    	mListViewAdapter.addAll(newMessages);
    	listViewScrollToBottom();
    }*/
    
    public void pushNewMessages(ArrayList<String> newPushMessages) {
    	Log.v(TAG, "Receiving new push messages");
    	Log.d(TAG, "Adding " + newPushMessages.size() + " new message(s) to ListViewAdapter");
    	
    	newPushMessages = prependTimeStampToAll(newPushMessages);
    	
    	mPushMessageArray.addAll(newPushMessages);
    	mListViewAdapter.addAll(newPushMessages);
    	listViewScrollToBottom();
    }
    
    private ArrayList<String> prependTimeStampToAll(ArrayList<String> pushMessages) {
    	Log.v(TAG, "Prepending time stamps to all push messages");
    	TimeFormatter timeFormatter = new TimeFormatter();
    	String currentMessage = "";
    	
    	for(int i = 0; i < pushMessages.size(); i++) {
    		currentMessage = pushMessages.get(i);
    		pushMessages.set(i, timeFormatter.getTimeStamp() + " " + currentMessage);
    	}
    	
    	return pushMessages;
    }
    
    private void crossfadeViews(final View inView, final View outView) {
    	Log.v(TAG, "crossfadeViews()");
    	
        inView.setVisibility(View.VISIBLE);
        
        ((MainActivity) mContext).getAnimationManager().fadeViewIn(inView, mCrossFadeAnimationDuration);
        ((MainActivity) mContext).getAnimationManager().fadeViewOutAndHide(outView, mCrossFadeAnimationDuration);
    }
    
    private void readSavedPushMessages() {
    	Log.v(TAG, "Reading saved push messages");
		FileInputStream messageInputStream = null;
		int messageCount = 0;
		
		try {
			messageInputStream = mContext.openFileInput(mContext.getString(R.string.saved_push_messages_file));	
			BufferedReader messageReader = new BufferedReader(new InputStreamReader(messageInputStream));
            
		    String messageLine = null; 
		    try {
			    while((messageLine = messageReader.readLine()) != null) {
			        mPushMessageArray.add(messageLine);
			        messageCount++;
			        
			        String logMessage = "";
			        if(messageLine.length() < 32) {
			        	logMessage = messageLine;
			        } else {
			        	logMessage = messageLine.substring(0, 32) + "...";
			        }
			        Log.v(TAG, "Read message from file: " + logMessage);
			    }
			    
			    messageReader.close();
			    messageInputStream.close();
		    } catch(IOException ioException) { 
		    	Log.e(TAG, "IOException at readSavedPushMessages()");
		    }
		} catch(FileNotFoundException fileNotFoundException) {
			Log.e(TAG, "FileNotFoundException at readSavedPushMessages()");
		} finally {
			
		}
		
		Log.v(TAG, "Saved messages read: " + String.valueOf(messageCount));
    }
	
	private void savePushMessages() {
		Log.v(TAG, "Saving push messages");
		File file = new File(mContext.getFilesDir(), mContext.getString(R.string.saved_push_messages_file));
		int messageCount = 0;
		
		try {
			FileOutputStream messageOutputStream = mContext.openFileOutput(mContext.getString(R.string.saved_push_messages_file), Context.MODE_PRIVATE);		
			BufferedWriter messageWriter = new BufferedWriter(new OutputStreamWriter(messageOutputStream));
			
			for(int i = 0; i < mPushMessageArray.size(); i++) {
				String nextMessage = mPushMessageArray.get(i);
				messageWriter.write(nextMessage + "\n");
				messageCount++;
				
				String logMessage = "";
		        if(nextMessage.length() < 32) {
		        	logMessage = nextMessage;
		        } else {
		        	logMessage = nextMessage.substring(0, 32) + "...";
		        }
		        Log.v(TAG, "Saving message to file: " + logMessage);
			}
			
			messageWriter.close();
			messageOutputStream.close();
		} catch(Exception exception) {
			Log.e(TAG, "Exception at savePushMessages()");
		}  
		
		Log.v(TAG, "Messages saved: " + String.valueOf(messageCount));
	}
    
	private class ReadPushMessagesTask extends AsyncTask<Void, Void, View[]> {
	    @Override
	    protected void onPostExecute(View[] transitionViews) {
	    	Log.v(TAG, "ReadPushMessagesTask | onPostExecute()");
	    	Log.d(TAG, "Adding " + String.valueOf(mPushMessageArray.size()) + " messages to ListViewAdapter");
	    	mListViewAdapter.addAll(mPushMessageArray);
	    	crossfadeViews(transitionViews[0], transitionViews[1]);
	    }

		@Override
		protected View[] doInBackground(Void... params) {
			Log.v(TAG, "ReadPushMessagesTask | doInBackground()");
			readSavedPushMessages();
			return new View[] { mListView, mLoadingView };
		}
	}
	
	private void setupAndStartRegistrationTask() {
		Log.v(TAG, "Setting up and starting GCM registration task");
		if(mContext == null) {
			Log.w(TAG, "Context was null");
			Log.w(TAG, "Registration task not setup nor started");
			return;
		}
		
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(GCM_PREFERENCES, 0);
		final String userName = sharedPreferences.getString("gcm_user_name", "");
		final String userEmail = sharedPreferences.getString("gcm_user_email", "");	
		//final boolean formHasBeenUpdated = sharedPreferences.getBoolean("registration_form_updated", false);

		try {
			GCMRegistrar.checkDevice(mContext);
			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(mContext);
		} catch(UnsupportedOperationException unsupportedperationException) {
			Log.e(TAG, "An UnsupportedOperationException occurred in PushMessageFragment");
			Log.e(TAG, "Failure while GCMRegistrar was trying to checkDevice() or checkManifest()");
			
			Log.d(TAG, "Device might not have Google Play Services installed");
			Log.w(TAG, "RegistrationTask not started");
			
			Toast.makeText(mContext, "Your device doesn't have support for push messages through " +
				"Google Cloud Messaging.", Toast.LENGTH_LONG);
			
			return;
		}
	
		final String registrationId = GCMRegistrar.getRegistrationId(mContext);
	
		if(registrationId == null || registrationId.equals("")) {
			Log.d(TAG, "Device seems to have not yet been registered");
			Log.d(TAG, "Attempting to register with GCM server now");
			GCMRegistrar.register(mContext, SENDER_ID);
		} else {
			final String oldRegistrationId = sharedPreferences.getString("gcm_registration_id", null);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("gcm_registration_id", registrationId);
			editor.commit();
			
			if(GCMRegistrar.isRegisteredOnServer(mContext)) {
				Toast.makeText(mContext, "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {			
				mRegistrationTask = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						Log.v(TAG, "RegistrationTask | doInBackground()");
						registerWithServer(userName, userEmail, registrationId, oldRegistrationId);

						return null;
					}
	
					@Override
					protected void onPostExecute(Void result) {
						Log.v(TAG, "RegistrationTask | onPostExecute()");
						mRegistrationTask = null; 
					}
				};
				
				mRegistrationTask.execute(null, null, null);
			}
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
    public void onPause() {
    	Log.v(TAG, "onPause()");
    	savePushMessages();
    	
    	Log.v(TAG, "Calling super.onPause()");
    	super.onPause();
    }
    
    @Override
    public void onStop() {
    	Log.v(TAG, "onStop()");
    	savePushMessages();
    	
    	Log.v(TAG, "Calling super.onStop()");
    	super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	Log.v(TAG, "onDestroy()");
    	savePushMessages();
    	cleanupFragment();
    	
    	Log.v(TAG, "Calling super.onDestroy()");
    	super.onDestroy();  
    }
    
    private void cleanupFragment() {
    	Log.v(TAG, "Cleaning up after PushMessageFragment");
    	if(mReadPushMessagesTask != null) {
			mReadPushMessagesTask.cancel(true);
			mReadPushMessagesTask = null;
			Log.d(TAG, "ReadPushMessagesTask cancelled successfully: " + (mReadPushMessagesTask == null ? "true" : "false"));
		} else {
			Log.d(TAG, "ReadPushMessagesTask was null");
		}
    	
    	if(mRegistrationTask != null) {
			mRegistrationTask.cancel(true);
			mRegistrationTask = null;
			Log.d(TAG, "RegistrationTask cancelled successfully: " + (mRegistrationTask == null ? "true" : "false"));
		} else {
			Log.d(TAG, "RegistrationTask was null");
		}
		
    	if(mServerThread != null) {
    		mServerThread.interrupt();
    		mServerThread = null;
    	} else {
    		Log.d(TAG, "ServerThread was null");
    	}
    	
		try {
			GCMRegistrar.onDestroy(mContext);
		} catch(Exception exception) {
			Log.e(TAG, "Exception at cleanupActivity()");
			Log.e(TAG, "Couldn't destroy GCMRegistrar");
		}
		
    	mContext = null;

        mFragmentTitle = null; 
        
        mLoadingView.clearAnimation();
        mLoadingView = null;
        mListView.clearAnimation();
        mListView = null;
        mClearMessagesButton = null;
        
        mListViewAdapter.clear();
        mListViewAdapter = null;
        mPushMessageArray.clear();
        mPushMessageArray = null;
    }
    
    private void registerWithServer(final String userName, final String userEmail, final String registrationId, final String oldRegistrationId) {
    	mServerThread = new Thread() {
            @Override
            public void run() {
            	Log.v(TAG, "registerWithServer(String, String, String) with THREAD_PRIORITY_BACKGROUND");
            	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            	ServerUtilities.register(mContext, userName, userEmail, registrationId, oldRegistrationId);
            }
            
            @Override
            public void interrupt() {
            	Log.d(TAG, "Interrupting ServerThread");
            	Log.d(TAG, "Interruption will cause IOException in ServerUtilities");
            	
            	Log.v(TAG, "Calling super.interrupt()");
            	super.interrupt();
            }
        };
        
        mServerThread.start();
    }
    
    private boolean isRegistrationFormComplete() {
    	Log.v(TAG, "Checking registration form for completeness");
    	SharedPreferences sharedPreferences = mContext.getSharedPreferences(GCM_PREFERENCES, 0);
    	return sharedPreferences.getBoolean("registration_form_complete", false);
    }
    
    private void showIncompleteFormDialog() {
    	Log.v(TAG, "showIncompleteFormDialog()");
    	
    	AlertDialogManager alertManager = new AlertDialogManager();
    	AlertDialog.Builder formNoticeDialogBuilder = alertManager.getAlertDialogBuilder(mContext, "Registration Form", 
    			"You must first fill out the registration form before you can receive any push notifications");
    	
    	formNoticeDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialogInterface, int which) {
    		}
    	});
    	
    	alertManager.finishBuildingDialog(formNoticeDialogBuilder);
    }
    
    private final void listViewScrollToBottom() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
            	Log.v(TAG, "listViewScrollToBottom()"); 
            	mListView.smoothScrollToPosition((mListView.getCount() - 1));
            }
        });
    }
}
