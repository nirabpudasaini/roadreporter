package org.kll.roadreporter;

import java.util.List;

import org.kll.roadreporter.database.DataSource;
import org.kll.roadreporter.database.DatabaseModel;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SavedReports extends ListActivity implements
		AsyncTaskCompleteListener {

	private DataSource database;
	private DatabaseModel report;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportlist);
		database = new DataSource(this);
		database.open();
		List<DatabaseModel> allSavedReports = database.getAllReports();
		ArrayAdapter<DatabaseModel> adapter = new ArrayAdapter<DatabaseModel>(
				this, android.R.layout.simple_list_item_1, allSavedReports);
		setListAdapter(adapter);
		registerForContextMenu(getListView());

	}

	@Override
	protected void onPause() {
		database.close();
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();
		database.open();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		report = (DatabaseModel) getListAdapter().getItem(position);
		this.openContextMenu(l);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.context_delete:
			deleteReport();
			Toast.makeText(getBaseContext(), "Report Deleted",
					Toast.LENGTH_LONG).show();
			return true;
		case R.id.context_submit:
			submitReport();
			return true;
		case R.id.context_details:
			showDetailsActivity();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void showDetailsActivity() {

		Intent i = new Intent(this, ReportDetails.class);
		Bundle b = new Bundle();
		String[] data = prepareData();
		b.putStringArray("DATA", data);
		i.putExtras(b);
		startActivity(i);

	}

	private void deleteReport() {
		@SuppressWarnings("unchecked")
		ArrayAdapter<DatabaseModel> adapter = (ArrayAdapter<DatabaseModel>) getListAdapter();
		database.deleteRecord(report);
		adapter.remove(report);
		adapter.notifyDataSetChanged();

	}

	private void submitReport() {
		String[] data = prepareData();
		new SendData(SavedReports.this).execute(data);

	}

	public String[] prepareData() {

		String[] data = new String[11];
		// Title
		data[0] = report.getTitle();
		Log.i("TITLE@prepareDataSaved", data[0]);

		// Description
		data[1] = report.getDescription();
		Log.i("DESCRIPTION@prepareDataSaved", data[1]);

		// Date
		data[2] = report.getDate();
		Log.i("DATE@prepareDataSaved", data[2]);

		// Current hour
		data[3] = report.getHour();
		Log.i("HOUR@prepareDataSaved", data[3]);

		// Current minute
		data[4] = report.getMinute();
		Log.i("MINUTE@prepareDataSaved", data[4]);

		// Am or Pm
		data[5] = report.getAmpm();
		Log.i("AMPM@prepareDataSaved", data[5]);

		// Catogery
		data[6] = report.getCatogery();
		Log.i("CATOGERY@prepareDataSaved", data[6]);

		// Latitude
		data[7] = report.getLatitude();
		Log.i("LATITUDE@prepareDataSaved", data[7]);

		// Longitude
		data[8] = report.getLongitude();
		Log.i("LONGITUDE@prepareDataSaved", data[8]);

		// Name of Location
		data[9] = report.getLocation();
		Log.i("LOCATION@prepareDataSaved", data[9]);

		// Path of the photo
		data[10] = report.getPhoto_url();
		Log.i("PHOTOPATH@prepareDataSaved", data[10]);

		return data;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.listview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.delete_all) {

			// TODO
			if (getListAdapter().getCount() != 0) {
				for (int i = 0; i < getListAdapter().getCount(); i++) {
					report = (DatabaseModel) getListAdapter().getItem(i);
					deleteReport();
					Toast.makeText(getBaseContext(), "Report Deleted",
							Toast.LENGTH_LONG).show();
				}
			}

			return true;
		}
		if (id == R.id.submit_all) {
			// TODO
			if (getListAdapter().getCount() != 0) {
				for (int i = 0; i < getListAdapter().getCount(); i++) {
					report = (DatabaseModel) getListAdapter().getItem(i);
					submitReport();
				}
			}

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTaskComplete(boolean result) {
		if (result) {
			deleteReport();
			Toast.makeText(this, "Sucessfully submitted the Report",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Problem Submitting the Report",
					Toast.LENGTH_LONG).show();
		}

	}

}
