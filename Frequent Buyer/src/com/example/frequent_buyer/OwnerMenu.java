package com.example.frequent_buyer;

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
import android.view.View;
import android.widget.Button;

public class OwnerMenu extends Activity 
{

	BroadcastReceiver logout;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";

	private static final String KEY_NAME = "name";
	private static final String KEY_LOGO = "logo";
	private static final String KEY_MENU = "menu";
	private static final String KEY_EVENTS = "events";

	// Progress Dialog when loading from the database
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.owner_menu_layout);

		/**
		 * Creating all buttons instances
		 * */

		// Dashboard Menu button
		Button btn_menu = (Button) findViewById(R.id.btn_update_menu);

		// Dashboard Events button
		Button btn_events = (Button) findViewById(R.id.btn_update_events);

		// Dashboard Coupons button
		Button btn_coupons = (Button) findViewById(R.id.btn_update_coupon);

		// Dashboard Logout button
		Button btn_logout = (Button) findViewById(R.id.btn_logout);

		// Listening to Coupons button click
		btn_coupons.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Launching Coupon Screen
				Intent activity = new Intent(OwnerMenu.this, ClientList.class);
				startActivity(activity);
			}
		});
		
		// Listening to Menu button click
		btn_menu.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Launching Menu Screen
				Intent activity = new Intent(OwnerMenu.this, TabMenu.class);
				startActivity(activity);
			}
		});

		// Listening to Events button click
		btn_events.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Launching Coupon Screen
				//Intent activity = new Intent(OwnerMenu.this, TabEvents.class);
				//startActivity(activity);
				startActivity(new Intent(OwnerMenu.this,SimpleCalendarViewActivity.class));
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


		new ConnectionAsyncTask().execute();
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



	/*
	 * Thread that handles the communication with the server
	 * Get information about the business stored in the server
	 */
	private class ConnectionAsyncTask extends AsyncTask<Void, Void, JSONObject> 
	{

		BusinessFunction businessFunction;

		@Override
		protected void onPreExecute() 
		{
			businessFunction = new BusinessFunction();
			dialog = ProgressDialog.show(OwnerMenu.this, "", "Loading...");
			dialog.show();
		}



		@Override
		protected JSONObject doInBackground(Void... params) 
		{
			// Get all business that are saved on the server
			String email = staticParams.userEmail;
			JSONObject json = businessFunction.getOwnersBusiness(email);
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
						// user successfully logged in
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_business = json.getJSONObject("business");

						db.addBusiness(json_business);
						String businessName = json_business.getString(KEY_NAME);
						String businessLogo = json_business.getString(KEY_LOGO);
						String businessMenu = json_business.getString(KEY_MENU);
						String businessEvents = json_business.getString(KEY_EVENTS);

						staticParams.saveBusinessDetail(businessName, businessLogo, businessMenu, businessEvents);
						if(dialog.isShowing()) 
						{
							dialog.dismiss();
						}

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


	}



}
