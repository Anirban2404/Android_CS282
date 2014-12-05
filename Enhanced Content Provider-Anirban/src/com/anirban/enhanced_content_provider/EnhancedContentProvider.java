package com.anirban.enhanced_content_provider;

/**
 * @author
 * Anirban Bhattacharjee
 * 
 */

import java.util.Arrays;
import java.util.HashSet;

import com.anirban.Database.IMAGE_Table;
import com.anirban.Database.Image_Helper;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EnhancedContentProvider extends ContentProvider {
	// database
	private Image_Helper database;
	public static final int IMAGE = 100;
	private static final int IMAGE_ID = 110;
	private static final String BASE_PATH = "images";
	private static final String AUTHORITY = "com.anirban.enhanced_content_provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/" + BASE_PATH;

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/" + BASE_PATH;

	private static final UriMatcher matcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		matcher.addURI(AUTHORITY, BASE_PATH, IMAGE);
		matcher.addURI(AUTHORITY, BASE_PATH + "/#", IMAGE_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int uriType = matcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case IMAGE:
			rowsDeleted = sqlDB.delete(IMAGE_Table.TABLE_IMAGE, selection,
					selectionArgs);
			break;
		case IMAGE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(IMAGE_Table.TABLE_IMAGE,
						IMAGE_Table.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(IMAGE_Table.TABLE_IMAGE,
						IMAGE_Table.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		Log.i("Content Provider", "Insering in Database");
		int uriType = matcher.match(uri);
		if (matcher.match(uri) != IMAGE) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case IMAGE:
			id = sqlDB.insert(IMAGE_Table.TABLE_IMAGE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		database = new Image_Helper(getContext(), null, null, 0);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Log.i("Content Provider", "Query firing in Database");
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection);
		queryBuilder.setTables(IMAGE_Table.TABLE_IMAGE);
		int uriType = matcher.match(uri);
		switch (uriType) {
		case IMAGE:
			break;
		case IMAGE_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(IMAGE_Table.COLUMN_ID + "="
					+ uri.getPath());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	/*
	 * (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		int uriType = matcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case IMAGE:
			rowsUpdated = sqlDB.update(IMAGE_Table.TABLE_IMAGE, values,
					selection, selectionArgs);
			break;
		case IMAGE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(IMAGE_Table.TABLE_IMAGE, values,
						IMAGE_Table.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(IMAGE_Table.TABLE_IMAGE, values,
						IMAGE_Table.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	/*
	 *  Check the columns to sync 
	 */
	private void checkColumns(String[] projection) {
		String[] available = { IMAGE_Table.COLUMN_DESCRIPTION,
				IMAGE_Table.COLUMN_TIME, IMAGE_Table.COLUMN_ID,
				IMAGE_Table.COLUMN_URL };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
