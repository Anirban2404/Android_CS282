package com.anirban.download_activity;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DownloadActivity extends Activity {
	Messenger messenger = null;
	static ProgressDialog dialog;
	private static ImageView imageView;
	Bundle mCurrentScore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		imageView = (ImageView) findViewById(R.id.imageView1);
		Log.d(getClass().getSimpleName(), "onCreate()");
	}

	static Handler handler = new Handler() {
		public void handleMessage(Message message) {
			Bundle data = message.getData();
			String key = data.getString(ThreadedDownloadService.RESULT_KEY);
			Log.i("KEY", key);
			if (message.arg1 == RESULT_OK && data != null) {
				String text = data
						.getString(ThreadedDownloadService.RESULT_KEY);
				new DownloadActivity().displayBitmap(text);
			}
		}
	};

	/*
	 * Displaying the Image
	 */
	void displayBitmap(String pathname) {
		Log.i("TAG", "Displaying..");
		Log.i("TAG", pathname);
		try {
			Log.i("TAG", pathname);
			Bitmap bitmap = null;
			bitmap = BitmapFactory.decodeFile(pathname);
			// check for null
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				Log.d("TAG", "Image is set.");
			}
		} catch (Exception io) {
			Log.e("TAG", "Error while loading image. ");
		} finally {
			dialog.dismiss();
		}
	}

	/*
	 * Downloading the image using Thread and Messenger model
	 */

	public void runThreadedMessenger(View view) {
		Context context = getApplicationContext();
		// Hide the Keyboard after editing
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		EditText Url = (EditText) findViewById(R.id.editText1);
		final String url = Url.getText().toString();
		imageView.setImageResource(R.drawable.drschmidt);

		/*
		 * Checking the valid URL
		 */
		if (url.length() == 0)
			Toast.makeText(context, "Please enter the URL!!!",
					Toast.LENGTH_LONG).show();
		else if ((url.indexOf("http://") > -1)
				|| (url.indexOf("https://") > -1)) {
			dialog = ProgressDialog.show(this, "Download",
					"downloading via ThreadMessenger");
			Messenger messenger = new Messenger(handler);
			Log.i("TAG", "handler called. ");
			Intent intent = new Intent(this, ThreadedDownloadService.class);
			try {
				intent.putExtra(ThreadedDownloadService.FILENAME, "image.jpg");
				intent.putExtra(ThreadedDownloadService.URLPATH, url);
				intent.putExtra("MESSENGER", messenger);
				Log.i("TAG", "Message sending... ");
				Log.i("TAG", url);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.i("TAG", "Service called. ");
			// Messenger Service calling from Activity
			startService(intent);
		} else
			Toast.makeText(context, "Please enter correct URL!!!",
					Toast.LENGTH_LONG).show();
	}

	/*
	 * Downloading the image using Thread and PendingIntent model
	 */

	public void runThreadedPendingIntent(View view) {
		Context context = getApplicationContext();
		// Hide the Keyboard after editing
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		EditText Url = (EditText) findViewById(R.id.editText1);
		final String url = Url.getText().toString();
		imageView.setImageResource(R.drawable.drschmidt);
		/*
		 * Checking the valid URL
		 */
		if (url.length() == 0)
			Toast.makeText(context, "Please enter the URL!!!",
					Toast.LENGTH_LONG).show();
		else if ((url.indexOf("http://") > -1)
				|| (url.indexOf("https://") > -1)) {
			dialog = ProgressDialog.show(this, "Download",
					"downloading via Pending Intent");
			Log.i("TAG", "PendingIntent called. ");
			Intent intent = new Intent(this, ThreadedDownloadService.class);
			try {
				PendingIntent pendingIntent = createPendingResult(
						ThreadedDownloadService.KEY_ID, new Intent(), 0);
				intent.putExtra(ThreadedDownloadService.FILENAME, "image.jpg");
				intent.putExtra(ThreadedDownloadService.URLPATH, url);
				intent.putExtra("PENDING_INTENT", pendingIntent);
				Log.i("TAG", "PendingIntent called. ");
			} catch (Exception e) {
				e.printStackTrace();
			}
			startService(intent);
		} else
			Toast.makeText(context, "Please enter correct URL!!!",
					Toast.LENGTH_LONG).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("PI", "In Pending Intent.....");
		if (requestCode == ThreadedDownloadService.KEY_ID) {
			String key = data
					.getStringExtra(ThreadedDownloadService.RESULT_KEY);
			Log.i("Activity", key);
			displayBitmap(key);
		}
	}

	/*
	 * Downloading the image using AysncTask and Broadcast Receiver model
	 */

	public void runAsyncTaskReceiver(View view) {
		EditText Url = (EditText) findViewById(R.id.editText1);
		final String url = Url.getText().toString();
		Log.i("TAG", url);
		/*
		 * To Hide the keyboard after edit
		 */
		Context context = getApplicationContext();
		// Keyboard hiding
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		imageView.setImageResource(R.drawable.drschmidt);
		/*
		 * Checking the valid URL
		 */
		if (url.length() == 0) {
			Toast.makeText(context, "Please enter the URL!!!",
					Toast.LENGTH_LONG).show();
		} else if ((url.indexOf("http://") > -1)
				|| (url.indexOf("https://") > -1)) {
			dialog = ProgressDialog.show(this, "Download",
					"downloading via AsyncTask Receiver");
			Intent intent = new Intent(this, ThreadedDownloadService.class);
			intent.setAction("BroadCast_Intent");
			sendBroadcast(intent);
			Log.i("TAG", "AsyncTask called. ");
			try {
				intent.putExtra(ThreadedDownloadService.FILENAME, "image.jpg");
				intent.putExtra(ThreadedDownloadService.URLPATH, url);
				Log.i("TAG", "AsyncTask sending... ");
				Log.i("TAG", url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			startService(intent);
		} else
			Toast.makeText(context, "Please enter correct URL!!!",
					Toast.LENGTH_LONG).show();
	}

	/*
	 * Implementing BroadCast Receiver
	 */

	private BroadcastReceiver onEvent = new BroadcastReceiver() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		public void onReceive(Context ctxt, Intent intent) {
			Log.i("TAG", "In BroadCast Receiver..");
			String key = intent
					.getStringExtra(ThreadedDownloadService.RESULT_KEY);
			Log.i(key, key);
			// if (key=="failed")
			// dialog.dismiss();
			// else
			displayBitmap(key);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d("TAG", "onResume..");
		IntentFilter filter = new IntentFilter(
				ThreadedDownloadService.ACTION_RESULT);
		// Register the Receiver
		registerReceiver(onEvent, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		try {
			Log.d("TAG", "onPause..");
			unregisterReceiver(onEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d(getClass().getSimpleName(), "onDestroy");
		super.onDestroy();
		Intent i = new Intent(this, ThreadedDownloadService.class);
		this.stopService(i);
		Toast.makeText(this, "Download is done..destroyed!!",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Reset the Image
	 */

	public void resetImage(View view) {
		imageView.setImageResource(R.drawable.drschmidt);
		Log.i("TAG", "Reset the image. ");
	}
}