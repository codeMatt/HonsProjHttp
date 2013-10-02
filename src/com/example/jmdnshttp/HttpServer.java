package com.example.jmdnshttp;

import java.util.Map;

public class HttpServer extends NanoHTTPD{

	public HttpServer(int port) {
		super(port);
	}
	
    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files){
		String msg = "";
    	
    	
    	return new NanoHTTPD.Response(msg);
	}

}
