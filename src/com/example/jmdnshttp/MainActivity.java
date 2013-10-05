package com.example.jmdnshttp;

import java.io.File;
import java.io.IOException;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private FileServer server; //80 is for root users on android
	private EditText textField;
	private String localAddress;
	private boolean started;
	private File path;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		started = false;
		if(isExternalStorageWritable()){
			path = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
		}
		else{
			path = getFilesDir();
		}
		
		//set up wifi manager and IP address
        WifiManager wifi = (WifiManager)
        		getSystemService(android.content.Context.WIFI_SERVICE);
        int ipAddress = wifi.getConnectionInfo().getIpAddress();
		localAddress = getIP(ipAddress);
		
		
		//UI Features--------------------------------------------------------------------
		//-------------------------------------------------------------------------------
		textField = (EditText) findViewById (R.id.editText2);
		
		
		Button startButton = (Button) findViewById (R.id.button1);
		startButton.setOnClickListener(new OnClickListener() {       	 
			@Override
			public void onClick(View arg0) {
				try{
					if(!started)
						initServer(localAddress);	
				}catch(Exception e){
					System.out.println("Error starting server");
					e.printStackTrace();
				}
			} 
		}); 
		
		//-------------------------------------------------------------------------------
		//End UI Features----------------------------------------------------------------
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
	
	// Checks if external storage is available for read and write
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	
	//method to initialise the http file server
	private boolean initServer(String address) throws IOException{
		if(address !=null){
			server = new FileServer(8080, path); //80 is reserved for root users on android
			server.startServer();
			
			if(server!=null){
				textField.append("The server has been started, at " + localAddress + ":8080");
				return true;
			}			
		}
		
		return false;
	}
	
	//method to initialise the http file server
	private boolean killServer(){
		System.out.println("shutting down server");
		if(server!=null){
			server.stopServer();
			return true;
		}
		else
			return false;
		
		
	}
	
    protected void onDestroy() {
    	super.onDestroy();
    	killServer();
    }
	
	//private void killServer(){
		//server.
	//}

}
