package org.kll.roadreporter;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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
		
		EditText txt_title = (EditText) findViewById(R.id.edit_title);
		txt_title.setText(title);
		
		EditText txt_desc = (EditText) findViewById(R.id.edit_desc);
		txt_desc.setText(description);
		
		showImage(photo_path);
		
		ImageButton takePhoto = (ImageButton) findViewById(R.id.btn_photo);
		Button updateReport = (Button) findViewById(R.id.btn_update);
		
		takePhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		updateReport.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
	}
	
	private void showImage(String path){
		File imgFile = new  File(path);
		if(imgFile.exists()){

		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    ImageView myImage = (ImageView) findViewById(R.id.last_photo);
		    myImage.setImageBitmap(myBitmap);
		    myImage.setVisibility(View.VISIBLE);

		}
	}

}
