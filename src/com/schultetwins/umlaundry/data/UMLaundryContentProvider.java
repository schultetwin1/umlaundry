package com.schultetwins.umlaundry.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class UMLaundryContentProvider extends ContentProvider {
	
	// Should be moved to a string
	private static final String AUTHORITY = "com.schultetwins.umlaundry.data.UMLaundryContentProvider";
	private static final String BUILDINGS_PATH = "buildings";
	private static final String ROOMS_PATH = "rooms";
	private static final int ROOM_ID = 100;
	private static final int MACHINE_ID = 110;
	
	private UMLaundryDatabase db;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
	    sURIMatcher.addURI(AUTHORITY, BUILDINGS_PATH + "/" + ROOMS_PATH + "/*", ROOM_ID);
	    sURIMatcher.addURI(AUTHORITY, BUILDINGS_PATH + "/" + ROOMS_PATH + "/#", MACHINE_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		db = new UMLaundryDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(UMLaundryDatabase.TABLE_MACHINES);
		
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case(ROOM_ID):
			queryBuilder.appendWhere(UMLaundryDatabase.COLUMN_MACHINE_ROOM + "="
	                + uri.getLastPathSegment());
			break;
			
		case (MACHINE_ID):
			queryBuilder.appendWhere(UMLaundryDatabase.COLUMN_MACHINE_ID + "=" 
					+ uri.getLastPathSegment());
			queryBuilder.appendWhere(UMLaundryDatabase.COLUMN_MACHINE_ROOM + "=" 
					+ uri.getPathSegments().get(-2));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}
		Cursor cursor = queryBuilder.query(db.getReadableDatabase(),
	            projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
