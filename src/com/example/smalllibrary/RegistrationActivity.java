package com.example.smalllibrary;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

public class RegistrationActivity extends Activity {
	
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etPhoneNo;
	private EditText etEmail;
	private EditText etIDNO;
	private EditText etBirthday;
	private EditText etPassword;
	private int mYear, mMonth, mDay;
	String today;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
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
						etBirthday.setText(mDay+"-"+Month+"-"+mYear);
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
	}
	
	// ButtonRegister Method
	public void registration(View v)
	{
		// TODO Error Checking
		// TODO Password Encrytion
			
		JSONObject json = new JSONObject();
		
		try {
			json.put("L_firstName", etFirstName.getText());
			json.put("L_lastName", etLastName.getText());
			json.put("L_phoneNo", etPhoneNo.getText());
			json.put("L_email", etEmail.getText());
			json.put("L_IDNO", etIDNO.getText());
			json.put("L_birthday", etBirthday.getText());
			json.put("L_password", etPassword.getText());
			json.put("L_accessRight", "100");
			json.put("L_registerDatetime", today);
			json.put("L_isBan", false);
			
			HttpPost request = new HttpPost("http://piguloming.no-ip.org:90/api/LibraryUser/PostLibraryUser");
			request.setHeader("Content-Type", "application/json");
			
			HttpEntity httpEntity = new StringEntity(json.toString(), HTTP.UTF_8);
			request.setEntity(httpEntity);
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(request);
			
			int code = httpResponse.getStatusLine().getStatusCode();  
	        Log.d("HttpStatus", "code: " + code);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

}
