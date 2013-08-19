package com.example.frequent_buyer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadingScreen extends Activity
{

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		


		Thread timer = new Thread()
		{

			public void run()
			{
				try
				{
					sleep(1000);
					/* 
					 * if the user details are saved(already logged in before) then
					 * automatically login
					 */
					UserFunctions userFunction = new UserFunctions();
					if(userFunction.isUserLoggedIn(getApplicationContext()))
					{
						/*
						 * get user details in order to know if he is the owner or the client
						 */
						staticParams.saveUserDetail(getApplicationContext());
						Intent activity;
						if(staticParams.userType.equals("client"))
						{
							activity = new Intent(LoadingScreen.this, BusinessList.class);
							startActivity(activity);
						}
						else
						{
							activity = new Intent(LoadingScreen.this, OwnerMenu.class);
							startActivity(activity);
						}
						// Close Loading Screen
						LoadingScreen.this.finish();	
					}
					else
					{
						Intent toOpen = new Intent(LoadingScreen.this, Login.class); 
						startActivity(toOpen);
						LoadingScreen.this.finish();
					}
				}
				catch(InterruptedException e)
				{

				}
			}

		};
		timer.start();
	}
	



}
