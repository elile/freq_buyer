package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions 
{

	private JSONParser jsonParser;


	private static String loginURL = "http://eliproj1.site88.net/login.php";
	private static String registerURL = "http://eliproj1.site88.net/register.php";

	private static String login_tag = "login";
	private static String register_tag = "register";

	// constructor
	public UserFunctions()
	{
		jsonParser = new JSONParser();
	}

	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		return json;
	}

	/**
	 * function make Login Request
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String name, String email, String password)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		return json;
	}

	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCountUsers();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}

	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context)
	{
		staticParams.logoutUser();
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetLoginTable();
		return true;
	}

}