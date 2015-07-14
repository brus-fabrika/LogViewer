package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.util.Configuration;

public class LogClientSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	final private int mPortNum = Configuration.getInstance().getPropertyAsInt("port", 4444);
	
	private File mLogFile;

	ArrayList<ClientLogPooler> mClients = new ArrayList<>();
	
	ServerSocket mServerSocket;

	private boolean isServerActivated = true;

	public LogClientSocket(File logFile){
		this.mLogFile = logFile;
	}
	
	@Override
	public void run() {
		log.info("Start log server on port: " + mPortNum);
		
		try {
			mServerSocket = new ServerSocket(mPortNum);
			while(isServerActivated) {
				Socket clientSocket = mServerSocket.accept();
				log.info("Client socket connected: " + clientSocket.getInetAddress());
				
				mClients.add(new ClientLogPooler(clientSocket, mLogFile));
			}
		} catch(SocketException e) {
			log.warning("Server interrupted");
		} catch (IOException e) {
			log.severe(Arrays.toString(e.getStackTrace()));
		}
	}

	public void stopServer() {
		isServerActivated = false;
		if(mServerSocket != null) {
			try {
				mServerSocket.close();
			} catch(IOException ignore) {
				log.severe(Arrays.toString(ignore.getStackTrace()));
			}
		}
	}
	
}
