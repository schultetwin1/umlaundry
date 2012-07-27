package com.schultetwins.umlaundry.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UMLaundryDatabase extends SQLiteOpenHelper {
	private static final String TAG = "UMLaundryDatabase";
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "laundry_data";
	
	// Only have a table for Machines -- for now
	public static final String TABLE_MACHINES        = "machines";
	public static final String ID					 = "_ID";
	public static final String COLUMN_MACHINE_ID     = "machine_id";
	public static final String COLUMN_MACHINE_ROOM   = "machine_room";
	public static final String COLUMN_MACHINE_STATUS = "machine_status";
	public static final String COLUMN_MACHINE_TIME   = "machine_time";
	public static final String COLUMN_LAST_UPDATED   = "last_updated";
	
	private static final String CREATE_MACHINE_TABLE = "CREATE TABLE " + TABLE_MACHINES
			+ " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + COLUMN_MACHINE_ID + " INTEGER, "
			+ COLUMN_MACHINE_ROOM + "TEXT NOT NULL" + COLUMN_MACHINE_STATUS +  " TEXT NOT NULL"
			+ COLUMN_MACHINE_TIME + "INTEGER NOT NULL" + COLUMN_LAST_UPDATED 
			+ "TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);";
	
	

	public UMLaundryDatabase(Context context) {
		super(context, DB_NAME, null /* Factory for Cursors */, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_MACHINE_TABLE);
		// JUST FOR TESTING!! REMOVE WHEN ITS WORKING!!
		db.execSQL("INSERT INTO " + TABLE_MACHINES + " 1, 1, 'In Use', 40");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database. Existing contents being deleted");
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_MACHINES);
		onCreate(db);
	}

}
