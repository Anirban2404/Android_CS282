/**
 * 
 */
package com.anirban.boundedservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.anirban.boundedservice.AsyncDisplay;

/**
 * @author Anirban Bhattacharjee
 * 
 */

/*
 * Download Bound Service for ASYNC Model
 */
public class DownloadBoundServiceAsync extends Service {

	public static String outputPath = "urlPath";
	private static String TAG = "";

	// communicate with AIDL using IPC
	protected final AsyncScript.Stub ibinder = new AsyncScript.Stub() {
		// The method of AIDL interface is overridden here
		public void downloadScript(String urlPath, AsyncDisplay callback) {
			Log.i("Async", "Executing Async AIDL..");
			outputPath = download(urlPath);
			try {
				Log.i("Async", "Executing Callback..");
				Log.i("Async", outputPath);
				String i = callback.toString();
				Log.i("Async", i);
				// Displaying the image to the imageView by
				// returning the filename back to DownloadActivity as a callback
				callback.executeDisplay(outputPath);
			} catch (RemoteException e) {
				Log.i("Async", "Exception in Callback..");
				e.printStackTrace();
			}
			Log.i("TAG", outputPath);
		}
	};

	protected String download(String urlPath) {
		Log.i("TAG", "Downloading... ");
		// Save it external directory sdcard
		String filename = "image.jpg";
		File output = new File(Environment.getExternalStorageDirectory(),
				filename);
		// If same named file exists , delete it
		if (output.exists()) {
			output.delete();
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		Bitmap bitmap = null;
		try {
			URL url = new URL(urlPath);
			Log.d(TAG, "Link establised");
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
				Log.i("TAG", "Download Complete.. ");
			} else {
				Log.e(TAG, "INVALID..File not found");
			}
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
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// Return binder to the Activity
		Log.i("TAG", "Returning Async IBinder..");
		return ibinder;
	}
}