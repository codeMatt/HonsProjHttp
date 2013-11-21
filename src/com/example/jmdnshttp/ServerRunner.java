package com.example.jmdnshttp;

import java.io.IOException;

public class ServerRunner {
	
	static NanoHTTPD serverInstance;
	
    @SuppressWarnings("rawtypes")
	public static void run(Class serverClass) {
        try {
            executeInstance((NanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(NanoHTTPD server) {
    	System.out.println("serverrunner calling server");
    	serverInstance = server;
        try {
        	serverInstance.start();
        } catch (IOException ioe) {
            System.out.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }
    }
    
    public static void stopInstance() throws NullPointerException{
    	if(serverInstance != null)
    		serverInstance.stop();
        System.out.println("Server stopped....");
    }
    

    
}
