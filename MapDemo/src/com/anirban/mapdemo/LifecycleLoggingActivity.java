/**
 * @author Anirban Bhattacharjee
 */
package com.anirban.mapdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

/*Creating my own Activity class inherited from android.app.Activity.*/
/*
 * User navigates through, out of, and back to your app, 
 * the Activity instances in your app transition between 
 * different states in their lifecycle.
 */

public class LifecycleLoggingActivity extends Activity {
	private String TAG = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(getClass().getSimpleName(), "onCreate()");
		Log.i(TAG, "******  MapDemo is Launching:C-H-E-E-R-S!!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_map_demo, menu);
		Log.i(TAG, "******  MapDemo is inflating as per the request");
		return true;
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(getClass().getSimpleName(), "onRestart()");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(getClass().getSimpleName(), "onStart()");
		Log.i(TAG, "******  MapDemo is starting: GPS update available");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(getClass().getSimpleName(), "onResume()");
		Log.i(TAG,
				"******  MapDemo is restarting: Resuming GPS update requests");
	}

	@Override
	public void onPause() {
		Log.d(getClass().getSimpleName(), "onPause()");
		Log.i(TAG,
				"******  MapDemo is pausing: Removing GPS update requests to save power");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.d(getClass().getSimpleName(), "onStop()");
		Log.i(TAG, "******  MapDemo is stopping: WAIT");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.d(getClass().getSimpleName(), "onDestroy()");
		Log.i(TAG, "******  MapDemo is saying GOOD-BYE!!!!");
		super.onDestroy();
	}
}
