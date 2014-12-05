package com.anirban.enhanced_content_provider;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class UtilityDownload {

	/*
	 * Getting XML from URL making HTTP request
	 */

	public static String download(String src) {
		Log.i("Path", "Downloading... ");
		String path = null;
		// Save it external directory sdcard
		String filename = String.valueOf(src.hashCode()) + ".jpg";
		File output = new File(Environment.getExternalStorageDirectory(),
				filename);
		// If same named file exists , delete it
		if (output.exists()) {
			int i = 0;
			i = i + 1;
			filename = String.valueOf(src.hashCode()) + "_" + i + ".jpg";
			output = new File(Environment.getExternalStorageDirectory(),
					filename);
			Log.i("FileName", filename);
		}
		FileOutputStream fos = null;
		Bitmap bitmap = null;
		try {
			URL url = new URL(src);
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
				path = output.getAbsolutePath();
			} else {
				Log.e("INVALID", "INVALID..Error");
				path = "Failed";
			}
			Log.i("Path", path);
			// Return the output Path
			return path;
		}
		// If Image download failed
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(src, "INVALID URL..File not found");
			path = "Failed";
			return path;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(src, "INVALID URL..Please enter correct one");
			path = "Failed";
			return path;
		}

	}

}
