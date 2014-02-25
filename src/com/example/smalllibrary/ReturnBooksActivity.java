package com.example.smalllibrary;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnBooksActivity extends Activity {

	private TextView textViewReturnBooksCount;
	private TextView textViewReturnBooksOutDate;
	private TextView textViewReturnBooksFee;
	private ListView listViewReturnBooks;
	
	private String scanCode = "";
	private ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
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
	// TODO Finish ReturnBookButton
	public void Finsh(View v)
	{
		// Show the total fine 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case Generic.scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					// Get scanned book id
					scanCode = data.getStringExtra("SCAN_RESULT");
					// Request to server to return the book
					String url = Generic.serverurl + "BorrowingRecord/PutBorrowingRecord/" + scanCode;
					new ReturnBookOperation().execute(url);
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
			try {
				JSONObject jsonObj = new JSONObject(result);
				String res = jsonObj.getString("Result");
				if(res.equals("True"))
				{
					CreateReturnBookListView(jsonObj);
				}
				else
				{
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReturnBooksActivity.this);
					alertDialog.setTitle("Error");
					alertDialog.setMessage(jsonObj.getString("Message"));
					alertDialog.setNeutralButton("Close", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}	
					});
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void CreateReturnBookListView(JSONObject jsonObj)
	{
		try {
			JSONObject returnBooks = jsonObj.getJSONObject("ReturnBooks");
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put("title", returnBooks.getString("title"));
			item.put("author", returnBooks.getString("author"));
			item.put("publisher",returnBooks.getString("publisher"));
			item.put("publicationDate", returnBooks.getString("publicationDate"));
			item.put("returnedDate", returnBooks.getString("returnedDate"));
			item.put("fine", returnBooks.getString("fine"));
			list.add(item);
			
			SimpleAdapter adapter = new SimpleAdapter(ReturnBooksActivity.this, list, R.layout.listview_return_book_item, 
					new String[]{"title","author","publisher","publicationDate", "returnedDate", "fine"},
					new int[]{R.id.textViewReturnedBookTitle, R.id.textViewReturnedBookAuthor, R.id.textViewReturnedBookPublisher, R.id.textViewReturnedBookPublicationDate, R.id.textViewReturnedBookReturnedDate, R.id.textViewReturnedBookFine});
			
			listViewReturnBooks.setAdapter(adapter);
			
		} catch (JSONException e) {
			e.printStackTrace();
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
