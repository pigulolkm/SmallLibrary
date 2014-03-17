package com.example.smalllibrary;

import java.security.MessageDigest;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Generic {
	public static final int scan_REQUEST = 1;
	public static final int scan_REQUEST_Card = 2;
	public static String serverurl = "http://piguloming.no-ip.org:90/api/";
	public static String LID = "";
	public static int borrowingLimit = 0;
	public static String unlockPassword = "123456"; // TODO add <Rule_unclockPassword> in Rule Table & get it when admin login success
	
	public static String computeHash(String input) {
		StringBuffer sb = new StringBuffer();
		try
		{
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
	
		    byte[] byteData = digest.digest(input.getBytes("UTF-8"));

		    for (int i = 0; i < byteData.length; i++){
		      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	    return sb.toString();
	}
	
	public static boolean isOnline(Activity a) {
	    ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
}
