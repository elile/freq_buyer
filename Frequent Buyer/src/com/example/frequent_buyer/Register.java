package com.example.frequent_buyer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends Activity
{

	Button btnRegister;
	TextView btnLinkToLogin;
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	TextView registerErrorMsg;
	
	ProgressDialog dialog;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_TYPE = "type";
	

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.reg_fullname);
		inputEmail = (EditText) findViewById(R.id.reg_email);
		inputPassword = (EditText) findViewById(R.id.reg_password);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (TextView) findViewById(R.id.link_to_login);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);

		btnRegister.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				String fullname = inputFullName.getText().toString();
				EmailValidator emailvalidator = new EmailValidator();
				if(emailvalidator.validate(email) == false)
				{
					if(is_password_empty(password))
						registerErrorMsg.setText("invalid email/password");
					else
						registerErrorMsg.setText("invalid email");
				}
				else if(password.equals(""))
					registerErrorMsg.setText("invalid password");
				else
				{
					registerErrorMsg.setText("");
					new ConnectionAsyncTask().execute(email, password, fullname);
				}
			}
		});

		btnLinkToLogin.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View arg0) 
			{
				Intent login = new Intent(Register.this, Login.class);
				startActivity(login);
				finish();
			}
		});
	}
	
	private boolean is_password_empty(String password)
	{
		if(password == null)
			return true;
		if(password.equals(""))
			return true;
		return false;
	}

	private class ConnectionAsyncTask extends AsyncTask<String, Void, JSONObject> 
	{

		UserFunctions userFunction;

		protected void onPreExecute() 
		{
			userFunction = new UserFunctions();
			dialog = ProgressDialog.show(Register.this, "", "Loading...");
			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) 
		{	
			String email = urls[0];
			String password = urls[1];
			String fullname = urls[2];
			UserFunctions userFunction = new UserFunctions();
			JSONObject JSON = userFunction.registerUser(fullname, email, password);
			return JSON;
		}

		@Override
		protected void onPostExecute(JSONObject json) 
		{
			super.onPostExecute(json);
			try 
			{
				if(json == null)
				{
					registerErrorMsg.setText("Error connecting to the server, try again");
				}
				else if (json.getString(KEY_SUCCESS) != null) 
				{
					registerErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					if(Integer.parseInt(res) == 1)
					{
						// user successfully registered
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_TYPE));
						staticParams.saveUserDetail(getApplicationContext());

						Intent businessMenu = new Intent(Register.this, BusinessList.class);
						startActivity(businessMenu);


				        if(dialog.isShowing()) 
				        {
				            dialog.dismiss();
				        }

						// Close Login Screen
						Register.this.finish();
					}
					else if(json.getString(KEY_ERROR) != null)
					{
						res = json.getString(KEY_ERROR);
						if(Integer.parseInt(res) == 1)
						{
							// Error in register
							registerErrorMsg.setText("Error occured in registration");
						}
						else if(Integer.parseInt(res) == 2)
						{
							// User already exist
							registerErrorMsg.setText("user already exist");	
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
