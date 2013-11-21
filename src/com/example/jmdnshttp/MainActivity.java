package com.example.jmdnshttp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private SimpleWebServer server; //80 is for root users on android
	private EditText textField, addressText;
	private String localAddress;
	private boolean started;
	private File path;
	private android.os.Handler handler = new android.os.Handler();
    private String logText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		started = false;
		if(isExternalStorageWritable()){
			File parentPath2 = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
			path = new File (parentPath2, "root");
		}
		else{
			path = getFilesDir();
		}
		path.mkdirs();
		System.out.println(path);
		
		//set up wifi manager and IP address
        WifiManager wifi = (WifiManager)
        		getSystemService(android.content.Context.WIFI_SERVICE);
        int ipAddress = wifi.getConnectionInfo().getIpAddress();
		localAddress = getIP(ipAddress);
		
		
		//UI Features--------------------------------------------------------------------
		//-------------------------------------------------------------------------------
		textField = (EditText) findViewById (R.id.server_text);
		
		addressText = (EditText) findViewById(R.id.address_text);
		
		
		Button startButton = (Button) findViewById (R.id.server_start_button);
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
		
		//get button
		Button getButton =  (Button) findViewById(R.id.get_button);
		getButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String address = addressText.getText().toString();
				
				if(address.charAt(0) == '1'){
					notifyUser("\nAttempting Get Method");
					doGetThread(address);
				}
				else{
					System.out.println("url doesn't start with \"1\"");
				}
				//notifications.append("\nGet Over!");

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
			server = new SimpleWebServer(null, 8080, path, false); //80 is reserved for root users on android
			ServerRunner.executeInstance(server); 
			
			if(server!=null){
				textField.append("server address: " + localAddress + ":8080");
				return true;
			}			
		}
		
		return false;
	}
	
	//creates and runs the thread to run the http get method
	public void doGetThread(final String address){

		System.out.println("Getting from: " + address);
		//sets which files to get
		final String[] getFiles = { 
									"100mb%20number%201.mp3",
									};

		new Thread(){
			public void run() {        		
				try {
					doGetMethod(address, getFiles);
				} catch (IOException e) {
					System.out.println("Get Failed");
					e.printStackTrace();
				}        		
			}
		}.start();
	}
	
	//do the http get methods required to download the files
	public void doGetMethod (String address, String[] files) throws IOException{

			long startSession = System.currentTimeMillis();
			long totalFileReceived = 0;
			addToLog("New Session,\tHTTPGET Client 1 -------------------------------------");
			
			//loop through every files and do a get for each one
			for(String file : files){


				String filename = file;
				String url = "http://" + address + ":8080/" + filename;
				notifyUser("getting: " + url);

				//set up files for receiving file
				File receivedDirectory = new File(path, "getFiles");
				receivedDirectory.mkdirs();
				File receivedFile = new File(receivedDirectory, "receivable_" + filename);
				receivedFile.createNewFile();

				//set up httpclient object
				HttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);		
				HttpGet httpGet = new HttpGet(url);
				//long getStart = System.currentTimeMillis();
				HttpResponse response = httpClient.execute(httpGet); //execute http get

				String responseStatus = "" + response.getStatusLine();
				notifyUser("Response: " + responseStatus);
				System.out.println("Response: " + responseStatus);
				FileOutputStream fos = new FileOutputStream(receivedFile);

				HttpEntity entity = response.getEntity();
				InputStream in = response.getEntity().getContent();

				if (entity != null) {
					long len = entity.getContentLength();
					if (!(responseStatus.equals("HTTP/1.1 200 OK"))) {
						System.out.println(responseStatus);	
					} else {      		
						System.out.println("content length: " + len);
						byte[] buffer = new byte[8 * 1024];
						long cumLength = 0;

						long fileStart = System.currentTimeMillis();
						for (int length = 0; (length = in.read(buffer)) > -1;) {
							fos.write(buffer, 0, length);
							cumLength+=length;
							notifyUserDownload("Downloaded: " + (int)(cumLength*100/len) + "%");
						}
						long fileDuration = System.currentTimeMillis() - fileStart;
						notifyUserDownload("Download complete");
						fos.flush();
						fos.close();
						
						addToLog("\nReceived file: " + file
								   + "\tDuration: " + fileDuration +" ms"
								   + "\tfile size: " + len + " bytes");
						totalFileReceived+=len;
						
					}
				}	
			}
			long sessionDuration = System.currentTimeMillis() - startSession;
			addToLog("\nSession complete:\nSession length " + sessionDuration + "ms\t"
					  + "Session transfer amount: " + totalFileReceived + "bytes" 
					  + "\n-------------------------------------------");
			writeLog("HTTPGet - 3 Client 25-35mb");
			notifyUser("get Complete");
		}

	
	//print to large text box in middle
	private void notifyUser(final String msg){	
		
	      handler.postDelayed(new Runnable() {
	            public void run() {
	            	TextView t = (TextView)findViewById(R.id.notification_text);
	            	t.setText(t.getText()+"\n-"+msg);
	            	}
	            }, 1);		
	}
	
	//write over previous msg on bottom text box
	private void notifyUserDownload(final String msg){	
		
	      handler.postDelayed(new Runnable() {
	            public void run() {
	            	TextView t = (TextView)findViewById(R.id.download_text);
	            	t.setText(msg);
	            	}
	            }, 1);		
	}
	
	//add a line to the log
	private void addToLog(String logMessage){
		if(logText == null)
			logText= "";
		logText= logText + "\n" + logMessage;
	}
	
	//output log string to text file
	private void writeLog(String logFileName){
		
		System.out.println("writing log");
		String fileName = logFileName + " at " + new Date().toString() + ".txt";
		File logDirectory = new File(path, "logs");
		logDirectory.mkdir();
		File log = new File(logDirectory, fileName);		
		
		try {
			log.createNewFile();
			PrintWriter out = new PrintWriter(log);
			out.println(logText);			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logText = "";
	}
	
	
	//kill the server instance
    protected void onDestroy() {
    	super.onDestroy();
    	ServerRunner.stopInstance();
    }
}
