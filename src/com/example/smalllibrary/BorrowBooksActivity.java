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
import android.widget.EditText;
import android.widget.Toast;

import com.example.smalllibrary.utils.Generic;

public class BorrowBooksActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_book);
		
		Scan();
	}
	
	//private void init() {	}
	
	//private void findViews(){	}
	
	private void Scan() {
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(BorrowBooksActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, Generic.scan_REQUEST);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case Generic.scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					Toast.makeText(this, "Scan Result = " + data.getStringExtra("SCAN_RESULT"), Toast.LENGTH_LONG).show();
					// TODO 
					// 1. Get scanned book id
					String bookID = data.getStringExtra("SCAN_RESULT");
					// 2. make request to server to check the book is valid to borrow(check <Book>, add column 'B_status' in DB)
					String url = "http://piguloming.no-ip.org:90/api/Book/GetBook/"+bookID;
					new GetBookOperation().execute(url);
					//   2.1 if OK -> add to listview
					//   2.2 if NOT -> Toast Invalid code
					// 4. Handle click confirm button -> Add borrowing record to database
					// TODO 3.check borrow book limit before 
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
		}
	}
	
	private class GetBookOperation extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(BorrowBooksActivity.this);
		
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
			// TODO receive result
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.borrow_book, menu);
		return true;
	}
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
