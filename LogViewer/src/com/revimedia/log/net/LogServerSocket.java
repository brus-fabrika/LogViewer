package com.revimedia.log.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogServerSocket implements Runnable{
	private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@Override
	public void run() {
		log.info("Try to create socket");
		try (ServerSocket serverSocket = new ServerSocket(4444);) {
			Socket clientSocket = serverSocket.accept();
			log.info("Socket connected in SERVER mode");
			
			//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			//BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
