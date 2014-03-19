package com.example.smalllibrary.Fragment;

import com.example.smalllibrary.R;
import com.example.smalllibrary.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UpdateBookFragment extends Fragment {

	// Called when first time shown & this fragment will be consumed by its parent activity 
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}
	
	// Used for building the layout
	// Called after the parent activity's onCreate is called
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_update_book, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
