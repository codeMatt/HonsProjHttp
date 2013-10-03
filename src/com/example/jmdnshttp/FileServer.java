package com.example.jmdnshttp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class FileServer extends NanoHTTPD{
	
	File path;

	public FileServer(int port, File rootDir) throws IOException {
		
		
		
		super(port, rootDir);
		
	}
	

	
    public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) {
    	
        File newPath = new File(path.getAbsolutePath() + "/samer");
        Response r = super.serveFile("/index.htm", header, path, true);
        return r;
}
	
	
}