package com.example.smalllibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.smalllibrary.CameraTestActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int scan_REQUEST = 1;
	EditText etLibraryUser;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
        findViews();
	}
	
	private void init() {
		
	}
	
	private void findViews(){
		etLibraryUser = (EditText)findViewById(R.id.etLibraryUser);
	}
	
	// "Scan Code" button click
	public void scanCode(View v) {
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, scan_REQUEST);
		}
	}
	
	// "Registration" button click
	public void registration(View v){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, RegistrationActivity.class);
		startActivity(intent);
	}
	
	// "GetLibraryUser" button click\
	public void GetLibraryUser(View v)
	{
		String url = "http://piguloming.no-ip.org:90/api/LibraryUser/GetLibraryUsers";
		new GetLibraryUsersOperation().execute(url);
	    //httpget.addHeader("Content-Type", "application/json; charset=utf-8");
//	    try {
//	        // Execute HTTP Post Request
//	    	DefaultHttpClient httpclient = new DefaultHttpClient();
//	    	Toast.makeText(this, "Requesting...", Toast.LENGTH_SHORT).show();
//	        HttpResponse response = httpclient.execute(new HttpGet("http://piguloming.no-ip.org:90/api/LibraryUser/GetLibraryUsers"));
//	        
//	        int statusCode = response.getStatusLine().getStatusCode();
//	        Toast.makeText(this, "Status Code: "+ statusCode, Toast.LENGTH_LONG).show();
//	        
//	        HttpEntity ht = response.getEntity();
//
//	            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
//
//	            InputStream is = buf.getContent();
//
//	            BufferedReader r = new BufferedReader(new InputStreamReader(is));
//
//	            StringBuilder total = new StringBuilder();
//	            String line;
//	            while ((line = r.readLine()) != null) {
//	                total.append(line);
//	            }
//	            Toast.makeText(this, total, Toast.LENGTH_LONG).show();
//
//	    } catch (ClientProtocolException e) {
//	        // TODO Auto-generated catch block
//	    } catch (IOException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	    }
	}
	
	private class GetLibraryUsersOperation extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		
		@Override
		protected void onPreExecute() {
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
					result = String.valueOf(httpResponse.getStatusLine().getStatusCode());
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
			etLibraryUser.setText(result);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					Toast.makeText(this, "Scan Result = " + data.getStringExtra("SCAN_RESULT"), Toast.LENGTH_LONG).show();
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
		}
	}
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
