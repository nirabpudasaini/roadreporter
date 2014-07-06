package org.kll.roadreporter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_REPORTS = "reports";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_MINUTE = "minute";
	public static final String COLUMN_AMPM = "ampm";
	public static final String COLUMN_CATOGERY = "catogery";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LOCATION = "location";
	public static final String COLUMN_PHOTO = "photo_url";

	private static final String DATABASE_NAME = "reports.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_REPORTS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_TITLE
			+ " text not null ," + COLUMN_DESCRIPTION + " text not null ,"
			+ COLUMN_DATE + " text not null ," + COLUMN_HOUR
			+ " text not null ," + COLUMN_MINUTE + " text not null ,"
			+ COLUMN_AMPM + " text not null ," + COLUMN_CATOGERY
			+ " text not null ," + COLUMN_LATITUDE + " text not null ,"
			+ COLUMN_LONGITUDE + " text not null ," + COLUMN_LOCATION
			+ " text not null , " + COLUMN_PHOTO + " text not null);";

	public SqlLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Log.w(MySQLiteHelper.class.getName(),
		// "Upgrading database from version " + oldVersion + " to "
		// + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
		onCreate(db);
	}

}
