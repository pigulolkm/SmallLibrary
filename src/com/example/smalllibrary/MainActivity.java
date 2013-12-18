package com.example.smalllibrary;

import com.example.smalllibrary.CameraTestActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int scan_REQUEST = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
	}
	
	public void init() {
		
	}
	
	// "Scan Code" button click
	public void scanCode(View v) {
		if(isCameraAvailable())
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, CameraTestActivity.class);
			startActivityForResult(intent, scan_REQUEST);
		}
	}
	
	public void registration(View v){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, RegistrationActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case scan_REQUEST:
				if(resultCode == RESULT_OK)
				{
					Toast.makeText(this, "Scan Result = " + data.getStringExtra("SCAN_RESULT"), Toast.LENGTH_LONG).show();
                } 
				else if(resultCode == RESULT_CANCELED) 
				{
					Toast.makeText(this, "Invalid code", Toast.LENGTH_LONG).show();
                }
                break;
		}
	}
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
