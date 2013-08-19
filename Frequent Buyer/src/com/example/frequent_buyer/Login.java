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

public class Login extends Activity 
{

	Button btnLogin;
	TextView txtLinkToRegister;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;

	ProgressDialog dialog;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_TYPE = "type";




	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// Importing all assets like buttons, text fields
		inputEmail = (EditText) findViewById(R.id.login_email);
		inputPassword = (EditText) findViewById(R.id.login_password);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		txtLinkToRegister = (TextView) findViewById(R.id.link_to_register);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				EmailValidator emailvalidator = new EmailValidator();
				if(emailvalidator.validate(email) == false)
				{
					if(is_password_empty(password))
						loginErrorMsg.setText("invalid email/password");
					else
						loginErrorMsg.setText("invalid email");
				}
				else if(is_password_empty(password))
					loginErrorMsg.setText("invalid password");
				else
				{
					loginErrorMsg.setText("");
					new ConnectionAsyncTask().execute(email, password);
				}

			}
		});

		// Link to Register Screen
		txtLinkToRegister.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View view) 
			{
				Intent register = new Intent(Login.this, Register.class);
				startActivity(register);
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

	/* 
	 * A thread to connect to the server to try to login
	 */
	private class ConnectionAsyncTask extends AsyncTask<String, Void, JSONObject> 
	{

		UserFunctions userFunction;

		@Override
		protected void onPreExecute() 
		{
			userFunction = new UserFunctions();
			dialog = ProgressDialog.show(Login.this, "", "Loading...");
			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... urls) 
		{	
			String email = urls[0];
			String password = urls[1];
			JSONObject json = userFunction.loginUser(email, password);
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
					loginErrorMsg.setText("Error connecting to the server, try again");
				}
				else if (json.getString(KEY_SUCCESS) != null) 
				{
					loginErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					if(Integer.parseInt(res) == 1)
					{
						// user successfully logged in
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_TYPE));
						staticParams.saveUserDetail(getApplicationContext());

						Intent activity;
						if(staticParams.userType.equals("client"))
						{
							activity = new Intent(Login.this, BusinessList.class);
							startActivity(activity);
						}
						else
						{
							activity = new Intent(Login.this, OwnerMenu.class);
							startActivity(activity);
						}


						if(dialog.isShowing()) 
						{
							dialog.dismiss();
						}
						// Close Login Screen
						Login.this.finish();
					}
					else
					{
						// Error in login
						loginErrorMsg.setText("Incorrect username/password");
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
