package com.example.frequent_buyer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClientMenu extends Activity 
{

	BroadcastReceiver logout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_menu_layout);

		/**
		 * Creating all buttons instances
		 * */

		// Dashboard Menu button
		Button btn_menu = (Button) findViewById(R.id.btn_client_menu);

		// Dashboard Messages button
		Button btn_messages = (Button) findViewById(R.id.btn_client_messages);

		// Dashboard Events button
		Button btn_events = (Button) findViewById(R.id.btn_client_events);

		// Dashboard Coupons button
		Button btn_coupons = (Button) findViewById(R.id.btn_client_coupon);
		
		// Dashboard Logout button
		Button btn_logout = (Button) findViewById(R.id.btn_logout);

		/**
		 * Handling all button click events
		 * */

		// Listening Menu button click
		btn_menu.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Launching Menu Screen
				Intent activity = new Intent(ClientMenu.this, TabMenu.class);
				startActivity(activity);
			}
		});

//		// Listening Messages button click
//		btn_messages.setOnClickListener(new View.OnClickListener() 
//		{
//
//			@Override
//			public void onClick(View view) 
//			{
//				// Launching Messages Screen
//				Intent i = new Intent(getApplicationContext(), MessagesActivity.class);
//				startActivity(i);
//			}
//		});

		// Listening to Events button click
		btn_events.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Launching Events Screen
				Intent activity = new Intent(ClientMenu.this, clientTabEvents.class);
				startActivity(activity);
			}
		});

		// Listening to Coupons button click
		btn_coupons.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				// Launching Coupon Screen
				Intent activity = new Intent(ClientMenu.this, TabCoupon.class);
				startActivity(activity);
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
		
	}
	
	@Override
	protected void onDestroy() 
	{
		this.unregisterReceiver(logout);
		super.onDestroy();
	}
	

	
	
}