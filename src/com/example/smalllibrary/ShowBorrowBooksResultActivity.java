package com.example.smalllibrary;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowBorrowBooksResultActivity extends Activity {
	
	private ListView listViewBorrowBooksSuccessResult;
	private ListView listViewBorrowBooksFailResult;
	private TextView textViewBorrowBooksSuccess;
	private TextView textViewBorrowBooksFail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_borrow_books_result);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		String result = intent.getStringExtra("BorrowBooksResult");
		
		findViews();
		ShowBorrowBooksResult(result);
	}
	
	private void findViews()
	{
		listViewBorrowBooksSuccessResult = (ListView)findViewById(R.id.listViewBorrowBooksSuccessResult);
		listViewBorrowBooksFailResult = (ListView)findViewById(R.id.listViewBorrowBooksFailResult);
		textViewBorrowBooksSuccess = (TextView)findViewById(R.id.textViewBorrowBooksSuccess);
		textViewBorrowBooksFail = (TextView)findViewById(R.id.textViewBorrowBooksFail);
		
	}
	
	private void ShowBorrowBooksResult(String result)
	{ 
		/*
		 * return json pattern
		 * {
		 * 		"Success":[
		 * 			{},{}..
		 * 		],
		 * 		
		 * 		"Fail":[
		 * 			{},{}..
		 * 		]	
		 * }
		 */
		//Toast.makeText(ShowBorrowBooksResultActivity.this, result, Toast.LENGTH_LONG).show();
		
		ArrayList<HashMap<String,Object>> successList = new ArrayList<HashMap<String,Object>>();
		ArrayList<HashMap<String,Object>> failList = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> successItem;
		HashMap<String,Object> failItem;
		
		try
		{
			JSONObject jsonObj = new JSONObject(result);
			JSONArray jsonArraySuccess = jsonObj.getJSONArray("Success");
			JSONArray jsonArrayFail = jsonObj.getJSONArray("Fail");
			JSONObject jsonSuccessObj;
			JSONObject jsonFailObj;
			
			int totalBooksBorrow = jsonArraySuccess.length() + jsonArrayFail.length();
			
			if(jsonArraySuccess.length() > 0)
			{
				// Create Success Borrow Book ListView
				for(int i = 0; i < jsonArraySuccess.length(); i++)
				{
					jsonSuccessObj = jsonArraySuccess.getJSONObject(i);
					successItem = new HashMap<String,Object>();
					
					successItem.put("title", jsonSuccessObj.getString("title"));
					successItem.put("author", jsonSuccessObj.getString("author"));
					successItem.put("publisher",jsonSuccessObj.getString("publisher"));
					successItem.put("publicationDate", "Published on : "+jsonSuccessObj.getString("publicationDate"));
					
					String[] datetime = jsonSuccessObj.getString("shouldReturnedDate").split("T");
					successItem.put("shouldReturnedDate", "Should return on : "+datetime[0]);
					
					successList.add(successItem);
				}
				
				SimpleAdapter adapterS = new SimpleAdapter(ShowBorrowBooksResultActivity.this, successList, R.layout.listview_borrow_book_item_success, 
						new String[]{"title","author","publisher","publicationDate", "shouldReturnedDate"},
						new int[]{R.id.textViewSuccessBorrowBookTitle, R.id.textViewSuccessBorrowBookAuthor, R.id.textViewSuccessBorrowBookPublisher, R.id.textViewSuccessBorrowBookPublicationDate, R.id.textViewSuccessBorrowBookShouldReturnedDate});
				
				listViewBorrowBooksSuccessResult.setAdapter(adapterS);
				textViewBorrowBooksSuccess.setText("Success Borrow : "+ jsonArraySuccess.length() + " / " + totalBooksBorrow);			
			}
			
			if(jsonArrayFail.length() > 0)
			{
				// Create Fail Borrow Book ListView
				for(int i = 0; i < jsonArrayFail.length(); i++)
				{
					jsonFailObj = jsonArrayFail.getJSONObject(i);
					failItem = new HashMap<String,Object>();
					
					failItem.put("title", jsonFailObj.getString("B_title"));
					failItem.put("author", jsonFailObj.getString("B_author"));
					failItem.put("publisher",jsonFailObj.getString("B_publisher"));
					failItem.put("publicationDate", "Published on : "+jsonFailObj.getString("B_publicationDate"));
					
					failList.add(failItem);
				}
				
				SimpleAdapter adapterF = new SimpleAdapter(ShowBorrowBooksResultActivity.this, failList, R.layout.listview_borrow_book_item_fail, 
						new String[]{"title","author","publisher","publicationDate"},
						new int[]{R.id.textViewFailBorrowBookTitle, R.id.textViewFailBorrowBookAuthor, R.id.textViewFailBorrowBookPublisher, R.id.textViewFailBorrowBookPublicationDate});
				
				listViewBorrowBooksFailResult.setAdapter(adapterF);
				textViewBorrowBooksFail.setText("Failed Borrow : "+ jsonArrayFail.length() + " / " + totalBooksBorrow);
			}
		}
		catch(JSONException e)
		{
			
		}
	}
	
	@Override
    public void onBackPressed()
    {
		Generic.LID = "";
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setClass(ShowBorrowBooksResultActivity.this,BorrowBooksLoginActivity.class);
		startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_borrow_books_result, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		if(menuItem.getItemId() == android.R.id.home)
			onBackPressed();
        return true;
    }

}
