package com.example.smalllibrary;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class BorrowBooksActivity extends Activity {

	private ListView listViewBorrowBooks;
	private static int BorrowedAmount = 0;
	
	private String scanCode = "";
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_book);
		
		Intent intent = new Intent();
		BorrowedAmount = intent.getIntExtra("borrowedAmount", 0);
		
		findViews();
		// TODO show borrow books; borrowed amount / borrowing Limit in activity_borrow_book
	}
	
	private void findViews(){
		listViewBorrowBooks = (ListView)findViewById(R.id.listViewBorrowBooks);
	}
	
	/////////////////////////////////
	// ScanMoreBooks button click //
	///////////////////////////////
	public void ScanBooks(View v) {
		Scan();
	}
	
	//////////////////////////////////////
	// ConfirmBorrowBooks button click //
	////////////////////////////////////
	public void ConfirmBorrowBooks(View v) {
		HashMap<String,Object> item;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj;
		
		String url = Generic.serverurl+"BorrowingRecord/PostBorrowingRecord";
		
		for(int i = 0; i < list.size(); i++)
		{
			item = new HashMap<String,Object>();
			jsonObj =  new JSONObject();
			
			try {
				item = list.get(i);
				jsonObj.put("B_id", item.get("id"));
				jsonObj.put("L_id", Generic.LID);
				jsonArray.put(jsonObj);
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		Toast.makeText(BorrowBooksActivity.this, jsonArray.toString(), Toast.LENGTH_LONG).show();
		
		String[] params = new String[]{url, jsonArray.toString()};
		new PostBorrowingRecord().execute(params);
	}
	
	private void Scan() {
		// Check the amount of borrowing book is out of limit
		if(BorrowedAmount < Generic.borrowingLimit)
		{
			if(isCameraAvailable())
			{
				Intent intent = new Intent();
				intent.setClass(BorrowBooksActivity.this, CameraTestActivity.class);
				startActivityForResult(intent, Generic.scan_REQUEST);
			}
		}
		else
		{
			Toast.makeText(BorrowBooksActivity.this, "You have reached the borrowing limit!", Toast.LENGTH_LONG).show();
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
					// 2. Request to server to check the book is valid to borrow
					String url = "http://piguloming.no-ip.org:90/api/Book/GetBook/"+scanCode;
					new GetBookOperation().execute(url);
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
			checkBookIsValid(result);
		}
		
	}
	
	private void checkBookIsValid(String result) {
		HashMap<String,Object> item;
		
		try {
			JSONObject jsonObj = new JSONObject(result);
			if(jsonObj.has("B_title") && jsonObj.has("B_author") && jsonObj.has("B_status"))
			{
				// Y : Can be borrowed
				if(jsonObj.getString("B_status").equals("Y"))
				{
					// Check the book exists in the list
					if(!isBookExistInList(jsonObj))
					{
						item = new HashMap<String,Object>();
						
						item.put("id", jsonObj.getString("B_id"));
						item.put("title", jsonObj.getString("B_title"));
						item.put("author", jsonObj.getString("B_author"));
						item.put("publisher",jsonObj.getString("B_publisher"));
						item.put("publicationDate", jsonObj.getString("B_publicationDate"));
						list.add(item);
						
						SimpleAdapter adapter = new SimpleAdapter(BorrowBooksActivity.this, list, R.layout.listview_book_item, 
								new String[]{"title","author","publisher","publicationDate"},
								new int[]{R.id.textViewBookTitle, R.id.textViewBookAuthor, R.id.textViewBookPublisher, R.id.textViewBookPublicationDate});
						
						listViewBorrowBooks.setAdapter(adapter);
						BorrowedAmount += 1;
					}
				}
				else
				{
					Toast.makeText(BorrowBooksActivity.this, jsonObj.getString("B_title")+" cannot be borrowed. The book's status is 'Not allowed'.", Toast.LENGTH_LONG).show();
				}
			}
			else
			{
				Toast.makeText(BorrowBooksActivity.this, "Invalid Code: "+scanCode, Toast.LENGTH_LONG).show();
			}			
		} catch (JSONException e) {
			if(result.contains("404"))
				Toast.makeText(BorrowBooksActivity.this, "Invalid Code, Result not found", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}	
	}
	
	private boolean isBookExistInList(JSONObject jsonObj) {
		HashMap<String,Object> item = new HashMap<String, Object>();
		for(int i = 0; i < list.size(); i++)
		{
			item = list.get(i);
			try {
				if(item.get("id").equals(jsonObj.getString("B_id")))
				{
					Toast.makeText(BorrowBooksActivity.this, "This book has already added into the list.", Toast.LENGTH_LONG).show();
					return true;
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private class PostBorrowingRecord extends AsyncTask<String, Void, String>{
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
		protected String doInBackground(String... params) {
			String result = null;
			try
			{
				HttpPost httpPost = new HttpPost(params[0]);
				// Convert JSONObject to JSON to String
				String json = params[1].toString();
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
			Toast.makeText(BorrowBooksActivity.this, result, Toast.LENGTH_LONG).show();
			// TODO if succeed borrowing books, show Sucess borrowing message and should retured date 
			//test
		}
		
	}
	
	@Override
    public void onBackPressed()
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(BorrowBooksActivity.this);
		builder.setTitle("Are you sure you want to logout?");
		
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Generic.LID = "";
				dialog.dismiss();
				BorrowBooksActivity.this.finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
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
