package com.anirban.contentprovider;

/**
 * @Author
 * Anirban Bhattacharjee
 * I've written the codes by abiding by the Honor Code.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Image_Helper extends SQLiteOpenHelper {

	public Image_Helper(Context context, String name, CursorFactory factor,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static final String DATABASE_NAME = "imagepath_table.db";
	private static final int DATABASE_VERSION = 2;

	// Method is called during creation of the database
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		IMAGE_Table.onCreate(database);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		IMAGE_Table.onUpgrade(database, oldVersion, newVersion);
	}

}
