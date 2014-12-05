package com.anirban.download_activity;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Service;
import android.app.Activity;
import android.app.PendingIntent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;

public class ThreadedDownloadService extends Service {
	public static final String FILENAME = "fileName";
	public static final String URLPATH = "urlPath";
	public static final String RESULT_KEY = "urlPath";
	public static final int KEY_ID = 1;
	public static String ACTION_RESULT = "ACTION_COMPLETE";
	private int result = Activity.RESULT_CANCELED;
	private static String TAG = "";

	public ThreadedDownloadService() {
		super();
		/*
		 * I was getting the below error for messenger and pending intent:
		 * W/System.err(940): android.os.NetworkOnMainThreadException
		 * W/System.err(940): at android.os.StrictMode$AndroidBlockGuardPolicy.
		 * onNetwork(StrictMode.java:1117) if I don't use ThreadPolicy.But the
		 * asyncTask was running fine. I went through few documents, and find
		 * that it's heavily discouraged to perform network operations on the
		 * main thread
		 * http://developer.android.com/reference/android/os/StrictMode.html
		 * (http://stackoverflow.com/search?q=NetworkOnMainThread+[android]) So,
		 * I used strict Mode StrictMode is a developer tool which detects
		 * things you might be doing by accident and brings them to your
		 * attention so you can fix them.
		 */
		// Network Stuff will run in the main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	/*
	 * Downloading the image from Server
	 */
	protected String download(String urlPath, String fileName) {
		Log.i("TAG", "Downloading... ");
		// Save it external directory sdcard
		File output = new File(Environment.getExternalStorageDirectory(),
				fileName);
		// If same named file exists , delete it
		if (output.exists()) {
			output.delete();
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		Bitmap bitmap = null;
		try {
			URL url = new URL(urlPath);
			stream = url.openConnection().getInputStream();
			Log.d(TAG, "Connection establised");
			Log.i(TAG, stream.toString());
			Log.i("image", "Image downloading..");
			bitmap = BitmapFactory.decodeStream(stream);
			fos = new FileOutputStream(output.getPath());
			Log.i("image", "Image Compressing..");
			// Compress the bitmap image to set in ImageView
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			// Flushing output on a buffered stream
			fos.flush();
			fos.close();
			Log.i("TAG", "Download Complete.. ");
			// Successfully finished
			result = Activity.RESULT_OK;
		}
		// If Image download failed
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(urlPath, "INVALID..File not found");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// Return the output Path
		return output.getAbsolutePath();
	}

	/*
	 * Taking input from runThreadMessenger Downloading the image using Thread
	 * and Messenger model
	 */
	protected void threadMessageDownload(Intent i) {
		try {
			i.getExtras();
			String urlPath = (String) i.getExtras().get(
					ThreadedDownloadService.URLPATH);
			String fileName = (String) i.getExtras().get(
					ThreadedDownloadService.FILENAME);
			Log.i("TAG", "ThreadMessageDownload... ");
			Log.i("TAG", urlPath);
			Log.i(TAG, fileName);
			String outputPath = download(urlPath, fileName);
			Log.i("Output", outputPath);
			// Sending the path as a message
			sendPath(outputPath, (Messenger) i.getExtras().get("MESSENGER"));
		} catch (Exception e1) {
			Log.e(getClass().getName(), "FAILED to Download", e1);
		}
	}

	/*
	 * Receiving the path and send message back to handler in Activity
	 */
	private void sendPath(String outputPath, Messenger messenger) {
		Log.i(TAG, "Sending..");
		Message backMsg = Message.obtain();
		backMsg.arg1 = result;
		Bundle bundle = new Bundle();
		Log.i("Sending Output..", outputPath);
		bundle.putString(RESULT_KEY, outputPath);
		Log.i(TAG, outputPath);
		backMsg.setData(bundle);
		Log.i("MESSAGE", "Got message");
		try {
			messenger.send(backMsg);
		} catch (android.os.RemoteException e1) {
			Log.e(getClass().getName(), "RemoteException sending message", e1);
		} catch (Exception e) {
			Log.e(getClass().getName(), "Exception sending message", e);
		}
	}

	/*
	 * Taking input from runPendingIntent Downloading the image using Thread and
	 * PendingIntent model
	 */
	private void threadPendingIntentDownload(Intent intent) {
		intent.getExtras();
		String urlPath = (String) intent.getExtras().get(
				ThreadedDownloadService.URLPATH);
		String fileName = (String) intent.getExtras().get(
				ThreadedDownloadService.FILENAME);
		Log.i("TAG", "Pending Intent Download... ");
		Log.i("TAG", urlPath);
		Log.i(TAG, fileName);
		String outputPath = download(urlPath, fileName);
		Log.i("Output", "Collecting output path.");
		Log.i("Output", outputPath);
		PendingIntent pendingIntent = (PendingIntent) intent.getExtras().get(
				"PENDING_INTENT");
		Intent replyIntent = new Intent();
		// Sending message to activity via pending Intent
		replyIntent.putExtra(RESULT_KEY, outputPath);
		try {
			pendingIntent.send(this, KEY_ID, replyIntent);
		} catch (PendingIntent.CanceledException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * Downloading the image using AsyncTask
	 */

	private void asyncTaskDownload(Intent intent) {
		intent.getExtras();
		final String urlPath = (String) intent.getExtras().get(
				ThreadedDownloadService.URLPATH);
		final String fileName = (String) intent.getExtras().get(
				ThreadedDownloadService.FILENAME);
		Log.i("TAG", "AsyncTask Download... ");
		Log.i("TAG", urlPath);
		Log.i(TAG, fileName);
		/*
		 * Declaring AysncTask Class
		 */
		class DownloadImage extends AsyncTask<String, Integer, String> {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
			@Override
			protected String doInBackground(String... params) {
				String outputPath = null;
				try {
					// Downloading the image
					Log.i("TAG", "Downloading in progress..");
					outputPath = download(urlPath, fileName);
					Log.i("Output", "Collecting output path.");
					Log.i(TAG, outputPath);
				} catch (Exception e) {
					Log.e("TAG", "ERROR..Unsucessful download");
					e.printStackTrace();
					outputPath = null;
					return null;
				}
				Log.i("Output", "Returning..");
				Log.i("Output", outputPath);
				return (outputPath);
			}

			protected void onPostExecute(String outputPath) {
				Log.i(TAG, "OnPostExecute..");
				// Send the Data to activity
				Log.i("Stored", outputPath);
				// ACTION_RESULT="ACTION_COMPLETE";
				Intent replyIntent = new Intent(ACTION_RESULT);
				Log.i("action", ACTION_RESULT);
				replyIntent.putExtra(RESULT_KEY, outputPath);
				sendBroadcast(replyIntent);
				Log.i("TAG", "Download via AyscTask is done..");
			}
		}
		try {
			new DownloadImage().execute();
		} catch (Exception e) {
			Log.e("TAG", "ERROR..AsyncTask failed to download.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.get("MESSENGER") != null) {
				Toast.makeText(this, "Messenger service starting",
						Toast.LENGTH_SHORT).show();
				Log.i("TAG", "ThreadMessageDownload called.. ");
				threadMessageDownload(intent);
			} else if (extras.get("PENDING_INTENT") != null) {
				Toast.makeText(this, "Pending Intent service starting",
						Toast.LENGTH_SHORT).show();
				Log.i("TAG", "Pending Intent Download called.. ");
				threadPendingIntentDownload(intent);
			} else {
				Log.i("TAG", "AsyncTask Receiver called.. ");
				asyncTaskDownload(intent);
			}
		}
		return Service.START_STICKY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}