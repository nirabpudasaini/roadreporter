package org.kll.roadreporter.adapters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.kll.roadreporter.GetImageThumbnail;
import org.kll.roadreporter.R;
import org.kll.roadreporter.database.DataSource;
import org.kll.roadreporter.database.DatabaseModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SavedReportListAdapter extends BaseAdapter {

	private Context mContext;
	private DataSource mDatabase;
	private List<DatabaseModel> mReports;

	public SavedReportListAdapter(Context c) {

		mContext = c;
		mDatabase = new DataSource(mContext);
		mDatabase.open();
		mReports = mDatabase.getAllReports();
		mDatabase.close();

	}

	@Override
	public int getCount() {
		return mReports.size();
	}

	@Override
	public Object getItem(int position) {
		return mReports.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mReports.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.saved_reports_row, parent, false);
		TextView title = (TextView) row.findViewById(R.id.list_row_title);
		TextView latlong = (TextView) row.findViewById(R.id.list_row_location);
		TextView date = (TextView) row.findViewById(R.id.list_row_time);
		ImageView image = (ImageView) row.findViewById(R.id.list_row_image);

		DatabaseModel tempobj = mReports.get(position);

		title.setText(tempobj.getTitle());
		latlong.setText("Lat:" + tempobj.getLatitude() + " " + "Lon:"
				+ tempobj.getLongitude());
		date.setText(tempobj.getDate());

		Bitmap bitmap = null;
		Uri uri = Uri.parse("file://" + tempobj.getPhoto_url());
        try {
            GetImageThumbnail getImageThumbnail = new GetImageThumbnail();
            bitmap = getImageThumbnail.getThumbnail(uri, mContext);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
		image.setImageBitmap(bitmap);

		return row;
	}

	public void remove(DatabaseModel report) {
		
		mReports.remove(report);
		
	}

}
