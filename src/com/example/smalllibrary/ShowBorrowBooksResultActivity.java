package com.example.smalllibrary;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ShowBorrowBooksResultActivity extends Activity {
	
	private ListView listViewBorrowBooksSuccessResult;
	private ListView listViewBorrowBooksFailResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_borrow_books_result);
		
		Intent intent = getIntent();
		String result = intent.getStringExtra("BorrowBooksResult");
		
		findViews();
		ShowBorrowBooksResult(result);
	}
	
	private void findViews()
	{
		listViewBorrowBooksSuccessResult = (ListView)findViewById(R.id.listViewBorrowBooksSuccessResult);
		listViewBorrowBooksFailResult = (ListView)findViewById(R.id.listViewBorrowBooksFailResult);
	}
	
	private void ShowBorrowBooksResult(String result)
	{
		// TODO if succeed borrowing books, show Sucess borrowing message and should retured date 
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
			
			// Create Success Borrow Book ListView
			for(int i = 0; i < jsonArraySuccess.length(); i++)
			{
				jsonSuccessObj = jsonArraySuccess.getJSONObject(i);
				successItem = new HashMap<String,Object>();
				
				successItem.put("title", jsonSuccessObj.getString("title"));
				successItem.put("author", jsonSuccessObj.getString("author"));
				successItem.put("publisher",jsonSuccessObj.getString("publisher"));
				successItem.put("publicationDate", jsonSuccessObj.getString("publicationDate"));
				
				String[] datetime = jsonSuccessObj.getString("shouldReturnedDate").split("T");
				successItem.put("shouldReturnedDate", datetime[0]);
				
				successList.add(successItem);
			}
			
			SimpleAdapter adapterS = new SimpleAdapter(ShowBorrowBooksResultActivity.this, successList, R.layout.listview_borrow_book_item_success, 
					new String[]{"title","author","publisher","publicationDate", "shouldReturnedDate"},
					new int[]{R.id.textViewSuccessBorrowBookTitle, R.id.textViewSuccessBorrowBookAuthor, R.id.textViewSuccessBorrowBookPublisher, R.id.textViewSuccessBorrowBookPublicationDate, R.id.textViewSuccessBorrowBookShouldReturnedDate});
			
			listViewBorrowBooksSuccessResult.setAdapter(adapterS);
			
			// Create Fail Borrow Book ListView
			for(int i = 0; i < jsonArrayFail.length(); i++)
			{
				jsonFailObj = jsonArrayFail.getJSONObject(i);
				failItem = new HashMap<String,Object>();
				
				failItem.put("title", jsonFailObj.getString("B_title"));
				failItem.put("author", jsonFailObj.getString("B_author"));
				failItem.put("publisher",jsonFailObj.getString("B_publisher"));
				failItem.put("publicationDate", jsonFailObj.getString("B_publicationDate"));
				
				failList.add(failItem);
			}
			
			SimpleAdapter adapterF = new SimpleAdapter(ShowBorrowBooksResultActivity.this, failList, R.layout.listview_borrow_book_item_fail, 
					new String[]{"title","author","publisher","publicationDate"},
					new int[]{R.id.textViewFailBorrowBookTitle, R.id.textViewFailBorrowBookAuthor, R.id.textViewFailBorrowBookPublisher, R.id.textViewFailBorrowBookPublicationDate});
			
			listViewBorrowBooksFailResult.setAdapter(adapterF);
		}
		catch(JSONException e)
		{
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_borrow_books_result, menu);
		return true;
	}

}
