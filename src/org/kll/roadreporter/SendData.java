package org.kll.roadreporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class SendData extends AsyncTask<String, Integer, Boolean> {

	private Activity activity;
	private ProgressDialog mProgressDialog;
	private Context context;
	private AsyncTaskCompleteListener callback;

	public SendData(Context c) {
		this.context = c;
		Activity a = (Activity) c;
		this.activity = a;
		this.callback = (AsyncTaskCompleteListener) a;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage("Uploading your report, Please have Patience");
		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		callback.onTaskComplete(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected Boolean doInBackground(String... params) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(
				"http://kathmandulivinglabs.org/roadreport/api");

		// get data to make post
		String title = params[0];
		String desc = params[1];
		String date = params[2];
		String hour = params[3];
		String minute = params[4];
		String ampm = params[5];
		String catogery = params[6];
		String latitude = params[7];
		String longitude = params[8];
		String location_name = params[9];
		String photo_url = params[10];
		String task = "report";

		// Building post parameters, key and value pair
		// for name
		// 1. id of the user 'id'
		// 2. name of the user 'name'
		// 3.latitude 'X'
		// 4.longitude 'Y'
		// 5.accuracy of gps 'accuracy'
		// 6.timestamp 'timestamp'

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("incident_title", title));
		nameValuePair.add(new BasicNameValuePair("incident_description", desc));
		nameValuePair.add(new BasicNameValuePair("incident_date", date));
		nameValuePair.add(new BasicNameValuePair("incident_hour", hour));
		nameValuePair.add(new BasicNameValuePair("incident_minute", minute));
		nameValuePair.add(new BasicNameValuePair("incident_ampm", ampm));
		nameValuePair
				.add(new BasicNameValuePair("incident_category", catogery));
		nameValuePair.add(new BasicNameValuePair("latitude", latitude));
		nameValuePair.add(new BasicNameValuePair("longitude", longitude));
		nameValuePair
				.add(new BasicNameValuePair("location_name", location_name));
		nameValuePair
				.add(new BasicNameValuePair("incident_photo[]", photo_url));
		nameValuePair.add(new BasicNameValuePair("task", task));

		try {
			MultipartEntityBuilder multiPartEntityBuilder = MultipartEntityBuilder
					.create();

			for (int index = 0; index < nameValuePair.size(); index++) {
				if (nameValuePair.get(index).getName()
						.equalsIgnoreCase("incident_photo[]")) {
					// If the key equals to photo, we use FileBody to transfer
					// the data
					Bitmap bitmapOrg = BitmapFactory.decodeFile(nameValuePair
							.get(index).getValue());
				    ByteArrayOutputStream bao = new ByteArrayOutputStream();
				    bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 60, bao);
				    byte[] data = bao.toByteArray();
				    multiPartEntityBuilder.addPart(nameValuePair.get(index)
							.getName(), new ByteArrayBody(data, "photo.jpeg"));
				    
//					multiPartEntityBuilder.addPart(nameValuePair.get(index)
//							.getName(), new FileBody(new File(nameValuePair
//							.get(index).getValue())));
				} else {
					// Normal string data
					multiPartEntityBuilder.addPart(nameValuePair.get(index)
							.getName(), new StringBody(nameValuePair.get(index)
							.getValue()));
				}
			}

			httpPost.setEntity(multiPartEntityBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost, localContext);

			HttpEntity response_entity = response.getEntity();
			System.out.println("Entity:" + response_entity);
			if (response_entity != null) {
				String responseBody = EntityUtils.toString(response_entity);
				String responsemessage = responseBody.toString();
				Log.i("DATA RESPONSE", responsemessage);

			}

		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			return false;
		}

		return true;	
		
	}
}