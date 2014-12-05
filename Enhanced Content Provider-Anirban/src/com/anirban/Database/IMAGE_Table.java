package com.anirban.Database;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * Defining the table in Database
 * to store the related data of the enhanced contentprovider
 */
public class IMAGE_Table {

	// Database table
	public static final String TABLE_IMAGE = "images";
	public static final String COLUMN_ID = "_ID";
	public static final String COLUMN_TIME = "downloadtime";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_URL = "urlpath";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " + TABLE_IMAGE
			+ "(" + "_id INTEGER PRIMARY KEY, " + COLUMN_TIME + " TEXT,"
			+ COLUMN_DESCRIPTION + " TEXT," + COLUMN_URL + " TEXT" + " )";

	public static void onCreate(SQLiteDatabase database) {
		Log.i("DATABASE", "Creating Database");
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(IMAGE_Table.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
		onCreate(database);
	}
}
