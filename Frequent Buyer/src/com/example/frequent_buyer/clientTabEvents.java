package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class clientTabEvents extends Activity 
{

	String getallevents = "http://eliproj1.site88.net/getallevents.php";
	JSONParser jsonParser;
	JSONObject json;
	TextView myText = null;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eligetevent);
		jsonParser = new JSONParser();
		json = new JSONObject();
		
		Thread t1 = new Thread()
		{
			public void run() 
			{	
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("tag", "getallevents"));
				params.add(new BasicNameValuePair("businessName", staticParams.businessName));
				json = jsonParser.getJSONFromUrl(getallevents, params);		
			}
		};
		t1.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			JSONArray jarr = json.getJSONArray("events");
			LinearLayout lView = new LinearLayout(this);

			myText = new TextView(this);
			JSONObject in_jarr;
			String mess;
			String sub;
			String date;
			String to_screen = "";
			int b = 1;
			for (int i = jarr.length()-1; i>=0; i--) {
				in_jarr = (JSONObject) jarr.get(i);
				mess = in_jarr.getString("message");
				sub = in_jarr.getString("subject");
				date = in_jarr.getString("date");
				to_screen += date+") subject: " + sub + "\n" + "message : "+ mess + "\n\n";
				b++;
			}
			myText.setTextSize(30);
			myText.setText(to_screen);
			lView.addView(myText);
			lView.setBackgroundResource(R.drawable.background);
			setContentView(lView);
		} catch (JSONException e) {}

	}


}
