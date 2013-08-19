package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.R.string;

public class BusinessUsersFunction 
{
	
	private JSONParser jsonParser;
	
	private static String getCouponURL = "http://eliproj1.site88.net/getCoupon.php";
	private static String getConsumerClubListURL = "http://eliproj1.site88.net/getConsumerClubList.php";
	private static String setcouponURL = "http://eliproj1.site88.net/setCuopon.php";
	private static String substractCouponURL = "http://eliproj1.site88.net/subtractCuopon.php";
	private static String setCuopon_tag = "setCuopon";
	private static String getCoupon_tag = "getCoupon";
	private static String getConsumerClubList_tag = "getConsumerClubList";
	private static String substractCoupon_tag = "substractCoupon";
	
	
	public BusinessUsersFunction() 
	{
		jsonParser = new JSONParser();
	}
	
	/*
	 * get coupon number from the server
	 */
	public JSONObject getCoupon(String email, String businessName)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getCoupon_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("businessName", businessName));
		JSONObject json = jsonParser.getJSONFromUrl(getCouponURL, params);
		return json;
	}
	
	/*
	 * get consumer club of the owner business
	 */
	public JSONObject getConsumerClubList(String email)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", getConsumerClubList_tag));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(getConsumerClubListURL, params);
		return json;
	}
	
	/*
	 * Update consumer coupon
	 */
	public JSONObject substractCoupon(String email, String businessName)
	{
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", substractCoupon_tag));
		params.add(new BasicNameValuePair("email", email));	
		params.add(new BasicNameValuePair("business_name", businessName));
		JSONObject json = jsonParser.getJSONFromUrl(substractCouponURL, params);
		return json;
	}
	
	public JSONObject setCuopon(String email, String businessName,String NumToCopon, String Benefit)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", setCuopon_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("businessName", businessName));
		params.add(new BasicNameValuePair("setCoupon", NumToCopon));
		params.add(new BasicNameValuePair("benefit", Benefit));
		JSONObject json = jsonParser.getJSONFromUrl(setcouponURL , params);
		return json;
	}

}
