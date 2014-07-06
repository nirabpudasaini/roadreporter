package org.kll.roadreporter.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource {

	private SQLiteDatabase mDatabase;
	private SqlLiteHelper mSqlhelper;
	private String[] allcolumns = { SqlLiteHelper.COLUMN_ID,SqlLiteHelper.COLUMN_TITLE,
			SqlLiteHelper.COLUMN_DESCRIPTION, SqlLiteHelper.COLUMN_DATE,
			SqlLiteHelper.COLUMN_HOUR, SqlLiteHelper.COLUMN_MINUTE,
			SqlLiteHelper.COLUMN_AMPM, SqlLiteHelper.COLUMN_CATOGERY,
			SqlLiteHelper.COLUMN_LATITUDE, SqlLiteHelper.COLUMN_LONGITUDE,
			SqlLiteHelper.COLUMN_LOCATION, SqlLiteHelper.COLUMN_PHOTO };

	public DataSource(Context context){
		mSqlhelper = new SqlLiteHelper(context);		
	} 
	
	public void open() throws SQLException{
		mDatabase = mSqlhelper.getWritableDatabase();
	}
	
	public void close(){
		mSqlhelper.close();
	}
	
	public DatabaseModel createRecord(String[] report){
		
		ContentValues values = new ContentValues();
		for (int i = 0 ; i < (allcolumns.length - 1); i++ ){
			values.put(allcolumns[i+1], report[i]);
		}
		long insertId = mDatabase.insert(SqlLiteHelper.TABLE_REPORTS, null,
		        values);
	    Cursor cursor = mDatabase.query(SqlLiteHelper.TABLE_REPORTS,
	            allcolumns, SqlLiteHelper.COLUMN_ID + " = " + insertId, null,
	            null, null, null);
	        cursor.moveToFirst();
	        DatabaseModel newReport = cursorToReport(cursor);
	        cursor.close();
		return newReport;
		
	}
	
	public void deleteRecord(DatabaseModel record){
		long id = record.getId();
		Log.i("Report deleted with id: " ,String.valueOf(id));
		mDatabase.delete(SqlLiteHelper.TABLE_REPORTS, SqlLiteHelper.COLUMN_ID
        + " = " + id, null);
	}
	
	public List<DatabaseModel> getAllReports(){
		
		List<DatabaseModel> reports = new ArrayList<DatabaseModel>();
		
	    Cursor cursor = mDatabase.query(SqlLiteHelper.TABLE_REPORTS,
	            allcolumns, null, null, null, null, null);

	        cursor.moveToFirst();
	        while (!cursor.isAfterLast()) {
	          DatabaseModel report = cursorToReport(cursor);
	          reports.add(report);
	          cursor.moveToNext();
	        }
	        // make sure to close the cursor
	        cursor.close();
		
		
		return reports;
		
	}
	
	
	public DatabaseModel cursorToReport(Cursor cursor){
		DatabaseModel report = new DatabaseModel();
		report.setId(cursor.getLong(0));
		report.setTitle(cursor.getString(1));
		report.setDescription(cursor.getString(2));
		report.setDate(cursor.getString(3));
		report.setHour(cursor.getString(4));
		report.setMinute(cursor.getString(5));
		report.setAmpm(cursor.getString(6));
		report.setCatogery(cursor.getString(7));
		report.setLatitude(cursor.getString(8));
		report.setLongitude(cursor.getString(9));
		report.setLocation(cursor.getString(10));
		report.setPhoto_url(cursor.getString(11));
		return report;
	}
	
}
