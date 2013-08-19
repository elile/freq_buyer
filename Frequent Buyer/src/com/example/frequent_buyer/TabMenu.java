package com.example.frequent_buyer;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
 

public class TabMenu extends Activity 
{
	
	BroadcastReceiver logout;
	
	EditText editEvents;
	Button btn_save, btn_logout, btn_edit;
	TextView title;
	ProgressDialog dialog;
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	public static final String KEY_BUSINESS_NAME = "name";
	public static final String KEY_BUSINESS_LOGO = "logo";
	public static final String KEY_BUSINESS_MENU = "menu";
	public static final String KEY_BUSINESS_EVENTS = "events";
    
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        

        // Dashboard Logout button
        btn_logout = (Button) findViewById(R.id.btn_logout);
        
        title = (TextView) findViewById(R.id.txtTitle);
        
        loadEvents();
        
		if(staticParams.userType.equals("owner"))
		{

			// Dashboard Edit button
			btn_edit = (Button) findViewById(R.id.btn_edit);
			btn_edit.setVisibility(View.VISIBLE);

			btn_save = (Button) findViewById(R.id.btn_save);

			// Dashboard Edit text
			editEvents = (EditText) findViewById(R.id.txt_edit);

			btn_edit.setOnClickListener(new OnClickListener() 
			{

				public void onClick(View v) 
				{
					btn_logout.setVisibility(View.INVISIBLE);
					btn_edit.setVisibility(View.INVISIBLE);
					title.setVisibility(View.INVISIBLE);
					btn_save.setVisibility(View.INVISIBLE);
					btn_save.setVisibility(View.VISIBLE);
					editEvents.setVisibility(View.VISIBLE);
				}
			});

			btn_save.setOnClickListener(new OnClickListener() 
			{

				public void onClick(View v) 
				{
					String link = editEvents.getText().toString();
					new ConnectionAsyncTask().execute(link);
				}
			});

		}
        
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
    
	private void loadEvents()
	{
 
        // Loader image - will be shown before loading image
        int loader = R.drawable.loader;
 
        // Imageview to show
        ImageView image = (ImageView) findViewById(R.id.image);
 
        // Image url
        String image_url = staticParams.businessMenu;
 
        // ImageLoader class instance
        ImageLoader imgLoader = new ImageLoader(getApplicationContext());
 
        // whenever you want to load an image from url
        // call DisplayImage function
        // url - image url to load
        // loader - loader image, will be displayed before getting image
        // image - ImageView
        imgLoader.DisplayImage(image_url, loader, image);
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
		if(btn_logout.getVisibility() == View.INVISIBLE)
		{
			btn_logout.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
			btn_edit.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.VISIBLE);
			btn_save.setVisibility(View.INVISIBLE);
			editEvents.setVisibility(View.INVISIBLE);
		}
		else
			finish();
	}
	
	/* 
	 * A thread to connect to the server to try to update the menu
	 */
	private class ConnectionAsyncTask extends AsyncTask<String, Void, JSONObject> 
	{

		BusinessFunction businessFunction;

		@Override
		protected void onPreExecute() 
		{
			businessFunction = new BusinessFunction();
			dialog = ProgressDialog.show(TabMenu.this, "", "Loading...");
			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) 
		{	
			String link = params[0];
			JSONObject json = businessFunction.changeMenu(link, staticParams.businessName);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) 
		{
			super.onPostExecute(json);

			try 
			{
				if(json == null)
				{
					// ERROR
				}
				else if (json.getString(KEY_SUCCESS) != null) 
				{
					String res = json.getString(KEY_SUCCESS);
					if(Integer.parseInt(res) == 1)
					{

						JSONObject json_business = json.getJSONObject("business");
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						db.updateBusiness(json_business);
						String events = json_business.getString(KEY_BUSINESS_EVENTS);
						staticParams.businessEvents = events;


						loadEvents();
						if(dialog.isShowing()) 
						{
							dialog.dismiss();
						}
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

