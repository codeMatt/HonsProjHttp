package com.example.jmdnshttp;

import java.io.IOException;

public class ServerRunner {
	
	static NanoHTTPD serverInstance;
	
    public static void run(Class serverClass) {
        try {
            executeInstance((NanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(NanoHTTPD server) {
    	serverInstance = server;
        try {
        	serverInstance.start();
        } catch (IOException ioe) {
            System.out.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }
    }
    
    public static void stopInstance(){
        serverInstance.stop();
        System.out.println("Server stopped....");
    }
    

    
}
