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
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BorrowBooksActivity extends Activity {

	private ListView listViewBorrowBooks;
	private TextView textViewBorrowBooksCount;
	private static int BorrowedAmount;
	
	private String scanCode = "";
	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_book);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		BorrowedAmount = intent.getIntExtra("borrowedAmount", Generic.borrowingLimit);
		
		findViews();
		init();
	}
	
	private void findViews(){
		listViewBorrowBooks = (ListView)findViewById(R.id.listViewBorrowBooks);
		textViewBorrowBooksCount = (TextView)findViewById(R.id.textViewBorrowBooksCount);
	}
	
	private void init(){
		textViewBorrowBooksCount.setText("Borrow Book Amount : " + BorrowedAmount + " / " + Generic.borrowingLimit);
	}
	
	/////////////////////////////////
	// ScanMoreBooks button click //
	///////////////////////////////
	public void ScanBooks(View v) {
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
			AlertDialog.Builder dialog = new AlertDialog.Builder(BorrowBooksActivity.this);
	         dialog.setTitle("You have reached the borrowing limit!");
	         dialog.setNeutralButton("OK", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
	         });
	         dialog.create().show();
		}
	}
	
	//////////////////////////////////////
	// ConfirmBorrowBooks button click //
	////////////////////////////////////
	public void ConfirmBorrowBooks(View v) {

		if(list.size() > 0)
		{
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
			
			String[] params = new String[]{url, jsonArray.toString()};
			
			if(checkNetworkState())
			{
				new PostBorrowingRecord().execute(params);
			}
		}
		else
		{
			AlertDialog.Builder dialog = new AlertDialog.Builder(BorrowBooksActivity.this);
	         dialog.setTitle("None book is in the list");
	         dialog.setNeutralButton("OK", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
	         });
	         dialog.create().show();
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
					String url = Generic.serverurl+"Book/GetBook/"+scanCode;
					
					if(checkNetworkState())
					{
						new GetBookOperation().execute(url);
					}
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
						textViewBorrowBooksCount.setText("Borrow Book Amount : " + BorrowedAmount + " / " + Generic.borrowingLimit);
					}
				}
				else
				{
					AlertDialog.Builder dialog = new AlertDialog.Builder(BorrowBooksActivity.this);
			         dialog.setTitle(jsonObj.getString("B_title")+" cannot be borrowed. The book's status is 'Not allowed'.");
			         dialog.setNeutralButton("OK", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
			         });
			         dialog.create().show();
				}
			}
			else
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(BorrowBooksActivity.this);
		         dialog.setTitle("Invalid Code: "+scanCode);
		         dialog.setNeutralButton("OK", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
		         });
		         dialog.create().show();
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
					AlertDialog.Builder dialog = new AlertDialog.Builder(BorrowBooksActivity.this);
			         dialog.setTitle("This book has already added into the list.");
			         dialog.setNeutralButton("OK", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
			         });
			         dialog.create().show();
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
			
			Intent intent = new Intent(BorrowBooksActivity.this, ShowBorrowBooksResultActivity.class);
			intent.putExtra("BorrowBooksResult", result);
			startActivity(intent);
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
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		onBackPressed();
       // startActivity(new Intent(BorrowBooksActivity.this,BorrowBooksLoginActivity.class)); 
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
