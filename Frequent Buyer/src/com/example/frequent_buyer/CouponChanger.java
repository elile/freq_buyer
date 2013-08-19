package com.example.frequent_buyer;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CouponChanger extends Activity
{
	EditText newNumToCopon,newBenefit;
	Button setCopon,subtract ;
	String Benefit ;
	String NumToCopon;
	String email = staticParams.consumerEmail;
	String businessName = staticParams.businessName;
	ProgressDialog dialog ;
	JSONObject json;
	BusinessUsersFunction BFunction = new BusinessUsersFunction();
	int a = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coponchanger);
		newBenefit = (EditText)findViewById(R.id.setbenefitcoponchangerID);
		newNumToCopon = (EditText)findViewById(R.id.setnumcoponchangerID);
		setCopon = (Button)findViewById(R.id.setcoponbuttunID);
		subtract = (Button)findViewById(R.id.subtractButtunID);

		setCopon.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				Thread t1 = new Thread()
				{
					public void run() 
					{	
						NumToCopon = newNumToCopon.getText().toString();
						Benefit = newBenefit.getText().toString();
						json = BFunction.setCuopon(email, businessName,
								NumToCopon, Benefit);
					}
				};
				t1.start();
				try {
					t1.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					subtract.setEnabled(true);
					Toast.makeText(getApplicationContext(),"you set coupon to "+
							json.getString("0").toString()+" and set benefit to "+json.getString("1").toString(),
							Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		subtract.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Thread t2 = new Thread()
				{
					public void run() 
					{
						json = BFunction.substractCoupon(email, businessName);
					}
				};
				t2.start();
				try {
					t2.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				try {
					a = json.getInt("coupon");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (a>0) {
					Toast.makeText(getApplicationContext(),
							"Number of coupon left to " + email + " is " +a,
							Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(getApplicationContext(),
							"Number of coupon left to " + email + " is " +a+" and you cant sub again please set new number",
							Toast.LENGTH_LONG).show();
					subtract.setEnabled(false);
				}
			}
		});

	}

}
