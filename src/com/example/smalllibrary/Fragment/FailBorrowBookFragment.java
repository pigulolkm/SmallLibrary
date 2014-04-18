package com.example.smalllibrary.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smalllibrary.R;
import com.example.smalllibrary.ShowBorrowBooksResultActivity;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FailBorrowBookFragment extends Fragment {
	
	private ListView listViewBorrowBooksFailResult;
	private TextView textViewBorrowBooksFail;
	private Activity activity;
	
	// Called when first time shown & this fragment will be consumed by its parent activity 
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	
	// Used for building the layout
	// Called after the parent activity's onCreate is called
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_fail_borrow_book, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		/*
		 * "Fail":[
		 * 		{},{}..
		 * 	]	
		 */
		/*Bundle bundle = getArguments(); 
		Log.d("FailBorrowBookFragment", bundle.getString("Json"));*/
		listViewBorrowBooksFailResult = (ListView)getView().findViewById(R.id.listViewBorrowBooksFailResult);
		textViewBorrowBooksFail = (TextView)getView().findViewById(R.id.textViewBorrowBooksFail);
		
		Bundle bundle = getArguments(); 
		Log.d("FailBorrowBookFragment", bundle.getString("Json") + bundle.getInt("totalBooksBorrow"));
		
		int totalBooksBorrow = bundle.getInt("totalBooksBorrow");
		
		ArrayList<HashMap<String,Object>> failList = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> failItem;
		JSONArray jsonArrayFail;
		try 
		{
			jsonArrayFail = new JSONArray(bundle.getString("Json"));
		
			JSONObject jsonFailObj;
			
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
				
				SimpleAdapter adapterF = new SimpleAdapter(activity, failList, R.layout.listview_borrow_book_item_fail, 
						new String[]{"title","author","publisher","publicationDate"},
						new int[]{R.id.textViewFailBorrowBookTitle, R.id.textViewFailBorrowBookAuthor, R.id.textViewFailBorrowBookPublisher, R.id.textViewFailBorrowBookPublicationDate});
				
				listViewBorrowBooksFailResult.setAdapter(adapterF);
				textViewBorrowBooksFail.setText("Fail Borrow : "+ jsonArrayFail.length() + " / " + totalBooksBorrow);			
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		super.onActivityCreated(savedInstanceState);
	}

}
