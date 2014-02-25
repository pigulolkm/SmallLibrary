package com.example.smalllibrary;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnBooksActivity extends Activity {

	private TextView textViewReturnBooksCount;
	private TextView textViewReturnBooksOutDate;
	private TextView textViewReturnBooksFee;
	private ListView listViewReturnBooks;
	
	private String scanCode = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_books);
		
		findViews();
		init();
	}
	
	private void findViews()
	{
		textViewReturnBooksCount = (TextView)findViewById(R.id.textViewReturnBooksCount);
		textViewReturnBooksOutDate = (TextView)findViewById(R.id.textViewReturnBooksOutDate);
		textViewReturnBooksFee = (TextView)findViewById(R.id.textViewReturnBooksFee);
		listViewReturnBooks = (ListView)findViewById(R.id.listViewReturnBooks);
	}
	
	private void init()
	{
		textViewReturnBooksCount.setText(getResources().getString(R.string.ReturnBookAmount) + "0");
		textViewReturnBooksOutDate.setText(getResources().getString(R.string.OutDateBooks)  + "0");
		textViewReturnBooksFee.setText(getResources().getString(R.string.Fine) + "$ 0");
	}
	
	/////////////////////////////////
	// ReturnBooksScan button click //
	///////////////////////////////
	public void ScanBooks(View v) {
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(ReturnBooksActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, Generic.scan_REQUEST);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case Generic.scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					// 1. Get scanned book id
					scanCode = data.getStringExtra("SCAN_RESULT");
					// 2. Request to server to return the book
					// TODO Return Book implement.........
					//String url = "http://piguloming.no-ip.org:90/api/Book/GetBook/"+scanCode;
					//new ReturnBookOperation().execute(url);
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
		}
	}
	
	private class ReturnBookOperation extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(ReturnBooksActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
			Dialog.setTitle("Loading");
			Dialog.setMessage("Please wait...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(String... urls) {
			String result = null;
			try
			{
				HttpGet httpGet = new HttpGet(urls[0]);
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
			
		}
		
	}
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.return_books, menu);
		return true;
	}

}
