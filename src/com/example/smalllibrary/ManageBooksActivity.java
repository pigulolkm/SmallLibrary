package com.example.smalllibrary;

import com.example.smalllibrary.Fragment.CreateBookFragment;
import com.example.smalllibrary.Fragment.UpdateBookFragment;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class ManageBooksActivity extends FragmentActivity  {
	
	private FragmentTabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_books);
		
		findViews();
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		initTab();
	}
	
	public void findViews()
	{
		tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
	}
	
	public void initTab()
	{
		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

	    tabHost.addTab(tabHost.newTabSpec("Create").setIndicator("Create"), CreateBookFragment.class, null);
	    tabHost.addTab(tabHost.newTabSpec("Update").setIndicator("Update"), UpdateBookFragment.class, null);
	    
	    tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				Toast.makeText(ManageBooksActivity.this, "Tab Changed", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_books, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		if(menuItem.getItemId() == android.R.id.home)
		{
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setClass(ManageBooksActivity.this,MainActivity.class);
	        startActivity(intent); 
		}
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
