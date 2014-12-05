package com.anirban.boundedservice;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DownloadActivity extends Activity {

	private static ProgressDialog dialog;
	private static ImageView imageView;
	private SyncScript syncservice = null;
	private AsyncScript asyncservice = null;
	String outputPath = null;
	boolean syncbound = false;
	boolean asyncbound = true;

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

	/*
	 * Displaying the Image to Imageview
	 */
	void displayBitmap(String pathname) {
		Log.i("TAG", "Displaying..");
		Log.i("TAG", pathname);
		try {
			Log.i("Display", pathname);
			Bitmap bitmap = null;
			// Decode an immutable bitmap from the specified byte array
			bitmap = BitmapFactory.decodeFile(pathname);
			// check for null
			if (bitmap != null) {
				// Setting the downloaded image
				imageView.setImageBitmap(bitmap);
				Log.i("TAG", "Image is set.");
			}
		} catch (Exception io) {
			// Error if cannot set the image
			Log.e("TAG", "Error while loading image. ");
		} finally {
			// Dismissing the dialog finally
			dialog.dismiss();
		}
	}

	/*
	 * Creating the Connection for Sync Model
	 */
	private ServiceConnection synconn = new ServiceConnection() {
		// Connecting to Sync_bindservice
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// communicate with sync_service using IPC
			syncservice = SyncScript.Stub.asInterface(binder);
			syncbound = true;
			Log.d("TAG", "OnServiceConnected sync..");
		}

		// Disconnecting from Sync_bindService
		public void onServiceDisconnected(ComponentName name) {
			syncservice = null;
			syncbound = false;
			Log.d("TAG", "OnServiceDisonnected..");
		}
	};

	/*
	 * Creating the Connection for Async Model
	 */

	private ServiceConnection asynconn = new ServiceConnection() {
		// Connecting to Async_bindService
		public void onServiceConnected(ComponentName name, IBinder ibinder) {
			// communicate with async_service using IPC
			asyncservice = AsyncScript.Stub.asInterface(ibinder);
			asyncbound = true;
			Log.d("TAG", "OnServiceConnected Async..");
		}

		// Disconnecting from Async_bindService
		public void onServiceDisconnected(ComponentName name) {
			asyncservice = null;
			asyncbound = false;
			Log.d("TAG", "OnServiceDisonnected..");
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("TAG", "OnResume..");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("TAG", "OnStart..");
		Log.i("TAG", "Binding the services..");
		// Binding with Sync_Service
		Intent syncintent = new Intent(DownloadActivity.this,
				DownloadBoundServiceSync.class);
		bindService(syncintent, this.synconn, Context.BIND_AUTO_CREATE);

		Log.i("TAG", "Binded the Syncservice..");

		// Binding with Aync_Service
		Intent asyncintent = new Intent(this, DownloadBoundServiceAsync.class);
		bindService(asyncintent, this.asynconn, Context.BIND_AUTO_CREATE);

		Log.i("TAG", "Binded the Asyncservice..");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("TAG", "OnPause..");
		// Unbinding the services..
		Log.i("TAG", "Unbinding the services..");
		if (syncbound) {
			unbindService(synconn);
			Log.d("TAG", "unbindSyncService!!!");
		}
		if (asyncbound) {
			unbindService(asynconn);
			Log.d("TAG", "unbindAsyncService!!!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("TAG", "OnStop..");
	}

	// runSyncTask
	/*
	 * Download and Set the image by Sync Model
	 */
	public void runSyncTask(View view) {
		if (syncbound) {
			Log.i("TAG", "Run Sync AIDL pressed..");
			imageView.setImageResource(R.drawable.drschmidt);
			Context context = getApplicationContext();
			EditText Url = (EditText) findViewById(R.id.editText1);
			final String url = Url.getText().toString();
			// final String filename="image.jpg";

			// Hide the Keyboard after editing
			InputMethodManager inputManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			/*
			 * Checking the valid URL
			 */
			Log.i("TAG", "Downloading via Sync AIDL...");
			if (url.length() == 0)
				Toast.makeText(context, "Please enter the URL!!!",
						Toast.LENGTH_LONG).show();
			else if ((url.indexOf("http://") > -1)
					|| (url.indexOf("https://") > -1)) {
				/*
				 * Progress dialog for SyncTask is called here. I tried
				 * different things over here but none worked.
				 */
				Log.i("TAG", "dialog");
				dialog = ProgressDialog.show(DownloadActivity.this, "Download",
						"downloading via Sync AIDL");
				String i = dialog.toString();
				Log.i("TAG", "dialog" + i);
				// calling the method to download and display the image
				threadeddownload_sync(url);
			} else
				Toast.makeText(context, "Please enter CORRECT URL!!!",
						Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * the method to download and display the image
	 */
	protected void threadeddownload_sync(final String url) {

		final String filename = "image.jpg";
		try {
			Log.i("ThreadedDownload", url);
			/*
			 * Running the download task in a thread
			 */
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						Log.i("Thread Sync", url + "/" + filename);
						outputPath = syncservice.syncScript(url, filename);
						Log.i("Thread Sync", outputPath);
						try {
							// Returning the value to the main thread
							runOnUiThread(new Runnable() {
								public void run() {
									Log.i("ThreadUI Sync", outputPath);
									// call the method to display it in
									// imageView
									displayBitmap(outputPath);
									Log.i("TAG", outputPath);
								}
							});
						} catch (Exception ie) {
							Log.e("Error", "Err.....");
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
			// Starting the thread
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// runAsyncTask
	/*
	 * Run and set the image via AsyncTask Model
	 */
	public void runAsyncTask(View view) {
		if (asyncbound) {
			Log.i("TAG", "Run Async AIDL pressed..");
			Context context = getApplicationContext();
			EditText Url = (EditText) findViewById(R.id.editText1);
			final String url = Url.getText().toString();
			imageView.setImageResource(R.drawable.drschmidt);
			// Hide the Keyboard after editing
			InputMethodManager inputManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			/*
			 * Checking the valid URL
			 */
			Log.i("TAG", "Downloading via Async AIDL...");
			if (url.length() == 0)
				Toast.makeText(context, "Please enter the URL!!!",
						Toast.LENGTH_LONG).show();
			else if ((url.indexOf("http://") > -1)
					|| (url.indexOf("https://") > -1)) {
				Log.i("TAG", "dialog");
				dialog = ProgressDialog.show(this, "Download",
						"downloading via Async AIDL");
				try {
					Log.i("TAG", "AsyncTask calling..");
					// Call the method to download and display the image via
					// Async Model
					AsyncsendCallbackService(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				Toast.makeText(context, "Please enter CORRECT URL!!!",
						Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * the method sends request to the service to download and store file,
	 * passes callback as a parameter to the original oneway AsyncDisplay AIDL
	 */
	public void AsyncsendCallbackService(String url) {
		try {
			asyncservice.downloadScript(url, icallback);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Fetching the filename back to DownloadActivity as a callback and display
	 * in imageView by Async Model
	 */
	protected final AsyncDisplay.Stub icallback = new AsyncDisplay.Stub() {
		public void executeDisplay(final String ouputPath) {
			Log.i("DisplayStub", outputPath);
			Log.i("TAG", "Returning result to activity..");
			runOnUiThread(new Runnable() {
				public void run() {
					Log.i("AsyncActivity", "displaying Image via Callback..");
					displayBitmap(outputPath);
				}
			});
		}
	};

	/*
	 * Reset the Image
	 */

	public void resetImage(View view) {
		imageView.setImageResource(R.drawable.drschmidt);
		Log.i("TAG", "Reset the image. ");
	}

}
