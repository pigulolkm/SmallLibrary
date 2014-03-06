package com.example.smalllibrary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateRuleActivity extends Activity {
	
	private EditText editTextBorrowingLimit;
	private EditText editTextRenewalLimit;
	private EditText editTextBorrowingPeriod;
	private EditText editTextReservationLimit;
	private EditText editTextOutDateFine;
	private String oriBorrowingLimit, oriRenewalLimit, oriBorrowingPeriod, oriReservationLimit, oriOutDateFine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_rule);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		getRulesFromDb();
		
		findViews();
	}
	
	private void findViews()
	{
		editTextBorrowingLimit = (EditText)findViewById(R.id.editTextBorrowingLimit);
		editTextRenewalLimit = (EditText)findViewById(R.id.editTextRenewalLimit);
		editTextBorrowingPeriod = (EditText)findViewById(R.id.editTextBorrowingPeriod);
		editTextReservationLimit = (EditText)findViewById(R.id.editTextReservationLimit);
		editTextOutDateFine = (EditText)findViewById(R.id.editTextOutDateFine);
	}
	
	public void updateRule(View v)
	{
		String url = Generic.serverurl + "Rules/PutRules/1";
		JSONObject jsonObj = new JSONObject();
		
		try {
			jsonObj.put("Rule_id", "1");
			jsonObj.put("Rule_borrowingLimit", editTextBorrowingLimit.getText().toString());
			jsonObj.put("Rule_renewalLimit", editTextRenewalLimit.getText().toString());
			jsonObj.put("Rule_borrowingPeriod", editTextBorrowingPeriod.getText().toString());
			jsonObj.put("Rule_reservationLimit", editTextReservationLimit.getText().toString());
			jsonObj.put("Rule_outDateFine", editTextOutDateFine.getText().toString());
			
			String[] params = new String[]{ url, jsonObj.toString() };
			new updateRules().execute(params);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void resetRule(View v)
	{
		setText();
	}
	
	private void setText()
	{
		editTextBorrowingLimit.setText(oriBorrowingLimit);
		editTextRenewalLimit.setText(oriRenewalLimit);
		editTextBorrowingPeriod.setText(oriBorrowingPeriod);
		editTextReservationLimit.setText(oriReservationLimit);
		editTextOutDateFine.setText(oriOutDateFine);
	}
	
	private void getRulesFromDb()
	{
		String url = Generic.serverurl + "Rules/GetRules/1";
		new getRules().execute(url);
	}
	
	private class getRules extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(UpdateRuleActivity.this);
		
		@Override
		protected void onPreExecute() {			
			Dialog.setCancelable(false);
			Dialog.setTitle("Loading");
			Dialog.setMessage("Please wait...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try
			{
				HttpGet httpGet = new HttpGet(params[0]);
				HttpResponse httpResponse = client.execute(httpGet);
				if(httpResponse.getStatusLine().getStatusCode() == 200)
				{
					result = EntityUtils.toString(httpResponse.getEntity());
				}
				else
				{
					result = httpResponse.getStatusLine().toString();
				}
				
			}
			catch(Exception e)
			{
				
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			Dialog.dismiss();
			try {
				JSONObject jsonObj = new JSONObject(result);
				oriBorrowingLimit = jsonObj.getString("Rule_borrowingLimit");
				oriRenewalLimit = jsonObj.getString("Rule_renewalLimit");
				oriBorrowingPeriod = jsonObj.getString("Rule_borrowingPeriod");
				oriReservationLimit = jsonObj.getString("Rule_reservationLimit");
				oriOutDateFine = jsonObj.getString("Rule_outDateFine");
				
				setText();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class updateRules extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(UpdateRuleActivity.this);
		
		@Override
		protected void onPreExecute() {			
			Dialog.setCancelable(true);
			Dialog.setTitle("Updating");
			Dialog.setMessage("Please wait...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try
			{
				HttpPut httpPut = new HttpPut(params[0]);
				String json = params[1];
				StringEntity se = new StringEntity(json);
				httpPut.setEntity(se);
				httpPut.setHeader("Content-Encoding", "UTF-8");
				httpPut.setHeader("Content-Type", "application/json");
				HttpResponse httpResponse = client.execute(httpPut);
				
				if(httpResponse.getStatusLine().getStatusCode() == 200)
				{
					result = EntityUtils.toString(httpResponse.getEntity());
				}
				else
				{
					result = httpResponse.getStatusLine().toString();
				}
				
			}
			catch(Exception e)
			{
				
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			Dialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_rule, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		if(menuItem.getItemId() == android.R.id.home)
			startActivity(new Intent(UpdateRuleActivity.this,MainActivity.class)); 
        return true;
    }

}
