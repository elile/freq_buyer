package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class BusinessFunction 
{

	
	private JSONParser jsonParser;
	
	private static String business_tag = "business";
	private static String ownerBusiness_tag = "ownerBusiness";
	private static String updateEvents_tag = "updateEvents";
	
	private static String getBusinessURL = "http://eliproj1.site88.net/getBusiness.php";
	private static String getOwnersBusinessURL = "http://eliproj1.site88.net/getOwnersBusiness.php";
	private static String changeBusinessEventsURL = "http://eliproj1.site88.net/updateEvents.php";
	private static String changeMenuEventsURL = "http://eliproj1.site88.net/updateMenu.php";
	
	
	
	// constructor
	public BusinessFunction()
	{
		jsonParser = new JSONParser();
	}
	
	public JSONObject getBusinessList()
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", business_tag));
		JSONObject json = jsonParser.getJSONFromUrl(getBusinessURL, params);
		return json;
	}
	
	/*
	 * Removes all the data stored in the sql lite on the business
	 */
	public void removeAllBusiness(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetBusinessTable();
	}
	
	/*
	 * Check if there are any business stored in sql lite
	 */
	public boolean businessAvailable(Context context)
	{
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCountBusiness();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	public JSONObject getOwnersBusiness(String email)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", ownerBusiness_tag));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(getOwnersBusinessURL, params);
		return json;
	}
	
	public JSONObject changeEvents(String Events, String name)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateEvents_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("events", Events));
		JSONObject json = jsonParser.getJSONFromUrl(changeBusinessEventsURL, params);
		return json;
	}
	
	public JSONObject changeMenu(String menu, String name)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", updateEvents_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("menu", menu));
		JSONObject json = jsonParser.getJSONFromUrl(changeMenuEventsURL, params);
		return json;
	}
	
	

	
	
	
}
