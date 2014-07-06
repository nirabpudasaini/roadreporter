package org.kll.roadreporter;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportDetails extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportdetails);
		
		Bundle b = this.getIntent().getExtras();
		String[] data = b.getStringArray("DATA");
		
		String title = data[0];
		String description = data[1];
		String photo_path = data[10];
		
		TextView txt_title = (TextView) findViewById(R.id.details_title);
		txt_title.setText(title);
		
		TextView txt_desc = (TextView) findViewById(R.id.details_description);
		txt_desc.setText(description);
		
		showImage(photo_path);
		
		
	}
	
	private void showImage(String path){
		File imgFile = new  File(path);
		if(imgFile.exists()){

		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

		    ImageView myImage = (ImageView) findViewById(R.id.details_image);
		    myImage.setImageBitmap(myBitmap);
		    myImage.setVisibility(View.VISIBLE);

		}
	}

}
