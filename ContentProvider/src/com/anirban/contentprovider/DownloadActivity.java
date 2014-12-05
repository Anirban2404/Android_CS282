package com.anirban.contentprovider;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import java.lang.ref.WeakReference;

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
import android.widget.TextView;
import android.widget.Toast;

public class DownloadActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static ProgressDialog dialog;
	private ImageView imageView;
	private AsyncScript asyncservice = null;
	private boolean asyncbound = false;
	private ListView listview;
	private SimpleCursorAdapter adapter;

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

		// Binding with Aync_Service
		Intent asyncintent = new Intent(DownloadActivity.this,
				DownloadService.class);
		bindService(asyncintent, this.asynconn, Context.BIND_AUTO_CREATE);
		Log.i("BindService", "Binded the Asyncservice..");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("", "OnStart..");
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
		public void executeDisplay(final String outputPath) {
			Log.i("DisplayStub", outputPath);
			Log.i("Aysncstub", "Returning result to activity..");
			runOnUiThread(new Runnable() {
				public void run() {
					String pathName = outputPath;
					Log.i("AsyncActivity", "fetching ImagePath via Callback..");
					dialog.dismiss();
					Toast.makeText(DownloadActivity.this,
							"Downloaded URI :" + pathName, Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
	};

	/*
	 * QueryviaQuery method calls query() on the ContentResolver to request that
	 * the associated ContentProvider to provide a cursor containing all the
	 * file(s) that match the URI back to thread and displayed it in Listview.
	 * By ckicking on it opens the file(s) and causes the bitmap(s) to be
	 * displayed on the screen ImageView.
	 */

	public void QueryviaQuery(View view) {
		// Making Listview visible
		listview.setVisibility(View.VISIBLE);
		// Making Imageview hidden
		imageView.setVisibility(View.GONE);
		// Spwaning the thread
		new Thread(new Runnable() {
			public void run() {
				// Calling content resolver
				final ContentResolver cr = getContentResolver();
				// Declaring cursor
				final Cursor cursor = cr.query(
						DownloadContentProvider.CONTENT_URI, null, null, null,
						null);
				// columns to retrieve
				final String[] projection = new String[] {
						IMAGE_Table.COLUMN_TIME, IMAGE_Table.COLUMN_DESCRIPTION };
				runOnUiThread(new Runnable() {
					public void run() {
						adapter = new SimpleCursorAdapter(
								DownloadActivity.this, R.layout.listview,
								cursor, projection, new int[] { R.id.time,
										R.id.title }, 0);
						// setting the listview
						listview.setAdapter(adapter);

						// Click event for single list row
						listview.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// Hiding the listview
								listview.setVisibility(View.GONE);
								// Making Imageview visible
								imageView.setVisibility(View.VISIBLE);

								// Fetching the data from column
								int a = cursor
										.getColumnIndex(IMAGE_Table.COLUMN_DESCRIPTION);
								String path = cursor.getString(a);
								Log.e("Path", path);
								// Calling the display function
								displayBitmap(path);
							}
						});
					}
				});
			}
		}).start();
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
		getLoaderManager().initLoader(0, null, DownloadActivity.this);
		// Adapter object acts as a bridge between an AdapterView and the
		// underlying data for that view
		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.listview, null,
				new String[] { IMAGE_Table.COLUMN_TIME,
						IMAGE_Table.COLUMN_DESCRIPTION }, new int[] {
						R.id.time, R.id.title }, 0);
		// Displaying in listview
		listview.setAdapter(adapter);
		// Click event for single list row
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Making Listview hidden
				listview.setVisibility(View.GONE);
				// Making Imageview visible
				imageView.setVisibility(View.VISIBLE);
				// fetching the string form list
				TextView Url = (TextView) findViewById(R.id.title);
				final String path = Url.getText().toString();
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
				DownloadContentProvider.CONTENT_URI, null, null, null, null);
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

	public void QueryviaAsyncQueryHandler(View view) {
		// Making Listview visible
		listview.setVisibility(View.VISIBLE);
		// Making Imageview hidden
		imageView.setVisibility(View.GONE);

		QueryHandler myQueryHandler = new QueryHandler(DownloadActivity.this);
		// Begins the asynchronous query
		myQueryHandler.startQuery(0, null, DownloadContentProvider.CONTENT_URI,
				(String[]) null, (String) null, (String[]) null, (String) null);

		// Click event for single list row
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Making Listview hidden
				listview.setVisibility(View.GONE);
				// Making Imageview visible
				imageView.setVisibility(View.VISIBLE);
				// Fetching the data from list
				TextView Url = (TextView) findViewById(R.id.title);
				final String path = Url.getText().toString();
				// Calling the display function
				displayBitmap(path);
			}
		});
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
							final String[] projection = new String[] {
									IMAGE_Table.COLUMN_TIME,
									IMAGE_Table.COLUMN_DESCRIPTION };
							int[] disp = new int[] { R.id.time, R.id.title };
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
	}

	/*
	 * Reset the Image
	 */

	public void resetImage(View view) {
		imageView.setImageResource(R.drawable.drschmidt);
		Log.i("RESETIMAGE", "Reset the image. ");
	}

}
