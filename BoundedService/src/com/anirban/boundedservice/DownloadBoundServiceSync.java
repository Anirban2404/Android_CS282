/**
 * @author Anirban Bhattacharjee
 *
 */
package com.anirban.boundedservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import android.util.Log;

/*
 * Download Bound Service for SYNC Model
 */

public class DownloadBoundServiceSync extends Service {
	public static final String FILENAME = "fileName";
	public static final String URLPATH = "urlPath";
	private static String TAG = "";

	// communicate with AIDL using IPC
	private final SyncScript.Stub binder = new SyncScript.Stub() {
		// The method of AIDL interface is overridden here
		public String syncScript(String urlPath, String fileName) {
			Log.i("TAG", "Executing SyncAIDL..");
			return download(urlPath, fileName);
		}
	};

	/*
	 * Downloading the Image from web
	 */
	protected String download(final String urlPath, final String fileName) {
		Log.i("TAG", "Downloading... ");
		// Save it external directory sdcard
		final File output = new File(Environment.getExternalStorageDirectory(),
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
			Log.d(TAG, "Link establised");
			// Fetching http response body as a string
			// Prepare a request object
			HttpUriRequest request = new HttpGet(url.toString());
			// Creates a new HTTP client from parameters and a connection
			// manager
			HttpClient httpClient = new DefaultHttpClient();
			// Execute the request
			HttpResponse response = httpClient.execute(request);
			// Examine the response status
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			// Checking for the status code
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
				// If file not downloaded because of statuscode error
				Log.e(TAG, "INVALID..File not found");
			}
		}
		// If Image download failed
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(urlPath, "INVALID..File not found");
		}
		// If URI is not maintaing the proper protocol or the string could not
		// be parsed
		catch (MalformedURLException e) {
			Log.e(urlPath, "INVALID URL!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Extra care if anything slipped out
		finally {
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
		// Return the output Path of the downloaded image
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
		Log.i("TAG", "Returning Sync IBinder..");
		return binder;
	}
}
