package com.example.frequent_buyer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ClientList extends Activity
{

	BroadcastReceiver logout;
	
	public static final String KEY_BUSINESS_NAME = "name";
	public static final String KEY_BUSINESS_EMAIL = "email";

	public String consumerName[] = null;
	public String consumerEmail[] = null;

	Activity activity;

	// Progress Dialog when loading from the database
	ProgressDialog dialog;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";

	ArrayAdapter<String> adapter;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_list);

		activity = this;
		list = (ListView) findViewById(R.id.list);
		

		String email = staticParams.userEmail;
		new ConnectionAsyncTask().execute(email);
		
		Button btn_logout = (Button)findViewById(R.id.btn_logout);
		
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
		super.onBackPressed();
		finish();
	}


	/*
	 * Thread that handles the communication with the server
	 * Get information about the consumer club clients
	 */
	private class ConnectionAsyncTask extends AsyncTask<String, Void, JSONObject> 
	{

		BusinessUsersFunction businessUsersFunction;

		@Override
		protected void onPreExecute() 
		{
			businessUsersFunction = new BusinessUsersFunction();
			dialog = ProgressDialog.show(ClientList.this, "", "Loading...");
			dialog.show();
		}



		@Override
		protected JSONObject doInBackground(String... params) 
		{
			// Get all business that are saved on the server
			String email = params[0];
			JSONObject json = businessUsersFunction.getConsumerClubList(email);
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
						// business successfully retrived in
						// Store all business details in SQLite Database
						JSONArray json_business = json.getJSONArray("user");

						consumerName = new String[json_business.length()];
						consumerEmail = new String[json_business.length()];
						
						insertconsumerClubListArray(json_business);

						// push the data into the adapter and how it in the list
						adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, consumerName);  

						list.setAdapter(adapter);
						list.setOnItemClickListener(new OnItemClickListener() 
						{
							public void onItemClick(AdapterView<?> parent, View view,
									int position, long id)
							{
								staticParams.saveConsumerDetail(consumerName[position], consumerEmail[position]);
								Intent TabCoupon = new Intent(ClientList.this, CouponChanger.class);
								startActivity(TabCoupon);
								finish();
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

		private void insertconsumerClubListArray(JSONArray json_business)
		{		
			try 
			{
				for(int i=0; i < json_business.length(); i++)
				{
					JSONObject jsonBusiness = json_business.getJSONObject(i);
					consumerName[i] = jsonBusiness.getString(KEY_BUSINESS_NAME);
					consumerEmail[i] = jsonBusiness.getString(KEY_BUSINESS_EMAIL);
				}
			}
			catch (JSONException e) {}
		}
	}


}
