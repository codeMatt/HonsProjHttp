package com.example.jmdnshttp;

public class FileServer extends NanoHTTPD{

	public FileServer(int port) {
		super(port);
		
	}
	
	public Response serve(){
		Response res;
		
		res = new Response("this is the simple response");
		
		
		return res;
	}
	
	
}