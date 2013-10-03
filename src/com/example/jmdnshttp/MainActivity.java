package com.example.jmdnshttp;

import java.io.IOException;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	private FileServer server; //80 is for root users on android
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set up wifi manager and IP address
        WifiManager wifi = (WifiManager)
        		getSystemService(android.content.Context.WIFI_SERVICE);
        int ipAddress = wifi.getConnectionInfo().getIpAddress();
		String localAddress = getIP(ipAddress);
		
		initServer(localAddress);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//method convert int ip address into string
	private String getIP(int ipAddress){
		
		//convert integer ip address into a string
		String address =  (ipAddress & 0xFF ) + "." +
	               ((ipAddress >> 8 ) & 0xFF) + "." +
	               ((ipAddress >> 16 ) & 0xFF) + "." +
	               ( (ipAddress >> 24 ) & 0xFF) ;
		
		
		
		return  address;
	}
	
	
	//method to initialise the http file server
	private boolean initServer(String address){
		if(address !=null){
			server  = new FileServer(8080); //80 is reserved for root users on android
						
			
		}
		
		return false;
	}

}
