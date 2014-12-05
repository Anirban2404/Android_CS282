package com.anirban.contentprovider;

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
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.anirban.contentprovider.AsyncDisplay;
import com.anirban.contentprovider.DownloadContentProvider;
import com.anirban.contentprovider.IMAGE_Table;

/*
 * Download Bound Service for ASYNC Model
 */
public class DownloadService extends Service {

	public String outputPath = "urlPath";

	private int i = 0;
	// communicate with AIDL using IPC
	protected final AsyncScript.Stub ibinder = new AsyncScript.Stub() {
		// The method of AIDL interface is overridden here
		public void downloadImage(String urlPath, AsyncDisplay callback) {
			Log.i("Async", "Executing Async AIDL..");
			outputPath = download(urlPath);
			Log.i("AsyncOutput", outputPath);
			ContentResolver cr = getContentResolver();
			android.content.ContentValues values = new android.content.ContentValues();
			// SimpleDateFormat dateFormat = new
			// SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String ddate = (String) date.toString();
			Log.i("Date", ddate);

			values.put(IMAGE_Table.COLUMN_DESCRIPTION, outputPath);
			values.put(IMAGE_Table.COLUMN_TIME, ddate);
			Log.i("ValuestoInsert", "Inserting Values" + values);
			cr.insert(DownloadContentProvider.CONTENT_URI, values);
			try {
				Log.i("Async", "Executing Callback..");
				Log.i("Async", outputPath);
				String i = callback.toString();
				Log.i("Async", i);
				// Displaying the image to the imageView by
				// returning the filename back to DownloadActivity as a callback
				Log.i("URI", values.toString());
				callback.executeDisplay(values.toString());
			} catch (RemoteException e) {
				Log.i("Async", "Exception in Callback..");
				e.printStackTrace();
			}
			Log.i("Path", outputPath);
		}
	};

	protected String download(String urlPath) {
		Log.i("Path", "Downloading... ");

		// Save it external directory sdcard
		String filename = String.valueOf(urlPath.hashCode()) + ".jpg";
		File output = new File(Environment.getExternalStorageDirectory(),
				filename);
		// If same named file exists , delete it
		if (output.exists()) {
			i = i + 1;
			filename = String.valueOf(urlPath.hashCode()) + "_" + i + ".jpg";
			output = new File(Environment.getExternalStorageDirectory(),
					filename);
			Log.i("FileName", filename);
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		Bitmap bitmap = null;
		try {
			URL url = new URL(urlPath);
			Log.d("URL Connnected", "Link establised");
			// Prepare a request object
			HttpUriRequest request = new HttpGet(url.toString());
			// Creates a new HTTP client from parameters and a connection
			// manager
			HttpClient httpClient = new DefaultHttpClient();
			// Execute the request
			HttpResponse response = httpClient.execute(request);
			// Examine the response status
			StatusLine statusLine = response.getStatusLine();
			// Checking for the status code
			int statusCode = statusLine.getStatusCode();
			if (200 == statusCode) {
				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				// Read the contents of an entity and return it as a byte array
				byte[] bytes = EntityUtils.toByteArray(entity);
				Log.i("image", "Image downloading..");
				// Decode an immutable bitmap from the specified byte array
				bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				// Creates an output file stream to write to the file
				// and getting the name of that path
				fos = new FileOutputStream(output.getPath());
				Log.i("image", "Image Compressing..");
				// Compress the bitmap image to set in ImageView
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				// Flushing output on a buffered stream
				fos.flush();
				fos.close();
				Log.i("completed", "Download Complete.. ");
			} else {
				Log.e("INVALID", "INVALID..File not found");
				Toast.makeText(this, "INVALID URL..File not found",
						Toast.LENGTH_SHORT).show();
			}
		}
		// If Image download failed
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(urlPath, "INVALID..File not found");
			Toast.makeText(this, "Error while downloading image.",
					Toast.LENGTH_SHORT).show();
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
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// Return binder to the Activity
		Log.i("IBinder", "Returning Async IBinder..");
		return ibinder;
	}
}