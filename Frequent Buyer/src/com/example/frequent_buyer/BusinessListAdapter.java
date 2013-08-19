package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class BusinessListAdapter extends BaseAdapter implements Filterable
{
 
	ArrayList<HashMap<String, String>> mOriginalValues; // Original Values

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader; 

	public BusinessListAdapter(Activity a, ArrayList<HashMap<String, String>> d) 
	{
		activity = a;
		data=d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() 
	{
		return data.size();
	}

	public Object getItem(int position) 
	{
		return position;
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View vi=convertView;
		if(convertView==null)
			vi = inflater.inflate(R.layout.list_row, null);

		TextView title = (TextView)vi.findViewById(R.id.txtBusinessName); // title
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

		HashMap<String, String> businessDetails = new HashMap<String, String>();
		businessDetails = data.get(position);

		// Setting all values in listview
		title.setText(businessDetails.get(BusinessList.KEY_BUSINESS_NAME));
		imageLoader.DisplayImage(businessDetails.get(BusinessList.KEY_BUSINESS_LOGO), R.drawable.loader, thumb_image);
		return vi;
	}

	public Filter getFilter() 
	{
		Filter filter = new Filter() 
		{

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,FilterResults results) 
			{

				data = (ArrayList<HashMap<String, String>>) results.values; // has the filtered values
				notifyDataSetChanged();  // notifies the data with new filtered values
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) 
			{
				FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
				ArrayList<HashMap<String, String>> FilteredArrList = new ArrayList<HashMap<String, String>>();

				if (mOriginalValues == null) 
				{
					mOriginalValues = new ArrayList<HashMap<String, String>>(data); // saves the original data in mOriginalValues
				}

				/********
				 * 
				 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
				 *  else does the Filtering and returns FilteredArrList(Filtered)  
				 *
				 ********/
				if (constraint == null || constraint.length() == 0) 
				{
					// set the Original result to return  
					results.count = mOriginalValues.size();
					results.values = mOriginalValues;
				} 
				else 
				{
					constraint = constraint.toString().toLowerCase(Locale.getDefault());
					for (int i = 0; i < mOriginalValues.size(); i++) 
					{
						String data1 = mOriginalValues.get(i).get(BusinessList.KEY_BUSINESS_NAME);
						if (data1.toLowerCase(Locale.getDefault()).startsWith(constraint.toString())) 
						{
							FilteredArrList.add(mOriginalValues.get(i));
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}
				return results;
			}
		};
		return filter;
	}
}
