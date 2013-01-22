package com.example.frequent_buyer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class TabEvents extends Activity 
{

	JSONParser jsonParser;
	Button sendEvent;
	EditText subject,message;
	String string_subject,string_message,string_date;
	String business;
	String setevnt = "http://eliproj1.site88.net/setevent.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.seteventeli);
		Bundle b = getIntent().getExtras();
		string_date = b.getString("Date");
		subject = (EditText)findViewById(R.id.EliSubjectId);
		message = (EditText)findViewById(R.id.EliMesageId);
		sendEvent = (Button)findViewById(R.id.ElisendeventId);
		business = staticParams.businessName;
		jsonParser = new JSONParser();
		sendEvent.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Thread t1 = new Thread()
				{
					public void run() 
					{	
						string_subject = subject.getText().toString();
						string_message = message.getText().toString();
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("tag", "setevent"));
						params.add(new BasicNameValuePair("subject", string_subject));
						params.add(new BasicNameValuePair("message", string_message));
						params.add(new BasicNameValuePair("businessName", business));
						params.add(new BasicNameValuePair("date", string_date));
						jsonParser.getJSONFromUrl(setevnt, params);		
					}
				};
				t1.start();
				try {
					t1.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(),"message sent..",Toast.LENGTH_LONG).show();
			}
		} );
	}

	
}

