package com.anirban.enhanced_content_provider;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import java.io.IOException;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.anirban.Database.IMAGE_Table;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/*
 * Download Bound Service for ASYNC Model
 */
public class DownloadService extends Service {

	public String outputPath = "urlPath";

	// communicate with AIDL using IPC
	protected final AsyncScript.Stub ibinder = new AsyncScript.Stub() {
		// The method of AIDL interface is overridden here
		public void downloadImage(String urlPath, AsyncDisplay callback) {
			Log.i("Async", "Executing Async AIDL..");
			// Connect to the website and get the html
			try {
				Document doc = Jsoup.connect(urlPath).get();
				// Get all elements with img tag ,
				Elements img = doc.getElementsByTag("img");
				for (Element el : img) {
					// for each element get the srs url
					String src = el.absUrl("src");
					Log.i("Image Found!", "src attribute is : " + src);
					outputPath = UtilityDownload.download(src);
					Log.e("PathName", outputPath);
					// outputPath= download(urlPath);
					Log.i("AsyncOutPutpath", outputPath);
					if (!outputPath.equals("Failed")) {
						Log.i("AsyncOutput", outputPath);
						ContentResolver cr = getContentResolver();
						android.content.ContentValues values = new android.content.ContentValues();
						// SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date = new Date();
						String ddate = (String) date.toString();
						Log.i("Date", ddate);
						// Inserting the values in database
						values.put(IMAGE_Table.COLUMN_DESCRIPTION, outputPath);
						values.put(IMAGE_Table.COLUMN_TIME, ddate);
						values.put(IMAGE_Table.COLUMN_URL, src);
						Log.i("ValuestoInsert", "Inserting Values" + values);
						cr.insert(EnhancedContentProvider.CONTENT_URI, values);
						try {
							Log.i("Async", "Executing Callback..");
							Log.i("Async", outputPath);
							String i = callback.toString();
							Log.i("Async", i);
							// Displaying the image to the imageView by
							// returning the filename back to DownloadActivity
							// as a callback
							Log.i("URI", values.toString());
							callback.executeDisplay(values.toString());
						} catch (RemoteException e) {
							Log.i("Async", "Exception in Callback..");
							e.printStackTrace();
						}
						Log.i("Path", outputPath);
					} else {
						try {
							Log.i("Failed", "Failed to Download.." + outputPath);
							callback.executeDisplay(outputPath);
						} catch (RemoteException e) {
							Log.i("RemoteException", "Failed to Download.."
									+ outputPath);
							e.printStackTrace();
						}
					}
				}
			} catch (IOException ex) {
				Log.e("DownloadServiceError:", DownloadService.class.getName());
				try {
					callback.executeDisplay("Failed");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};

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