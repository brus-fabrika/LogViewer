package com.revimedia.log.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.FileTailerPool;
import com.revimedia.log.model.LxpInstanceList;
import com.revimedia.log.util.Configuration;

public class LogServerSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	final private int mPortNum = Configuration.getInstance().getPropertyAsInt("port", 4444);
	
	private LxpInstanceList mInstanceList = new LxpInstanceList();
	
	private File mLogFile; // TODO: remove

	ArrayList<ClientLogPooler> mClients = new ArrayList<>();
	
	ServerSocket mServerSocket;

	private volatile boolean isServerActivated = true;

	public LogServerSocket(File logFile){
		this.mLogFile = logFile;  // TODO: remove
	}
	
	public LogServerSocket() {
		mInstanceList.scan();
		try {
			for(String fileName : mInstanceList.getMostRecentFileList()) {
				FileTailer tailer = FileTailerPool.getTailerForFile(new File(fileName));
				String instance = mInstanceList.getInstanceForFile(fileName);
				if(instance != null) {
					tailer.addCustomField(instance);
				}
			}
		} catch(FileNotFoundException e) {
			log.severe(e.toString());
		}
		
	}

	@Override
	public void run() {
		log.info("Start log server on port: " + mPortNum);
		
		try {
			mServerSocket = new ServerSocket(mPortNum);
			while(isServerActivated) {
				Socket clientSocket = mServerSocket.accept();
				log.info("Client socket connected: " + clientSocket.getInetAddress());
				mClients.add(new ClientLogPooler(clientSocket));
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
			FileTailerPool.stopAllTailers();
		}
	}
	
}
