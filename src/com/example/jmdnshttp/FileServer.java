package com.example.jmdnshttp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class FileServer{

	File rootDir;
	int portNo;
	boolean noLogs;
	SimpleWebServer webServer;
	
	public FileServer(int port, File path) throws IOException {
		noLogs = false;
		portNo = port;
		rootDir = new File(path, "root");
	}
	
	/**
	 * Override this to customize the server.
	 * <p>
	 * 
	 * (By default, this delegates to serveFile() and allows directory listing.)
	 * 
	 * @parm uri Percent-decoded URI without parameters, for example
	 *       "/index.cgi"
	 * @parm method "GET", "POST" etc.
	 * @parm parms Parsed, percent decoded parameters from URI and, in case of
	 *       POST, data.
	 * @parm header Header entries, percent decoded
	 * @return HTTP response, see class Response for details
	 */
    public void startServer() {

    	webServer = new SimpleWebServer(null, portNo, rootDir, noLogs);
    	
    	ServerRunner.executeInstance(webServer);
    	
	}
    
    public void stopServer(){
    	ServerRunner.stopInstance();
    } 
	
	
}