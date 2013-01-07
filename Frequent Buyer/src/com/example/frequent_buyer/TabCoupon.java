package com.example.frequent_buyer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;


public class TabCoupon extends Activity 
{

	ProgressDialog dialog;
	JSONObject backFromServer;
	TextView number_lest_to_benefit;
	TextView text_of_benefit;
	String email = staticParams.userEmail;
	String business = staticParams.businessName;
	String benefit_name;
	int num_lest_to_benefit;
	ProgressDialog WaitView;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon);
		number_lest_to_benefit = (TextView)findViewById(R.id.number_to_coupon_id);
		text_of_benefit = (TextView)findViewById(R.id.benefit_text_id);
		
		// get from server
		get_number_lest_to_benefit(email, business);

		staticParams.userCounterToBenefit = num_lest_to_benefit;
		changeScreen();

	}

	// get from the server 
	private void get_number_lest_to_benefit(String user_email , String business_selected)
	{
		final BusinessUsersFunction UsersFunction = new BusinessUsersFunction();
		//WaitView = ProgressDialog.show(TabCoupon.this, "", "Loading...");
		//WaitView.show();
		Thread toRun = new Thread()
		{
			public void run()
			{
				backFromServer = UsersFunction.getCoupon(email, business);
			}
		};toRun.start();
		try {
			
			toRun.join();
		} catch (InterruptedException e) {}
		//WaitView.dismiss();
		try {
			benefit_name = 		backFromServer.getString("Benefit") ;
			num_lest_to_benefit = backFromServer.getInt("coupon");
			// if it return false or junk there is no line with username in business_users
		} catch (JSONException e) {}
	}
	
	private void changeScreen() 
	{
		String changeScreen = Integer.toString( num_lest_to_benefit);
		//seting screen
		number_lest_to_benefit.setText(changeScreen);
		text_of_benefit.setText(benefit_name);		
	}

//	private void showJson(JSONObject backFromServer) 
//	{
//		AlertDialog.Builder alertbox2 = new AlertDialog.Builder(this);
//		alertbox2.setTitle("jeson");
//		try {
//			alertbox2.setMessage(backFromServer.toString(1));
//		} catch (JSONException e) {alertbox2.setMessage(e.toString());}
//		alertbox2.setPositiveButton("ok", new DialogInterface.OnClickListener()
//		{
//			public void onClick(DialogInterface arg0, int arg1) {}
//		});
//		alertbox2.show();		
//	}

}

