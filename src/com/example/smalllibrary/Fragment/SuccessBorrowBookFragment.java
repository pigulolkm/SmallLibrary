package com.example.smalllibrary.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smalllibrary.R;

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

public class SuccessBorrowBookFragment extends Fragment {
	
	private ListView listViewBorrowBooksSuccessResult;
	private TextView textViewBorrowBooksSuccess;
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
		return inflater.inflate(R.layout.fragment_success_borrow_book, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		/*
		 * "Success":[
		 * 		{},{}..
		 * 	]	
		 */
		listViewBorrowBooksSuccessResult = (ListView)getView().findViewById(R.id.listViewBorrowBooksSuccessResult);
		textViewBorrowBooksSuccess = (TextView)getView().findViewById(R.id.textViewBorrowBooksSuccess);
		
		Bundle bundle = getArguments(); 
		Log.d("SuccessBorrowBookFragment", bundle.getString("Json") + bundle.getInt("totalBooksBorrow"));
		
		int totalBooksBorrow = bundle.getInt("totalBooksBorrow");
		
		ArrayList<HashMap<String,Object>> successList = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> successItem;
		JSONArray jsonArraySuccess;
		try 
		{
			jsonArraySuccess = new JSONArray(bundle.getString("Json"));
		
			JSONObject jsonSuccessObj;
			
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
				
				SimpleAdapter adapterS = new SimpleAdapter(activity, successList, R.layout.listview_borrow_book_item_success, 
						new String[]{"title","author","publisher","publicationDate", "shouldReturnedDate"},
						new int[]{R.id.textViewSuccessBorrowBookTitle, R.id.textViewSuccessBorrowBookAuthor, R.id.textViewSuccessBorrowBookPublisher, R.id.textViewSuccessBorrowBookPublicationDate, R.id.textViewSuccessBorrowBookShouldReturnedDate});
				
				listViewBorrowBooksSuccessResult.setAdapter(adapterS);
				textViewBorrowBooksSuccess.setText("Success Borrow : "+ jsonArraySuccess.length() + " / " + totalBooksBorrow);			
			}
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		super.onActivityCreated(savedInstanceState);
	}

}
