package com.example.smalllibrary;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	
	private EditText etFirstName, etLastName, etPhoneNo, etEmail, etIDNO, etBirthday, etPassword, etConfirmPassword;
	private int mYear, mMonth, mDay;
	String today;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		init();
		findViews();
		setListeners();
	}
	
	private void init()
	{
		Calendar calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		today = sdfDateTime.format(calendar.getTime());
	}
	
	private void findViews()
	{
		etFirstName = (EditText)findViewById(R.id.editTextFirstName);
		etLastName 	= (EditText)findViewById(R.id.editTextLastName);
		etPhoneNo 	= (EditText)findViewById(R.id.editTextPhoneNo);
		etEmail 	= (EditText)findViewById(R.id.editTextEmail);
		etIDNO 		= (EditText)findViewById(R.id.editTextIDNO);
		etBirthday 	= (EditText)findViewById(R.id.editTextBirthday);
		etPassword 	= (EditText)findViewById(R.id.editTextPassword);
		etConfirmPassword = (EditText)findViewById(R.id.editTextConfirmPassword);
		
	}
	
	private void setListeners()
	{
		etBirthday.setOnTouchListener(etBirthdayListener);
	}
	
	private View.OnTouchListener etBirthdayListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				new DatePickerDialog(RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() 
	            {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
					{
						mYear = year;
						mMonth = monthOfYear;
						mDay = dayOfMonth;
						
						// Due to monthOfYear starts from 0
						int Month = monthOfYear + 1;
						etBirthday.setText(mYear+"-"+Month+"-"+mDay);
					}
				}, mYear,mMonth,mDay).show();
			}
			return false;
		}
	};
	
	// ButtonReset Method
	public void reset(View v)
	{
		etFirstName.setText("");
		etLastName.setText("");
		etPhoneNo.setText("");
		etEmail.setText("");
		etIDNO.setText("");
		etBirthday.setText("");
		etPassword.setText("");
		etConfirmPassword.setText("");
	}
	
	// ButtonRegister Method
	public void registration(View v)
	{
		// TODO Error Checking
			
		JSONObject jsonObj = new JSONObject();
		;
		try {
			jsonObj.put("L_firstName", etFirstName.getText());
			jsonObj.put("L_lastName", etLastName.getText());
			jsonObj.put("L_phoneNo", etPhoneNo.getText());
			jsonObj.put("L_email", etEmail.getText());
			jsonObj.put("L_IDNO", etIDNO.getText());
			jsonObj.put("L_birthday", etBirthday.getText());
			jsonObj.put("L_password", Generic.computeHash(etPassword.getText().toString()));
			
			new PostRegistration().execute(jsonObj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class PostRegistration extends AsyncTask<JSONObject, Void, String>{

		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(RegistrationActivity.this);
		
		@Override
		protected void onPreExecute() {			
			Dialog.setCancelable(true);
			Dialog.setTitle("Loading");
			Dialog.setMessage("Please wait...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(JSONObject... jsonObj) {
			String result = null;
			try
			{
				HttpPost httpPost = new HttpPost("http://piguloming.no-ip.org:90/api/LibraryUser/PostLibraryUser");
				// Convert JSONObject to JSON to String
				String json = jsonObj[0].toString();
				// Set json to StringEntity
				StringEntity se= new StringEntity(json);
				// Set httpPost Entity
				httpPost.setEntity(se);
				// Set some headers to inform server about the type of the content
				httpPost.setHeader("Content-Encoding", "UTF-8");
				httpPost.setHeader("Content-Type", "application/json");
				HttpResponse httpResponse = client.execute(httpPost);
				
				if(httpResponse.getStatusLine().getStatusCode() == 201)
				{
					result = "Success Registration! ";
					
				}
				else
				{
					result = httpResponse.getStatusLine().toString();
					result += " "+ EntityUtils.toString(httpResponse.getEntity());
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
			Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		if(menuItem.getItemId() == android.R.id.home)
			startActivity(new Intent(RegistrationActivity.this,MainActivity.class)); 
        return true;
    }

}
