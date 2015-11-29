package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
	
	Map<String, String> mInstanceFiles = new HashMap<>();
	
	ServerSocket mServerSocket;
	Thread mInstanceListRefreshThread;
	
	private volatile boolean isServerActivated = true;

	public LogServerSocket() {
	}

	private void startRefreshThread() {
		mInstanceListRefreshThread = new Thread(() -> {
			log.info("Instance refresh thread started");
			while(!Thread.currentThread().isInterrupted()) {
				mInstanceList.scan();
				try {
					for(String fileName : mInstanceList.getMostRecentFileList()) {
						String instance = mInstanceList.getInstanceForFile(fileName);
						if(!mInstanceFiles.containsKey(instance)) {
							log.info("New instance added to log list: " + instance + "(" + fileName + ")");
							mInstanceFiles.put(instance, fileName);
							FileTailer tailer = FileTailerPool.getTailerForFile(new File(fileName));
							if(instance != null) {
								tailer.addCustomField(instance);
							}
						} else {
							log.info("Check file update for instance: " + instance);
							String prevFileName = mInstanceFiles.get(instance);
							
							if(!FileTailerPool.isFileTailed(prevFileName)) {
								log.severe("Error - file is not tailed but it should: " + prevFileName);
							}
							
							if(prevFileName.equals(fileName)) {
								log.info("Instance already has the most recent file: " + fileName);
							} else {
								log.info("Instance has new the most recent file: " + fileName);
								
								mInstanceFiles.put(instance, fileName);
								FileTailer tailer = FileTailerPool.getTailerForFile(new File(fileName));
								if(instance != null) {
									tailer.addCustomField(instance);
								}
								
								tailer = FileTailerPool.getTailerForFile(prevFileName);
								if(tailer != null) {
									tailer.setActive(false);
								} else {
									log.severe("Error - Tailer object is not found for file: " + prevFileName);
								}
							}
						}
					}
					
					// TODO: remove all unused tailer threads
					
					FileTailerPool.startAllTailers();
					
					Thread.sleep(10000L); // TODO: refactor this later
				} catch (InterruptedException e) {
					log.info("Instance refresh thread interrupted");
					break;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
			//init();
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
