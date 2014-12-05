package com.anirban.thread.download;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ThreadedDownloadActivity extends Activity {

	private static ProgressDialog dialog;
	private static ImageView imageView;
	private static Bitmap downloadBitmap;
	private static Handler handler;
	private Thread downloadThread;
	private String TAG = "";
	private Message msg;
	protected static final int SET_PROGRESS_BAR_VISIBILITY = 0;

	@SuppressWarnings("deprecation")
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_threaded_download);
		Log.d(getClass().getSimpleName(), "onCreate()");
		Log.i(TAG, "******  ThreadDemo is Launching:C-H-E-E-R-S!!");

		// get the latest imageView after restart of the application
		imageView = (ImageView) findViewById(R.id.imageView1);
		// Did we already download the image?
		if (downloadBitmap != null) {
			imageView.setImageBitmap(downloadBitmap);
		}
		// Check if the thread is already running
		// Object downloadThread;
		downloadThread = (Thread) getLastNonConfigurationInstance();
		if (downloadThread != null && downloadThread.isAlive()) {
			dialog = ProgressDialog.show(this, "Download", "downloading..");
		}
	}

	/*
	 * To download the image from web via protocol
	 */
	Bitmap downloadBitmap(String url) throws IOException {
		long startTime = System.currentTimeMillis();
		Log.d("DownloadManager", "download begining");
		HttpUriRequest request = new HttpGet(url.toString());
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		long endTime = System.currentTimeMillis();
		/* checking the URL is OK or not */
		if (200 == statusCode) {
			HttpEntity entity = response.getEntity();
			byte[] bytes = EntityUtils.toByteArray(entity);
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
					bytes.length);
			Log.d("DownloadManager", "download ready in"
					+ ((endTime - startTime) / 1000) + " sec");
			return bitmap;
		} else {
			dialog.dismiss();
			/* if URL is invalid */
			throw new IOException("Download failed, HTTP response code "
					+ statusCode + " - " + statusLine.getReasonPhrase());
		}
	}

	/*
	 * Downloading Image using Runnable
	 */

	public void runRunnable(View view) {
		// Create a handler to update the UI
		handler = new Handler();
		EditText Url = (EditText) findViewById(R.id.editText1);
		final String url = Url.getText().toString();
		// Declaring TOAST method
		Context context = getApplicationContext();
		CharSequence text1 = "Oops..URL is not valid!!";
		CharSequence text2 = "Please enter the URL!!";
		int duration = Toast.LENGTH_SHORT;
		final Toast toast1 = Toast.makeText(context, text1, duration);
		Toast toast2 = Toast.makeText(context, text2, duration);
		// Hide the Keyboard after editing
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		// null URL checking
		if (url.length() == 0)
			toast2.show();
		// protocol checking
		else if ((url.indexOf("http://") > -1)
				|| (url.indexOf("https://") > -1)) {
			dialog = ProgressDialog.show(this, "Download",
					"downloading via Runnable");
			try {
				Runnable downloadThread = new Runnable() {
					public void run() {
						try {
							// Download the Image
							downloadBitmap = downloadBitmap(url);
							handler.post(new Runnable() {
								public void run() {
									imageView.setImageBitmap(downloadBitmap);
									dialog.dismiss();
								}
							});
						} catch (IOException e) {
							dialog.dismiss();
							e.printStackTrace();
							toast1.show();
						}
					}
				};
				new Thread(downloadThread).start();
			} catch (Exception e) {
				e.printStackTrace();
				dialog.dismiss();
				toast1.show();
			}
		} else
			toast1.show();
	}

	/*
	 * Implementing RunMessage method to download the image from web using
	 * message and handlers
	 */

	public void runMessages(View view) {
		// Initializing Handler
		final Handler mHandler;
		EditText Url = (EditText) findViewById(R.id.editText1);
		final String url = Url.getText().toString();
		Context context = getApplicationContext();
		// Declaring Toast Method
		CharSequence text1 = "Oops..URL is not valid!!";
		CharSequence text2 = "Please enter the URL!!";
		int duration = Toast.LENGTH_SHORT;
		final Toast toast1 = Toast.makeText(context, text1, duration);
		Toast toast2 = Toast.makeText(context, text2, duration);
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		// null URL checking
		if (url.length() == 0)
			toast2.show();
		else if ((url.indexOf("http://") > -1)
				|| (url.indexOf("https://") > -1)) {
			dialog = ProgressDialog.show(this, "Download",
					"downloading via RunMessage");
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					// process incoming messages here
					switch (msg.what) {
					case 1:
						imageView.setImageBitmap(downloadBitmap);
						dialog.dismiss();
						break;
					case 2:
						toast1.show();
						dialog.dismiss();
					}
				}
			};
			
			new Thread() {
				public void run() {
					try {
						// Download the Image
						downloadBitmap = downloadBitmap(url);
						// Message passing
						Message message = mHandler.obtainMessage(1,
								downloadBitmap);
						mHandler.sendMessage(message);
					} catch (IOException e) {
						dialog.dismiss();
						Message message = mHandler.obtainMessage(2, "error");
						mHandler.sendMessage(message);
						e.printStackTrace();
					}
				}
			}.start();
		}
		// if the URL is invalid
		else
			toast1.show();
	}

	/*
	 * Downloading the image using AsyncTask
	 */

	private class DownloadImage extends AsyncTask<Integer, Integer, Bitmap> {
		// Taking the URL as input
		EditText Url = (EditText) findViewById(R.id.editText1);
		final String url = Url.getText().toString();
		Context context = getApplicationContext();
		// Declaring Toast Method
		CharSequence text1 = "Oops..URL is not valid!!";
		CharSequence text2 = "Please enter the URL!!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast1 = Toast.makeText(context, text1, duration);
		Toast toast2 = Toast.makeText(context, text2, duration);

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		protected void onPreExecute() {
			// URL length and protocol checking
			if (url.length() == 0)
				toast2.show();
			else if ((url.indexOf("http://") == -1)
					|| (url.indexOf("http://") == -1))
				toast1.show();
			else
				Log.i("TAG", "Start downloading via asyncTask..");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bitmap doInBackground(Integer... params) {
			// Downloading the image
			try {
				if (url.length() != 0
						&& ((url.indexOf("http://") > -1) || (url
								.indexOf("http://") > -1))) {
					downloadBitmap = downloadBitmap(url);
					Log.i("TAG", "Downloading in progress..");
				}
			} catch (IOException e) {
				e.printStackTrace();
				toast1.show();
			}
			return downloadBitmap;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(Bitmap result) {
			if (dialog != null) {
				// Set the Downloaded image at imageview
				imageView.setImageBitmap(downloadBitmap);
				dialog.dismiss();
				Log.i("TAG", "Download via AyscTask is done..");
			}
		}
	}

	public void runAsyncTask(View view) {
		/*
		 * To Hide the keyboard after edit
		 */
		Context context = getApplicationContext();
		// Keyboard hiding
		InputMethodManager inputManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		new DownloadImage().execute();
		dialog = ProgressDialog.show(this, "Download",
				"downloading via AsyncTask");
	}

	/*
	 * Reseting of the image
	 */
	public void resetImage(View view) {
		if (downloadBitmap != null) {
			downloadBitmap = null;
		}
		imageView.setImageResource(R.drawable.drschmidt);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_threaded_download, menu);
		return true;
	}

	// Save the thread
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return downloadThread;
	}

	// dismiss dialog if activity is destroyed
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		super.onDestroy();
	}

}
