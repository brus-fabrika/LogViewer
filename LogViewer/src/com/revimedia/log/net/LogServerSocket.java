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
	
	ArrayList<ClientLogPooler> mClients = new ArrayList<>();
	
	ServerSocket mServerSocket;
	Thread mInstanceListRefreshThread;
	
	private volatile boolean isServerActivated = true;

	public LogServerSocket() {
	}
	
	private void init() {
		mInstanceList.scan();
		try {
			for(String fileName : mInstanceList.getMostRecentFileList()) {
				FileTailer tailer = FileTailerPool.getTailerForFile(new File(fileName));
				String instance = mInstanceList.getInstanceForFile(fileName);
				if(instance != null) {
					tailer.addCustomField(instance);
				}
			}
			FileTailerPool.startAllTailers();
		} catch(FileNotFoundException e) {
			log.severe(e.toString());
		}
	}

	private void startRefreshThread() {
		mInstanceListRefreshThread = new Thread(() -> {
			log.info("Instance refresh thread started");
			while(!Thread.currentThread().isInterrupted()) {
				mInstanceList.refresh(); 
				try {
					Thread.sleep(60000L); // TODO: refactor this later
				} catch (InterruptedException e) {
					log.info("Instance refresh thread interrupted");
					break;
				}
			}
			log.info("Instance refresh thread stopped");
		});
		mInstanceListRefreshThread.start();
	}
	
	@Override
	public void run() {
		log.info("Start log server on port: " + mPortNum);
		
		if(mClients.size() == 0) {
			init();
			startRefreshThread();
		}

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
				if(mServerSocket != null) {
					mServerSocket.close();
				}
				if(mInstanceListRefreshThread != null) {
					mInstanceListRefreshThread.interrupt();
				}
			} catch(IOException ignore) {
				log.severe(Arrays.toString(ignore.getStackTrace()));
			}
			FileTailerPool.stopAllTailers();
		}
	}
	
}
