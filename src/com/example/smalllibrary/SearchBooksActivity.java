package com.example.smalllibrary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchBooksActivity extends Activity {

	private EditText editTextSearchKey;
	private Spinner spinnerSeachOption;
	
	private String[] key;
    private ArrayAdapter<String> searchOptionAdapter;
    private static final int scan_REQUEST = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_books);
		 ///////////////////////////////////
		// No title bar in this activity //
		//////////////////////////////////
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    
	    init();
        findViews();
        setListener();
        
        spinnerSeachOption.setAdapter(searchOptionAdapter);
	}
	
	private void init() {
		key 				= new String[]{"Author","Title","Subject","Publisher","ISBN","Scan Code"};
		searchOptionAdapter 	= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, key);
		
	}
	
	private void findViews(){
		editTextSearchKey = (EditText)findViewById(R.id.editTextSearchKey);
		spinnerSeachOption = (Spinner)findViewById(R.id.spinnerSeachOption);
	}
	
	private void setListener() {
		
		spinnerSeachOption.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				///////////////////////////////////////////
				// Handle Case: Scan code, call scanner // 
				/////////////////////////////////////////
				if(arg0.getSelectedItem().toString().equals("Scan Code"))
				{
					if(isCameraAvailable())
					{
						Intent intent = new Intent();
						intent.setClass(SearchBooksActivity.this, CameraTestActivity.class);
						startActivityForResult(intent, scan_REQUEST);
					}
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
	    });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					editTextSearchKey.setText(data.getStringExtra("SCAN_RESULT"));
                } 
                break;
		}
	}
	
	//////////////////////////////////////////////////////
	// SearchBooks button click, success: make request //
	////////////////////////////////////////////////////
	public void SearchBooks(View v) {
		if(editTextSearchKey.getText().toString().trim().equals(""))
		{
			Toast.makeText(SearchBooksActivity.this, "Please enter search terms", Toast.LENGTH_LONG);
		}
		else
		{
			String searchKey = editTextSearchKey.getText().toString();
			String searchOption = spinnerSeachOption.getSelectedItem().toString();
			String url = Generic.serverurl + "Book/GetBookByKey"+"?searchKey"+searchKey+"&searchOption"+searchOption;
			
			new GetSearchBooksOperation().execute(url);
		}
	}
	
	private class GetSearchBooksOperation extends AsyncTask<String, Void, String>{
		
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(SearchBooksActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
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
			Toast.makeText(SearchBooksActivity.this, result, Toast.LENGTH_LONG).show();
			// TO-DO make listView
		}
	
	}
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_books, menu);
		return true;
	}

}
