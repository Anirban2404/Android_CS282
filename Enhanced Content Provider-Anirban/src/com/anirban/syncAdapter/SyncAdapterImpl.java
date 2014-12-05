package com.anirban.syncAdapter;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import android.content.ContentResolver;

import com.anirban.Database.IMAGE_Table;
import com.anirban.enhanced_content_provider.DownloadActivity;
import com.anirban.enhanced_content_provider.EnhancedContentProvider;
import com.anirban.enhanced_content_provider.IPC;
import com.anirban.enhanced_content_provider.UtilityDownload;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapterImpl extends AbstractThreadedSyncAdapter {

	private static final String TAG = "SyncAdapterImpl";
	private Context myContext;
	private String imagePath;
	private String urlPath;

	// Java Networking thing declaration
	private ObjectOutputStream outputToClient;
	private ObjectInputStream inputFromClient;

	private static Socket socket;
	private String serverIpAddress = "10.0.2.2";

	// PORT 5000 GET REDIRECTED TO THE SERVER EMULATOR'S
	// PORT 6000
	// - telnet localhost 5554
	// - redir add tcp:6000:6000
	private static final int REDIRECTED_SERVERPORT = 6000;

	boolean isClientConnected = false;

	public SyncAdapterImpl(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		myContext = context;
		AccountManager.get(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts
	 * .Account, android.os.Bundle, java.lang.String,
	 * android.content.ContentProviderClient, android.content.SyncResult)
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(TAG, "In onPerformSync: ");
		Log.d(TAG, "isClientConnected: " + isClientConnected);
		if (isClientConnected == false) {
			try {
				socket = new Socket(serverIpAddress, REDIRECTED_SERVERPORT);
				isClientConnected = true;

				outputToClient = new ObjectOutputStream(
						socket.getOutputStream());

				ContentResolver contentResolver = myContext
						.getContentResolver();
				Log.d(TAG, "ON_performSync: " + account.toString());

				Cursor cursor = contentResolver.query(
						EnhancedContentProvider.CONTENT_URI, null, null, null,
						null);

				IPC mycommData = new IPC();
				String diffPath;

				// adding the different urls
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						while (cursor.moveToNext()) {
							diffPath = cursor
									.getString(cursor
											.getColumnIndexOrThrow(IMAGE_Table.COLUMN_URL));
							Log.d(TAG, "Added: " + diffPath);
							mycommData.getUrlStrings().add(diffPath);

						}
						;
					}
				}

				cursor.close();
				Log.d(TAG, "Sending to Server..");
				outputToClient.reset();
				outputToClient.flush();
				Log.v(TAG, outputToClient.toString());
				outputToClient.writeObject(mycommData);
				Log.v(TAG, mycommData.toString());
				outputToClient.flush();

				//Fetching the url in ClientSide
				inputFromClient = new ObjectInputStream(socket.getInputStream());
				mycommData = (IPC) inputFromClient.readObject();
				Log.v(TAG, mycommData.toString());
				 /*
				  * For the different urls downloading the images
				  * 				  
				  */
				
				for (int i = 0; i < mycommData.getdifferenceServer().size(); i++) {
					urlPath = mycommData.getdifferenceServer().get(i);
					imagePath = UtilityDownload.download(urlPath);
					
					if (imagePath != null) {
						
						Log.d(TAG, imagePath);
						// Inserting the values in client database
						android.content.ContentValues values = new android.content.ContentValues();
						Date date = new Date();
						String ddate = (String) date.toString();						
						values.put(IMAGE_Table.COLUMN_DESCRIPTION, imagePath);
						values.put(IMAGE_Table.COLUMN_TIME, ddate);
						values.put(IMAGE_Table.COLUMN_URL, urlPath);
						Log.d(TAG, "Inserting Values: " + values);
						contentResolver.insert(
								EnhancedContentProvider.CONTENT_URI, values);
					}
				}
				
				outputToClient.flush();
				inputFromClient.close();
				outputToClient.close();
				socket.close();
				isClientConnected = false;
				DownloadActivity.isConnected = false;
				Log.i(TAG,
						"Synchronization is successful..");
				mycommData = null;
			} catch (UnknownHostException e) {
				Log.e(TAG, outputToClient.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, outputToClient.toString());
				e.printStackTrace();
			} catch (Exception e) {
				Log.e(TAG, outputToClient.toString());
				e.printStackTrace();
			} finally {
				try {
					Log.d(TAG, "Finally Done");
					socket.close();
					isClientConnected = false;
					DownloadActivity.isConnected = false;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
