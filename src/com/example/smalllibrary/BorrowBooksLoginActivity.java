package com.example.smalllibrary;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import android.content.pm.PackageManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BorrowBooksLoginActivity extends Activity {

	private static String BorrowerToken = "0";
	private static String BorrowerEmail = "";
	private static String LID = "";
	private static int BorrowedAmount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_books_login);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// TODO add email & password validation instead of checking by QR token
	}
	
	////////////////////////////////////
	// ButtonScanBorrowingToken Click //
	////////////////////////////////////
	public void ScanBorrowingToken(View v)
	{		
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(BorrowBooksLoginActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, Generic.scan_REQUEST);
		}
	}
	
	///////////////////////////////////
	// ButtonScanBorrowingCard Click //
	///////////////////////////////////
	public void ScanBorrowingCard(View v)
	{
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(BorrowBooksLoginActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, Generic.scan_REQUEST_Card);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case Generic.scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					try
					{
						String[] results = data.getStringExtra("SCAN_RESULT").split("_");
						BorrowerToken = results[0];
						BorrowerEmail = results[1];
						LID = results[2];
						String url = Generic.serverurl + "LibraryUser/GetValidateToken?token="+BorrowerToken+"&email="+BorrowerEmail+"&Lid="+LID;
						
						if(checkNetworkState())
						{
							new validateToken().execute(url);
						}
					}
					catch(Exception e)
					{
						Toast.makeText(BorrowBooksLoginActivity.this, "Invalid code", Toast.LENGTH_LONG).show();
					}
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
			case Generic.scan_REQUEST_Card:
				if(resultCode == RESULT_OK)
				{
					try
					{
						String result = data.getStringExtra("SCAN_RESULT");
						String url = Generic.serverurl + "LibraryUser/GetValidateCard?cardID="+result;
						
						if(checkNetworkState())
						{
							new validateCard().execute(url);
						}
					}
					catch(Exception e)
					{
						Toast.makeText(BorrowBooksLoginActivity.this, "Invalid code", Toast.LENGTH_LONG).show();
					}
				}
				else if(resultCode == RESULT_CANCELED)
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
				}
		}
	}
	
	private class validateToken extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(BorrowBooksLoginActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
			Dialog.setTitle("Logging in");
			Dialog.setMessage("Connecting...");
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
			
			try 
			{
				JSONArray jsonArray = new JSONArray(result);
				
				if(jsonArray.length() != 0)
				{
					JSONObject jsonObj = jsonArray.getJSONObject(0);
					// True means token is correct
					if(jsonObj.getString("result").equals("True"))
					{
						BorrowedAmount = Integer.parseInt(jsonObj.getString("borrowedAmount"));
						Generic.borrowingLimit = Integer.parseInt(jsonObj.getString("borrowingLimit"));
						Generic.LID = LID;
						
						Dialog.setTitle("Login Success");
						Dialog.setMessage("Please wait...");
						
						// Intent after 3 seconds
						Timer timer = new Timer();
						timer.schedule(new TimerTask(){
							@Override
							public void run() {
								Dialog.dismiss();
								Intent i = new Intent();
								i.setClass(BorrowBooksLoginActivity.this, BorrowBooksActivity.class);
								i.putExtra("borrowedAmount", BorrowedAmount);
								startActivity(i);
							}}, 3000);
					}
					else
					{
						Dialog.dismiss();
						
						AlertDialog.Builder builder = new AlertDialog.Builder(BorrowBooksLoginActivity.this);
						builder.setMessage("Invalid code!");
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,	int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				}
			}
			catch(JSONException e)
			{
				
			}
			
		}
	}
	
private class validateCard extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(BorrowBooksLoginActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
			Dialog.setTitle("Logging in");
			Dialog.setMessage("Connecting...");
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
			
			try 
			{
				JSONArray jsonArray = new JSONArray(result);
				
				if(jsonArray.length() != 0)
				{
					JSONObject jsonObj = jsonArray.getJSONObject(0);
					// True means token is correct
					if(jsonObj.getString("result").equals("True"))
					{
						BorrowedAmount = Integer.parseInt(jsonObj.getString("borrowedAmount"));
						Generic.borrowingLimit = Integer.parseInt(jsonObj.getString("borrowingLimit"));
						Generic.LID = jsonObj.getString("LID");
						
						Dialog.setTitle("Login Success");
						Dialog.setMessage("Please wait...");
						Dialog.dismiss();
						
						AlertDialog.Builder builder = new AlertDialog.Builder(BorrowBooksLoginActivity.this);
						builder.setTitle("Username :");
						builder.setMessage(jsonObj.getString("name"));
						builder.setPositiveButton("Continue", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,	int which) {
								Intent i = new Intent();
								i.setClass(BorrowBooksLoginActivity.this, BorrowBooksActivity.class);
								i.putExtra("borrowedAmount", BorrowedAmount);
								startActivity(i);
							}
						});
						builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Generic.LID = "";
								BorrowBooksLoginActivity.this.recreate();
							}
						});
						builder.create().show();
					}
					else
					{
						Dialog.dismiss();
						
						AlertDialog.Builder builder = new AlertDialog.Builder(BorrowBooksLoginActivity.this);
						builder.setTitle("Invalid code!");
						builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,	int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				}
			}
			catch(JSONException e)
			{
				
			}
			
		}
	}
	
	@Override
    public void onBackPressed()
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(BorrowBooksLoginActivity.this);
		final EditText editTextUnlockPassword = new EditText(BorrowBooksLoginActivity.this);

		editTextUnlockPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		builder.setTitle("Please insert password to unlock");
		builder.setView(editTextUnlockPassword);
		builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(editTextUnlockPassword.getText().toString().equals(Generic.unlockPassword))
				{
					dialog.dismiss();
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.setClass(BorrowBooksLoginActivity.this, MainActivity.class);
					startActivity(intent);
				}
				else
				{
					Toast.makeText(BorrowBooksLoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
					dialog.dismiss();
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
    }
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.borrow_books_login, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		if(menuItem.getItemId() == android.R.id.home)
		{
			onBackPressed();
		}
        return true;
    }
	
	private boolean checkNetworkState()
	{
		if(Generic.isOnline(this))
		{
			return true;
		}
		else
        {
        	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        	dialog.setTitle("Warning");
        	dialog.setMessage(getResources().getString(R.string.warning_networkConnectionError));
        	dialog.setNeutralButton("OK", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
        	}).create().show();;
        }
		return false;
	}
}
