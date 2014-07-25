package org.kll.roadreporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.kll.roadreporter.database.DataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements AsyncTaskCompleteListener, OnItemSelectedListener {

	private EditText edittext_title, edittext_desc;
	private Button btn_save, btn_submit;
	private ImageView last_photo;
	private ImageButton btn_photo;
	private Spinner spin_cat;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location currentLocation;
	private String mCatogery;
	static final int REQUEST_TAKE_PHOTO = 1;
	private String mCurrentPhotoPath;
	private DataSource database;
	private Uri outputFileUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edittext_title = (EditText) findViewById(R.id.edit_title);
		edittext_desc = (EditText) findViewById(R.id.edit_desc);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_photo = (ImageButton) findViewById(R.id.btn_photo);
		last_photo = (ImageView) findViewById(R.id.last_photo);
		spin_cat = (Spinner) findViewById(R.id.spinner_catogery);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 2, locationListener);
		database = new DataSource(this);
		database.open();
		
		spin_cat.setOnItemSelectedListener(this);

		btn_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isEverythingOk()) {
					saveToDatabase();
				}
				

			}
		});

		btn_submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEverythingOk()) {
					sendData();
				}

			}
		});

		btn_photo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startCameraToTakePhotoIntent();

			}
		});

	}

	@Override
	protected void onPause() {
		locationManager.removeUpdates(locationListener);
		database.close();
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 2, locationListener);
		database.open();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.print("Activity Result");
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			System.out.print("Photo Taken and Result OK");
			if (outputFileUri != null) {
				Bitmap bitmap = null;
	            try {
	                GetImageThumbnail getImageThumbnail = new GetImageThumbnail();
	                bitmap = getImageThumbnail.getThumbnail(outputFileUri, this);
	            } catch (FileNotFoundException e1) {
	                e1.printStackTrace();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
				last_photo.setImageBitmap(bitmap);
				last_photo.setVisibility(View.VISIBLE);
			}
		}
		//Log.i("ACTIVITYRESULT", mCurrentPhotoPath);

	}


	private boolean isEverythingOk() {

		// this method will do housekeeping like cheking gps status, cheking is
		// fields are filled, is photo taken, will return true if everything is
		// Ok

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
			return false;
		}

		if (edittext_title.getText().toString().matches("")) {
			Toast.makeText(getApplicationContext(),
					R.string.title_error_notset, Toast.LENGTH_LONG).show();
			return false;
		}

		if (edittext_desc.getText().toString().matches("")) {
			Toast.makeText(getApplicationContext(), R.string.desc_error_notset,
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (last_photo.getVisibility() == View.INVISIBLE) {
			Toast.makeText(getApplicationContext(),
					"Make sure you have taken the photo", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		if (currentLocation != null) {
			if (currentLocation.getAccuracy() > 20.0) {
				Toast.makeText(getApplicationContext(),
						R.string.gps_error_nofix, Toast.LENGTH_LONG).show();
				return false;
			} else {
				return true;

			}
		} else {
			Toast.makeText(getApplicationContext(), R.string.gps_error_nofix,
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public String[] prepareData() {

		String[] report = new String[11];
		// Title
		report[0] = edittext_title.getText().toString();
		//Log.i("TITLE@prepareData", report[0]);

		// Description
		report[1] = edittext_desc.getText().toString();
		//Log.i("DESCRIPRION@prepareData", report[1]);

		// Date
		report[2] = getDate();
		//Log.i("DATE@prepareData", report[2]);

		// Current hour
		report[3] = getHour();
		//Log.i("HOUR@prepareData", report[3]);

		// Current minute
		report[4] = getMinute();
		//Log.i("MINUTE@prepareData", report[4]);

		// Am or Pm
		report[5] = getAmPm();
		//Log.i("AMPM@prepareData", report[5]);

		// Catogery
		report[6] = mCatogery;
		//Log.i("CATOGERY@prepareData", report[6]);

		// Latitude
		report[7] = String.valueOf(currentLocation.getLatitude());
		//Log.i("LATITUDE@prepareData", report[7]);

		// Longitude
		report[8] = String.valueOf(currentLocation.getLongitude());
		//Log.i("LONGITUDE@prepareData", report[8]);

		// Name of Location
		report[9] = "mobile app";
		//Log.i("LOCATION@prepareData", report[9]);

		// Path of the photo
		report[10] = mCurrentPhotoPath;
		//Log.i("PHOTOPATH@prepareData", report[10]);

		return report;

	}

	private void saveToDatabase() {
		String[] report = prepareData();
		database.createRecord(report);
		clearForm();
		Toast.makeText(this, "Report Sucessfully Saved", Toast.LENGTH_LONG)
				.show();
	}

	private void sendData() {

		String[] report = prepareData();

		new SendData(MainActivity.this).execute(report);
	}

	private void startCameraToTakePhotoIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

			File file = getOutputMediaFile();
			outputFileUri = Uri.fromFile(file);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			mCurrentPhotoPath = file.getAbsolutePath(); 
			startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

		} else {
			Toast.makeText(getBaseContext(), "No Camera App Found",
					Toast.LENGTH_LONG).show();
		}
	}

	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"RoadReporter");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

	private void clearForm() {
		edittext_title.setText("");
		edittext_desc.setText("");
		
		last_photo.setVisibility(View.INVISIBLE);
	}

	private String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		String formattedDate = format.format(new Date());
		return formattedDate;
	}

	private String getHour() {
		SimpleDateFormat format = new SimpleDateFormat("hh", Locale.US);
		String formattedHour = format.format(new Date());
		return formattedHour;
	}

	private String getMinute() {
		SimpleDateFormat format = new SimpleDateFormat("mm", Locale.US);
		String formattedMinute = format.format(new Date());
		return formattedMinute;
	}

	private String getAmPm() {
		SimpleDateFormat format = new SimpleDateFormat("aa", Locale.US);
		String formattedMinute = format.format(new Date());
		return formattedMinute.toLowerCase(Locale.US);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.gps_error_off)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.saved_reports) {
			Intent i = new Intent(this, SavedReports.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTaskComplete(boolean result) {
		if (result) {
			Toast.makeText(this, "Sucessfully submitted the Report",
					Toast.LENGTH_LONG).show();
			clearForm();
		} else {
			Toast.makeText(this, "Problem Submitting the Report Data",
					Toast.LENGTH_LONG).show();
		}

	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (isBetterLocation(location, currentLocation)) {
				currentLocation = location;
			}

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if(position == 0){
			mCatogery = "1";
		} 
		else if(position == 1){
			mCatogery = "2";			
		}
		else if(position == 2){
			mCatogery = "3";
			
		}		

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

}
