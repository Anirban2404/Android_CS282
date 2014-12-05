package com.anirban.enhanced_content_provider;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */
import java.io.File;
import java.lang.ref.WeakReference;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.anirban.Database.IMAGE_Table;

public class DownloadActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	protected static ProgressDialog dialog;
	private ImageView imageView;
	private AsyncScript asyncservice = null;
	private boolean asyncbound = false;
	private ListView listview;
	private SimpleCursorAdapter adapter;

	private static final String TAG = "DownloadActivity";

	public static final int SERVERPORT = 6000;
	public static boolean isConnected = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		// Declaring the imageView
		imageView = (ImageView) findViewById(R.id.imageView1);
		Log.d(getClass().getSimpleName(), "onCreate()");
		// Declaring the listview
		listview = (ListView) findViewById(R.id.listView1);
		MyContentObserver contentObserver = new MyContentObserver();
		this.getContentResolver().registerContentObserver(
				EnhancedContentProvider.CONTENT_URI, true, contentObserver);
	}

	/*
	 * Displaying the Image to Imageview
	 */
	void displayBitmap(String pathname) {
		Log.i("DisplayImage", "Displaying..");
		Log.i("Path", pathname);
		try {
			Log.i("Display", pathname);
			Bitmap bitmap = null;
			// Decode an immutable bitmap from the specified byte array
			bitmap = BitmapFactory.decodeFile(pathname);
			// check for null
			if (bitmap != null) {
				// Setting the downloaded image
				imageView.setImageBitmap(bitmap);
				Log.i("IMAGE", "Image is set.");
				Toast.makeText(this, "Image Displayed", Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception io) {
			// Error if cannot set the image
			Log.e("Error", "Error while loading image. ");
			Toast.makeText(this, "Error while loading image ",
					Toast.LENGTH_SHORT).show();
		} finally {
			Log.i("Cool", "Displayed....");
		}
	}

	/*
	 * Creating the Connection for Async Model
	 */

	private ServiceConnection asynconn = new ServiceConnection() {
		// Connecting to Async_bindService
		public void onServiceConnected(ComponentName name, IBinder ibinder) {
			// communicate with async_service using IPC
			asyncservice = AsyncScript.Stub.asInterface(ibinder);
			asyncbound = true;
			Log.d("ServiceConnection", "OnServiceConnected Async..");
		}

		// Disconnecting from Async_bindService
		public void onServiceDisconnected(ComponentName name) {
			asyncservice = null;
			asyncbound = false;
			Log.d("ServiceConnection", "OnServiceDisonnected..");
		}
	};

	ServiceConnection sync = new ServiceConnection() {
		/*
		 * (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("Sync", "setsyncService bound ");
		}

		/*
		 * (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("Sync", "setsyncService Unbound ");
		}
	};

	ServiceConnection authenticate = new ServiceConnection() {
		/*
		 * (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("Auth", "AuthenticationService bound ");
		}

		/*
		 * (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("Auth", "AuthenticationService Unbound ");
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
		Log.d("resume", "OnResume..");
		Log.i("onStart", "Binding the services..");

		// Binding with Async_Service
		Intent asyncintent = new Intent(DownloadActivity.this,
				DownloadService.class);
		bindService(asyncintent, this.asynconn, Context.BIND_AUTO_CREATE);
		Log.i("DownloadActivity", "Binded to DownloadService");
		asyncbound = true;
		// myCommsThread = new Thread(new
		// mySyncHandler(getApplicationContext()));
		// myCommsThread.start();
		new mySyncHandler(getApplicationContext()).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "OnStart..");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("", "OnPause..");
		// Unbinding the services..
		Log.i("UnBind", "Unbinding the services..");
		if (asyncbound) {
			unbindService(asynconn);
			Log.d("Unbinded", "unbindAsyncService!!!");
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
		Log.d("", "OnStop..");
	}

	/*
	 * Initiate downloading the image via AsyncTask Model
	 */
	public void runAsyncTask(View view) {
		if (asyncbound) {
			Log.i("AsyncTask", "Run Async AIDL pressed..");
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
			Log.i("", "Downloading via Async AIDL...");
			if (url.length() == 0)
				Toast.makeText(context, "Please enter the URL!!!",
						Toast.LENGTH_LONG).show();
			else if ((url.indexOf("http://") > -1)
					|| (url.indexOf("https://") > -1)) {
				dialog = ProgressDialog.show(this, "Download",
						"downloading via Async AIDL");
				try {
					Log.i("AsyncDownload", "AsyncTask calling..");
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
			asyncservice.downloadImage(url, callback);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Fetching the filename back to DownloadActivity as a callback and display
	 * in imageView by Async Model
	 */
	protected final AsyncDisplay.Stub callback = new AsyncDisplay.Stub() {
		public void executeDisplay(String outputPath) {
			Log.i("DisplayStub", outputPath);
			Log.i("Aysncstub", "Returning result to activity..");
			dialog.dismiss();
			final String pathName = outputPath;
			runOnUiThread(new Runnable() {
				public void run() {
					Log.i("Pathname", pathName);
					if (pathName.equals("Failed")) {
						Log.e("Pathname", pathName);
						Log.e("Err..", "Incorrect url..");
						Toast.makeText(DownloadActivity.this,
								"Please enter CORRECT URL!!!..Try Again",
								Toast.LENGTH_LONG).show();
					} else {
						Log.i("Pathname", pathName);
						Log.i("AsyncActivity",
								"fetching ImagePath via Callback..");
						Toast.makeText(DownloadActivity.this,
								"Downloaded URI :" + pathName,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	};

	/*
	 * Set sync called to connect the device (emulator) to another device
	 * (emulator) and synchronizing the state of the ContentProviders so they
	 * are consistent
	 */

	public void syncConnection(View view) {
		Toast.makeText(this, "Sync in Progress", Toast.LENGTH_SHORT).show();
		Account account = new Account("ANIRBAN",
				this.getString(R.string.ACCOUNT_TYPE));
		AccountManager accountManager = AccountManager.get(this);
		accountManager.addAccountExplicitly(account, "PASSWORD", null);
		ContentResolver.setIsSyncable(account,
				"com.anirban.enhanced_content_provider", 1);

		Log.d(TAG, "Sync Automatic");

		// Bundle for configuring sync settings
		Bundle result = new Bundle();
		result.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		result.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

		Log.d(TAG, "Sync Request");
		ContentResolver.requestSync(account,
				"com.anirban.enhanced_content_provider", result);
	}

	/*
	 * Query via CursorLoader QueryviaCursorLoader method uses a CursorLoader to
	 * return a Cursor containing all the file(s) that match the URI back to
	 * thread and display it on listview. On clicking on it opens the file(s)
	 * and causes the bitmap(s) to be displayed on the screen back to
	 * DownloadActivity as an onLoadFinished() callback, which opens the file(s)
	 * and causes the bitmap(s) to be displayed on the screen.
	 */
	public void QueryviaCursorLoader(View view) {
		// Making Listview Visible
		listview.setVisibility(View.VISIBLE);
		// Hiding the Image View
		imageView.setVisibility(View.GONE);
		// Stops and removes the loader
		getLoaderManager().destroyLoader(0);
		/*
		 * calling initloader, which ensures a loader is initialized and active.
		 * If the loader doesn't already exist, one is created and (if the
		 * activity/fragment is currently started) starts the loader. Otherwise
		 * the last created loader is re-used.
		 */
		String[] projection = new String[] { IMAGE_Table.COLUMN_DESCRIPTION,
				IMAGE_Table.COLUMN_TIME, IMAGE_Table.COLUMN_DESCRIPTION };
		int[] disp = new int[] { R.id.list_image, R.id.time, R.id.title };
		getLoaderManager().initLoader(0, null, DownloadActivity.this);

		// Adapter object acts as a bridge between an AdapterView and the
		// underlying data for that view
		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.listview, null, projection, disp, 0);
		// Displaying in listview
		listview.setAdapter(adapter);
		// Click event for single list row
		listview.setOnItemClickListener(new OnItemClickListener() {
			// @SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = (Cursor) listview.getAdapter().getItem(position);
				// Making Listview hidden
				listview.setVisibility(View.GONE);
				// Making Imageview visible
				imageView.setVisibility(View.VISIBLE);
				// fetching the string form list
				c.moveToPosition(position);
				int a = c.getColumnIndex(IMAGE_Table.COLUMN_DESCRIPTION);
				String path = c.getString(a);
				Log.e("Path", path);
				// Calling the display function
				displayBitmap(path);
			}
		});
	}

	// Creates a new loader after the initLoader () call
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(DownloadActivity.this,
				EnhancedContentProvider.CONTENT_URI, null, null, null, null);
		return cursorLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content
	 * .Loader, java.lang.Object)
	 */

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content
	 * .Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	/*
	 * Query via AsyncQueryHandler QueryviaAsyncQueryHandler method uses an
	 * AsyncQueryHandler to return a Cursor containing all the file(s) that
	 * matches the URI back to thread and display it in listview. On clicking it
	 * back to DownloadActivity as an onQueryComplete() callback, which opens
	 * the file(s) and causes the bitmap(s) to be displayed on the screen.
	 */

	public void deleteviaAsyncQueryHandler(View view) {
		// Making Listview visible
		listview.setVisibility(View.VISIBLE);
		// Making Imageview hidden
		imageView.setVisibility(View.GONE);
		QueryHandler myQueryHandler = new QueryHandler(DownloadActivity.this);
		// Begins the asynchronous query
		myQueryHandler.startQuery(0, null, EnhancedContentProvider.CONTENT_URI,
				(String[]) null, (String) null, (String[]) null, (String) null);

		// Click event for single list row
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Making Listview hidden
				listview.setVisibility(View.GONE);
				// Making Imageview visible
				imageView.setVisibility(View.VISIBLE);
				Cursor c = (Cursor) listview.getAdapter().getItem(position);
				// fetching the string form list
				c.moveToPosition(position);
				int a = c.getColumnIndex(IMAGE_Table.COLUMN_DESCRIPTION);
				String path = c.getString(a);
				Log.e("Path", path);
				// Calling the delete function
				deleteviaAsyncQueryHandler_helper(path);
			}
		});
	}

	/*
	 * Clear all the images from database as well as from storage
	 */
	public void clearAllImages(View view) {
		QueryHandler myQueryHandler = new QueryHandler(DownloadActivity.this);
		final Object mCommand = null;
		Context mycontext = this;
		Cursor cursor = mycontext.getContentResolver().query(
				EnhancedContentProvider.CONTENT_URI, null, null, null, null);
		myQueryHandler.startDelete(0, mCommand,
				EnhancedContentProvider.CONTENT_URI, null, (String[]) null);
		if (cursor != null) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				String deleteString = cursor.getString(cursor
						.getColumnIndexOrThrow(IMAGE_Table.COLUMN_DESCRIPTION));
				// Deleting the file from sdcard via the method
				deleteviaAsyncQueryHandler_helper(deleteString);
				cursor.moveToNext();
			}
		}
		// Making Listview hidden
		listview.setVisibility(View.GONE);
		// Making Imageview visible
		imageView.setVisibility(View.VISIBLE);
		Log.w(TAG, "all the images are deleted..");

	}

	public void deleteviaAsyncQueryHandler_helper(String deletePath) {
		final Object mCommand = null;
		String mValue = IMAGE_Table.COLUMN_DESCRIPTION + "= \"" + deletePath
				+ "\"";
		Log.i("deletePath", mValue);
		QueryHandler myQueryHandler = new QueryHandler(DownloadActivity.this);
		myQueryHandler.startDelete(0, mCommand,
				EnhancedContentProvider.CONTENT_URI, (String) mValue,
				(String[]) null);
		File file = new File(deletePath);
		// Deleting the file from sdcard
		if (file.exists()) {
			file.delete();
			Log.w(file.getName(), " is deleted!");
		} else {
			Log.w(file.getName(), "Delete operation is failed.");
		}
		Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show();
	}

	// Declaring class extends AsyncQueryHandler
	private class QueryHandler extends AsyncQueryHandler {
		// Use weak reference to avoid memory leak
		private WeakReference<DownloadActivity> mydownloadActivity;

		public QueryHandler(Context context) {
			super(context.getContentResolver());
			mydownloadActivity = new WeakReference<DownloadActivity>(
					(DownloadActivity) context);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.content.AsyncQueryHandler#onQueryComplete(int,
		 * java.lang.Object, android.database.Cursor)
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, final Cursor cr) {
			// do some stuff for the query result for token
			DownloadActivity activity = mydownloadActivity.get();
			if (activity != null && !activity.isFinishing()) {
				if (cr != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							String[] projection = new String[] {
									IMAGE_Table.COLUMN_DESCRIPTION,
									IMAGE_Table.COLUMN_TIME,
									IMAGE_Table.COLUMN_DESCRIPTION };
							int[] disp = new int[] { R.id.list_image,
									R.id.time, R.id.title };
							// The adapter that binds data to the ListView
							adapter = new SimpleCursorAdapter(
									DownloadActivity.this, R.layout.listview,
									cr, projection, disp, 0);
							// listview is set
							listview.setAdapter(adapter);
						}
					});
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.content.AsyncQueryHandler#onDeleteComplete(int,
		 * java.lang.Object, int)
		 */
		@Override
		protected void onDeleteComplete(int token, Object cookie, int id) {
			// do some stuff for the query result for token
			DownloadActivity activity = mydownloadActivity.get();
			if (activity != null && !activity.isFinishing()) {
				runOnUiThread(new Runnable() {
					public void run() {
					}
				});
			}
		}
	}

	/*
	 * Reset the Image
	 */

	public void resetImage(View view) {
		imageView.setImageResource(R.drawable.drschmidt);
		Log.i("RESETIMAGE", "Reset the image. ");
	}

	private class MyContentObserver extends ContentObserver {
		public MyContentObserver() {
			super(null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.database.ContentObserver#onChange(boolean)
		 */
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("CHANGEIMAGE", "Change in database occured.. ");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.database.ContentObserver#deliverSelfNotifications()
		 */
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

	}
}
