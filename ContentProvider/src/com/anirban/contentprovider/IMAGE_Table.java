package com.anirban.contentprovider;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class IMAGE_Table {

	// Database table
	public static final String TABLE_IMAGE = "images";
	public static final String COLUMN_ID = "_ID";
	public static final String COLUMN_TIME = "downloadtime";
	public static final String COLUMN_DESCRIPTION = "description";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " + TABLE_IMAGE
			+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_TIME
			+ " TEXT," + COLUMN_DESCRIPTION + " TEXT" + " )";

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
