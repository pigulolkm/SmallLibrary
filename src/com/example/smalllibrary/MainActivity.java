package com.example.smalllibrary;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.smalllibrary.CameraTestActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private DrawerLayout layDrawer;
    private ListView lstDrawer;
    private String[] drawer_menu;

    private ActionBarDrawerToggle drawerToggle;
    private CharSequence mDrawerTitle = "Home";
	
    private ListView ListViewAnnouncementResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check network
        if(Generic.isOnline(this))
        {
        	
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
        
        // TODO Admin login
        
        initActionBar();
        initDrawer();
        // To set up the list of options on the left slider menu
        initDrawerList();
        getActionBar().setTitle(getTitle());
        findViews();
        
        if(Generic.announcementJson == "")
		{
			if(Generic.isOnline(this))
			{
				new downloadLastFiveAnnouncement().execute();
			}
		}
		else
		{
			showAnnouncement(Generic.announcementJson);
		}
	}
    
    private void findViews()
    {
    	ListViewAnnouncementResult = (ListView)findViewById(R.id.ListViewAnnouncementResult);
		ListViewAnnouncementResult.setOnItemClickListener(new announcementOnItemClick());
	}
    
    private class downloadLastFiveAnnouncement extends AsyncTask<Void, Void, String>
	{
		private final HttpClient  client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
			Dialog.setCanceledOnTouchOutside(false);
			Dialog.setTitle("Loading");
			Dialog.setMessage("Please wait...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(Void...params) {
			
			String result = null;
			try
			{
				HttpGet httpGet = new HttpGet(Generic.serverurl + "Announcement/GetLastFiveAnnouncements");
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
			Generic.announcementJson = result;
			showAnnouncement(Generic.announcementJson);
			Log.d("MainActivity", result);
		}
	}
	
	private void showAnnouncement(String json)
	{
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;
		
		try {
			JSONArray jsonArray = new JSONArray(json);

			if(jsonArray.length() != 0)
			{			
				JSONObject jsonObj;
				
				for(int i = 0; i < jsonArray.length(); i++)
				{
					jsonObj = jsonArray.getJSONObject(i);
					item = new HashMap<String,Object>();
					
					String[] msg = jsonObj.getString("A_content").split(":");
					String datetime = jsonObj.getString("A_datetime").replace("T", " ");
					String[] temp = datetime.split(":");
					String YYYYmmddHHMM = temp[0] + ":" + temp[1];
					
					item.put("msg", msg[1]);
					item.put("datetime", YYYYmmddHHMM);
					list.add(item);
				}
				
				SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, list, R.layout.listview_announcement_item, 
						new String[]{"msg","datetime"},
						new int[]{R.id.textViewAnnouncementContent, R.id.textViewAnnouncementDatetime});
				
				ListViewAnnouncementResult.setAdapter(adapter);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
	
	public void showAllAnnouncement(View v)
	{		
		if(Generic.isOnline(this))
		{
			new showAllAnnouncementOperation().execute(null, null);
		}
	}
	
	private class showAllAnnouncementOperation extends AsyncTask<String, Void, String>
	{
		private final HttpClient client = new DefaultHttpClient();
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setCancelable(true);
			Dialog.setCanceledOnTouchOutside(false);
			Dialog.setTitle("Loading");
			Dialog.setMessage("Please wait...");
			Dialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try
			{
				HttpGet httpGet = new HttpGet(Generic.serverurl + "Announcement/GetAnnouncements");
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
			Log.d("MainActivity-ShowAllannouncement", result);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ShowAllAnnouncementActivity.class);
			intent.putExtra("AllAnnouncementJson", result);
			startActivity(intent);
		}
	};
	
	public class announcementOnItemClick implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) 
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ShowAnnouncementActivity.class);
			intent.putExtra("Pos", pos);
			startActivity(intent);
		}
	}
    
    private void initActionBar(){
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
    
    private void initDrawer(){
        setContentView(R.layout.activity_main);

        layDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        lstDrawer = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        layDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        lstDrawer.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(
                this, 
                layDrawer,
                R.drawable.ic_drawer, 
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(getTitle());
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };
        drawerToggle.syncState();

        layDrawer.setDrawerListener(drawerToggle);
    }
    
    private void initDrawerList(){
    	drawer_menu = this.getResources().getStringArray(R.array.drawer_menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawer_menu);
        lstDrawer.setAdapter(adapter);
    }

	public void ScanCode() {
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, Generic.scan_REQUEST);
		}
	}

	public void Registration(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, RegistrationActivity.class);
		startActivity(intent);
	}
	
	public void SearchBooks(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SearchBooksActivity.class);
		startActivity(intent);
	}

	public void BorrowBooks() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, BorrowBooksLoginActivity.class);
		startActivity(intent);
	}
	
	public void ReturnBooks() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ReturnBooksActivity.class);
		startActivity(intent);
	}
	
	public void ManageBooks() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ManageBooksActivity.class);
		startActivity(intent);
	}
	
	public void UpdateRules() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, UpdateRuleActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case Generic.scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(data.getStringExtra("SCAN_RESULT")));
					startActivity(intent);
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	private void selectItem(int position) {
	    switch (position) {
	    case 0: // Home
	        break;

	    case 1: // Scan Code
	    	if(isCameraAvailable())
	    		ScanCode();
	        break;

	    case 2: // Search Books
	    	SearchBooks();
	        break;
	        
	    case 3: // Registry Borrowers
	    	Registration();
	        break;
	        
	    case 4: // Borrow Books
	    	BorrowBooks();
	        break;
	    
	    case 5: // Return Books
	    	ReturnBooks();
	    	break;
	    	
	    case 6: // Manage Books
	    	ManageBooks();
	    	break;
	    	
	    case 7: // Update Rules
	    	UpdateRules();
	    	break;
	    default:
	    	
	        return;
	    }

	    lstDrawer.setItemChecked(position, true);
	    // setTitle(drawer_menu[position]);
	    layDrawer.closeDrawer(lstDrawer);
	}
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.action_new_announcement:
				Intent intent = new Intent(this, PostAnnouncementActivity.class);
				startActivity(intent);
				return true;
				
			case android.R.id.home:
			    //Home icon is selected
			    if (drawerToggle.onOptionsItemSelected(item)) {
			        return true;
			    }
		}
		
	    return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void setTitle(CharSequence title) {
	    getActionBar().setTitle(title);
	}
	
	@Override
	public void onResume()
	{
		if(!getActionBar().getTitle().equals("Home"))
		{
			selectItem(0);
		}
		super.onResume();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
