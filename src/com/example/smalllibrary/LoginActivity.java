package com.example.smalllibrary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText EditTextEmail;
	private EditText EditTextPassword;
	
	private String Email;
	private String Password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		findViews();
	}
	
	private void findViews()
	{
		EditTextEmail = (EditText)findViewById(R.id.EditTextEmail);
		EditTextPassword = (EditText)findViewById(R.id.EditTextPassword);
	}
	
	////////////////////////
	// Login Button Click //
	////////////////////////
	public void login(View v)
	{
		//Reset errors.
		EditTextEmail.setError(null);
		EditTextPassword.setError(null);
		
		Email = EditTextEmail.getText().toString();
		Password = EditTextPassword.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(Password)) 
		{
			EditTextPassword.setError(getString(R.string.error_field_required));
			focusView = EditTextPassword;
			cancel = true;
		} 
		/*else if (Password.length() < 4) 
		{
			EditTextPassword.setError(getString(R.string.error_invalid_password));
			focusView = EditTextPassword;
			cancel = true;
		}*/

		// Check for a valid email address.
		if (TextUtils.isEmpty(Email)) 
		{
			EditTextEmail.setError(getString(R.string.error_field_required));
			focusView = EditTextEmail;
			cancel = true;
		} 
		else if (!Email.contains("@")) 
		{
			EditTextEmail.setError(getString(R.string.error_invalid_email));
			focusView = EditTextEmail;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else 
		{
			String passwordHash = Generic.computeHash(Password);
			JSONObject jsonObj = new JSONObject();
			
			try 
			{
				jsonObj.put("pw", passwordHash);
				jsonObj.put("email", Email);
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("UserPwJson", jsonObj.toString());
				setResult(RESULT_OK,returnIntent);
				finish();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {   
		switch(menuItem.getItemId())
		{
	    	case android.R.id.home:
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setClass(LoginActivity.this, BorrowBooksLoginActivity.class);
	        startActivity(intent); 
	        return true;
		}
	    return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
