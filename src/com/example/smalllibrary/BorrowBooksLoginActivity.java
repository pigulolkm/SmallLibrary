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
import android.widget.Toast;

public class BorrowBooksLoginActivity extends Activity {

	private static String BorrowerToken = "0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_books_login);
	}
	
	/////////////////////////////////////
	// ScanBorrowingToken Button Click //
	/////////////////////////////////////
	public void ScanBorrowingToken(View v)
	{
		// TODO BorrowBooksLogin
		// 1. Scan the QR code
		// 2. request to server and validate it
		// 3. if true 
		//   3.1 request borrowed amount by L_id & Br_returnedDate == null
		//   3.3 intent to BorrowBooksActivity
		
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(BorrowBooksLoginActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, Generic.scan_REQUEST);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case Generic.scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					BorrowerToken =  data.getStringExtra("SCAN_RESULT");
					String url = Generic.serverurl + "/LibraryUser/GetValidateToken?token="+BorrowerToken;
					new validateToken().execute(url);
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
		}
	}
	
	private class validateToken extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(BorrowBooksLoginActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
			Dialog.setTitle("Logging in");
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
			if(result.equals("True"))
			{
				// TODO step 3.
			}
		}
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

}
