package org.kll.roadreporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.kll.roadreporter.database.DataSource;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportDetails extends Activity implements OnItemSelectedListener{

	static final int REQUEST_TAKE_PHOTO = 1;
	private EditText txt_title, txt_desc;
	private String mCurrentPhotoPath, mCatogery;
	private Uri outputFileUri;
	private DataSource database;
	private String[] data;
	private long id;
	
	private Spinner spin_cat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportdetails);

		Bundle b = this.getIntent().getExtras();
		data = b.getStringArray("DATA");
		id = b.getLong("ID");

		database = new DataSource(this);
		database.open();

		String title = data[0];
		String description = data[1];
		String photo_path = data[10];

		txt_title = (EditText) findViewById(R.id.edit_title);
		txt_title.setText(title);

		txt_desc = (EditText) findViewById(R.id.edit_desc);
		txt_desc.setText(description);

		showImage(photo_path);
		
		spin_cat = (Spinner) findViewById(R.id.spinner_catogery);
		spin_cat.setSelection(Integer.parseInt(data[6]) - 1);
		spin_cat.setOnItemSelectedListener(this);
		

		ImageButton takePhoto = (ImageButton) findViewById(R.id.btn_photo);
		Button updateReport = (Button) findViewById(R.id.btn_update);

		takePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startCameraToTakePhotoIntent();

			}

		});

		updateReport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEverythingOk()) {
					saveToDatabase();
				}
			}
		});

	}

	@Override
	protected void onPause() {
		database.close();
		super.onPause();
	}

	private boolean isEverythingOk() {

		if (txt_title.getText().toString().matches("")) {
			Toast.makeText(getApplicationContext(),
					R.string.title_error_notset, Toast.LENGTH_LONG).show();
			return false;
		}

		if (txt_desc.getText().toString().matches("")) {
			Toast.makeText(getApplicationContext(), R.string.desc_error_notset,
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;

	}

	private void saveToDatabase() {
		String[] report = prepareData();
		Log.i("DATA", report.toString());
		Log.i("ID", String.valueOf(id));
		Boolean sucess = database.updateRecord(id, report);
		Log.i("SUCESS", String.valueOf(sucess));
		if (sucess) {
			Intent i = new Intent(ReportDetails.this, SavedReports.class);
			Toast.makeText(this, "Report Sucessfully Updated",
					Toast.LENGTH_LONG).show();
			startActivity(i);
			finish();
		} else {
			Toast.makeText(
					this,
					"Problem Updating Database, Operation Could not be Completed",
					Toast.LENGTH_LONG).show();

		}
	}

	private String[] prepareData() {
		data[0] = txt_title.getText().toString();
		data[1] = txt_desc.getText().toString();
		data[6] = mCatogery;
		if (mCurrentPhotoPath != null) {
			data[10] = mCurrentPhotoPath;
		}
		return data;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.print("Activity Result");
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			System.out.print("Photo Taken and Result OK");
			if (outputFileUri != null) {
				Bitmap bitmap = null;
				try {
					GetImageThumbnail getImageThumbnail = new GetImageThumbnail();
					bitmap = getImageThumbnail
							.getThumbnail(outputFileUri, this);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				ImageView myImage = (ImageView) findViewById(R.id.last_photo);
				myImage.setImageBitmap(bitmap);
			}
		}
		// Log.i("ACTIVITYRESULT", mCurrentPhotoPath);

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

	private void showImage(String path) {
		File imgFile = new File(path);
		if (imgFile.exists()) {

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());
			ImageView myImage = (ImageView) findViewById(R.id.last_photo);
			myImage.setImageBitmap(myBitmap);
			myImage.setVisibility(View.VISIBLE);

		}
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
