package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class BusinessList extends Activity 
{



	// JSON Response node names
	private static String KEY_SUCCESS = "success";	
	public static final String KEY_BUSINESS_NAME = "name";
	public static final String KEY_BUSINESS_LOGO = "logo";
	
	BroadcastReceiver logout;

	// Progress Dialog when loading from the database
	ProgressDialog dialog;

	Button searchBtn, btn_logout;
	EditText inputSearch;
	TextView title;

	// Adapter that holds the data showed in the list
	BusinessListAdapter adapter;

	// String that the adapter takes the information from
	ArrayList<HashMap<String, String>> business = null;

	ListView businessList;

	ConnectionAsyncTask connect;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_list);

		connect = new ConnectionAsyncTask();

		businessList = (ListView) findViewById(R.id.list);

		searchBtn = (Button)findViewById(R.id.btn_search);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		title = (TextView) findViewById(R.id.txtTitle);
		searchBtn.setVisibility(View.VISIBLE);
		inputSearch = (EditText)findViewById(R.id.txt_search);
		



		// Listening Search button click
		searchBtn.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				searchBtn.setVisibility(View.INVISIBLE);
				btn_logout.setVisibility(View.INVISIBLE);
				title.setVisibility(View.INVISIBLE);
				inputSearch.setVisibility(View.VISIBLE);
			}
		});

		inputSearch.addTextChangedListener(new TextWatcher() 
		{
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) 
			{
				// When user changed the Text
				adapter.getFilter().filter(cs.toString());
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) 
			{
				// TODO Auto-generated method stub

			}

			public void afterTextChanged(Editable arg0) 
			{
				// TODO Auto-generated method stub
			}
		});


		// Listening to Logout button click
		btn_logout.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Logout and launch the login screen
				staticParams.clean();
				UserFunctions userFunctions = new UserFunctions();
				userFunctions.logoutUser(getApplicationContext());
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("com.package.ACTION_LOGOUT");
				sendBroadcast(broadcastIntent);
				Intent login = new Intent(getApplicationContext(), Login.class);
				startActivity(login);
				finish();
			}
		});
		
		/*
		 * register receiver to listen to logout click
		 */
		logout = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				finish();
			}
		};
	    IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction("com.package.ACTION_LOGOUT");
	    registerReceiver(logout, intentFilter);


		connect.execute(this);
	}

//	@Override
//	protected void onResume() 
//	{
//		super.onResume();
//		UserFunctions userFunction = new UserFunctions();
//		if(userFunction.isUserLoggedIn(getApplicationContext()) == false)
//			finish();
//	}

	private boolean checkSearchOpen()
	{
		if(inputSearch.getVisibility() == View.VISIBLE)
		{
			searchBtn.setVisibility(View.VISIBLE);
			btn_logout.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
			inputSearch.setVisibility(View.INVISIBLE);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() 
	{
		this.unregisterReceiver(logout);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() 
	{
		/*
		 * Show a dialog to confirm if the user want to exit from the application
		 */
		if(checkSearchOpen() == false)
		{
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Closing Frequent Buyer")
			.setMessage("Are you sure you want exit?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) {
					finish();    
				}

			})
			.setNegativeButton("No", null)
			.show();
		}
	}

	/*
	 * Thread that handles the communication with the server
	 * Get information about the business stored in the server
	 */
	private class ConnectionAsyncTask extends AsyncTask<Activity, Void, JSONObject> 
	{

		BusinessFunction businessFunction;
		Activity activity;

		@Override
		protected void onPreExecute() 
		{
			businessFunction = new BusinessFunction();
			dialog = ProgressDialog.show(BusinessList.this, "", "Loading...");
			dialog.show();
		}



		@Override
		protected JSONObject doInBackground(Activity... params) 
		{
			// Get all business that are saved on the server
			activity = params[0];
			JSONObject json = businessFunction.getBusinessList();
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) 
		{
			super.onPostExecute(json);

			try 
			{
				if (json.getString(KEY_SUCCESS) != null) 
				{
					String res = json.getString(KEY_SUCCESS);
					if(Integer.parseInt(res) == 1)
					{
						business = new ArrayList<HashMap<String,String>>();
						// business successfully retrived in
						// Store all business details in SQLite Database
						JSONArray json_business = json.getJSONArray("business");

						// Clear all previous data in database
						businessFunction.removeAllBusiness(getApplicationContext());
						insertBusinessListIntoAdapter(json_business);

						// push the data into the adapter and how it in the list
						adapter=new BusinessListAdapter(activity, business);  
						businessList.setAdapter(adapter);
						businessList.setCacheColorHint(0);
						businessList.setTextFilterEnabled(true);


						businessList.setOnItemClickListener(new OnItemClickListener() 
						{
							public void onItemClick(AdapterView<?> parent, View view,
									int position, long id)
							{
								// When clicked, open the business activity
								String name = ((TextView) view.findViewById(R.id.txtBusinessName)).getText().toString();
								staticParams.saveBusinessDetail(getApplicationContext(), name);
								Intent activity;
								if(staticParams.userType.equals("client") == true)
									activity = new Intent(BusinessList.this, ClientMenu.class);
								else
									activity = new Intent(BusinessList.this, OwnerMenu.class);
								startActivity(activity);
							}
						});

					}
					else
					{
						// Error in login
					}
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			if(dialog.isShowing()) 
			{
				dialog.dismiss();
			}
		}

		private ArrayList<HashMap<String,String>> insertBusinessListIntoAdapter(JSONArray json_business)
		{				
			try 
			{
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
				for(int i=0; i < json_business.length(); i++)
				{
					HashMap<String, String> businessdetail = new HashMap<String, String>();
					JSONObject jsonBusiness = json_business.getJSONObject(i);
					// Add all business to the table
					db.addBusiness(jsonBusiness);
					String name = jsonBusiness.getString(KEY_BUSINESS_NAME);
					String logo = jsonBusiness.getString(KEY_BUSINESS_LOGO);

					businessdetail.put(KEY_BUSINESS_NAME, name);
					businessdetail.put(KEY_BUSINESS_LOGO, logo);
					business.add(businessdetail);
				}
			}
			catch (JSONException e) {}
			return business;
		}
	}






}
