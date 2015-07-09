package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.util.Configuration;

public class LogServerSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private File mLogFile;

	ArrayList<ClientLogPooler> mClients = new ArrayList<>();

	private boolean isServerActivated = true;
	
	public LogServerSocket(File logFile){
		this.mLogFile = logFile;
	}
	
	@Override
	public void run() {
		int portNum = Configuration.getInstance().getPropertyAsInt("port", 4444);
		log.info("Start log server on port: " + portNum);
		try (ServerSocket serverSocket = new ServerSocket(portNum);) {
			while(isServerActivated ) {
				Socket clientSocket = serverSocket.accept();
				log.info("Client socket connected: " + clientSocket.getInetAddress());
				
				mClients.add(new ClientLogPooler(clientSocket, mLogFile));
			}
		} catch (IOException e) {
			log.severe(Arrays.toString(e.getStackTrace()));
		}
	}

	public void stopServer() {
		isServerActivated = false; // TODO: this will not work as accept is a blocking call.
									// so we can never ever go out from waiting for socket loop
	}
	
}
