package com.example.smalllibrary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class PostAnnouncementActivity extends Activity {

	private ProgressDialog Dialog;
	
	private EditText EditTextAnnouncement;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_announcement);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		findViews();
	}
	
	public void findViews()
	{
		EditTextAnnouncement = (EditText)findViewById(R.id.EditTextAnnouncement);
	}
	
	public void PostAnnouncement(View v)
	{
		if(checkNetworkState())
		{
			final HttpClient client = new DefaultHttpClient();
			
			Dialog = new ProgressDialog(PostAnnouncementActivity.this);
			Dialog.setCancelable(false);
			Dialog.setCanceledOnTouchOutside(false);
			Dialog.setTitle("Loading");
			Dialog.setMessage("Please wait...");
			Dialog.show();
			
			JSONObject json = new JSONObject();
			try 
			{
				json.put( "msg" , EditTextAnnouncement.getText().toString());
			} 
			catch (JSONException e1) 
			{
				e1.printStackTrace();
			}
			
			String[] params = new String[]{ "GCM/PostAnnouncement", json.toString()};
			
			new AsyncTask<String, Void, String>()
			{
				@Override
				protected String doInBackground(String... params) {
					String result = null;
					try
					{
						Log.d("PostAnnouncementActivity", "Request url : "+ Generic.serverurl + params[0]);
						Log.d("PostAnnouncementActivity", "Sending announcement to server :" + params[1]);
						HttpPost httpPost = new HttpPost(Generic.serverurl + params[0]);
						StringEntity se= new StringEntity(params[1]);
						httpPost.setEntity(se);
						httpPost.setHeader("Content-Encoding", "UTF-8");
						httpPost.setHeader("Content-Type", "application/json");
						HttpResponse httpResponse = client.execute(httpPost);
						
						result = httpResponse.getStatusLine().toString();
						result += " "+ EntityUtils.toString(httpResponse.getEntity());					
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
					Log.d("PostAnnouncement", result+"");
			    }
			}.execute(params);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_announcement, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		if(menuItem.getItemId() == android.R.id.home)
		{
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setClass(PostAnnouncementActivity.this,MainActivity.class);
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
