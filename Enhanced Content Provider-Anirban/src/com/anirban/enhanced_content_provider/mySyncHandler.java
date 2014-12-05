package com.anirban.enhanced_content_provider;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import com.anirban.Database.IMAGE_Table;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

class mySyncHandler extends Thread {

	IPC myCommData;
	private Context mycontext;
	private static final String TAG = "mySyncHandler";

	private ServerSocket ss;
	private Socket cs;

	private ObjectInputStream inputFromClient;
	private ObjectOutputStream outputToClient;
	public static final int SERVERPORT = 6000;

	ArrayList<String> clientStrings, serverStrings, getdifference;
	String addString, imagePath, urlPath;

	public mySyncHandler(Context context) {
		mycontext = context;
	}

	@Override
	public void run() {
		try {
			while (true) {
				ss = new ServerSocket(SERVERPORT);
				Log.v(TAG, "Listening");
				cs = ss.accept();
				Log.v(TAG, "Server interacted with Client");

				if (cs != null) {
					inputFromClient = new ObjectInputStream(cs.getInputStream());
					DownloadActivity.isConnected = true;

					while (true) {
						Log.d(TAG, "Reading mycommdata");
						// Retrieving the object sent from Client
						if (myCommData == null)
							myCommData = (IPC) inputFromClient.readObject();
						// Read mycommdata
						if (myCommData != null) {
							Log.d(TAG, "Fetching the Url Strings.. ");
							clientStrings = new ArrayList<String>();
							serverStrings = new ArrayList<String>();
							getdifference = new ArrayList<String>();

							// Fetching the Strings from emulator1
							Cursor cursor = mycontext.getContentResolver()
									.query(EnhancedContentProvider.CONTENT_URI,
											null, null, null, null);

							if (cursor != null) {
								cursor.moveToFirst();
								for (int i = 0; i < cursor.getCount(); i++) {
									addString = cursor
											.getString(cursor
													.getColumnIndexOrThrow(IMAGE_Table.COLUMN_URL));
									serverStrings.add(addString);
									cursor.moveToNext();
								}
							}

							// Fetching the Strings from emulator2
							for (int i = 0; i < myCommData.getUrlStrings()
									.size(); i++) {
								Log.d(TAG, myCommData.getUrlStrings().get(i));
								clientStrings.add(myCommData.getUrlStrings()
										.get(i));
							}

							// Checking the server and client string difference

							for (int i = 0; i < clientStrings.size(); i++) {
								if (!serverStrings.contains(clientStrings
										.get(i))) {
									getdifference.add(clientStrings.get(i));
									Log.e(TAG, addString);
								}
							}

							for (int i = 0; i < serverStrings.size(); i++) {
								if (!clientStrings.contains(serverStrings
										.get(i))) {
									myCommData.getdifferenceServer().add(
											serverStrings.get(i));
								}
							}

							// Downloading the different images
							for (int i = 0; i < getdifference.size(); i++) {
								urlPath = getdifference.get(i);
								imagePath = UtilityDownload.download(urlPath);

								if (imagePath != null) {
									ContentResolver cr = mycontext
											.getContentResolver();
									android.content.ContentValues values = new android.content.ContentValues();

									// Inserting the values in database
									values.put(IMAGE_Table.COLUMN_DESCRIPTION,
											imagePath);
									Date date = new Date();
									String ddate = (String) date.toString();
									Log.i("Date", ddate);
									values.put(IMAGE_Table.COLUMN_TIME, ddate);
									values.put(IMAGE_Table.COLUMN_URL, urlPath);
									Log.d(TAG, "Server: Inserting Values: "
											+ values);
									cr.insert(
											EnhancedContentProvider.CONTENT_URI,
											values);
								}
							}

							// Sending mycommData Object to Client
							outputToClient = new ObjectOutputStream(
									cs.getOutputStream());

							outputToClient.writeObject(myCommData);
							outputToClient.flush();

							// Closing the connections
							outputToClient.close();
							inputFromClient.close();
							cs.close();
							ss.close();

							myCommData = null;
							DownloadActivity.isConnected = false;
							// getting out of that loop
							break;
						} else {
							Log.d(TAG, "Please wait for few momments..");
							Thread.sleep(200);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
