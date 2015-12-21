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
	final static private Logger LOG = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	final static private int PORT_NUMBER = Configuration.getInstance().getPropertyAsInt("port", 4444);
	final static private long FILELIST_REFRESH_INTERVAL = Configuration.getInstance().getPropertyAsInt("filelist_refresh_interval", 10000);
	
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
			LOG.info("Instance refresh thread started");
			while(!Thread.currentThread().isInterrupted()) {
				mInstanceList.scan();
				try {
					for(String fileName : mInstanceList.getMostRecentFileList()) {
						String instance = mInstanceList.getInstanceForFile(fileName);
						if(!mInstanceFiles.containsKey(instance)) {
							LOG.info("New instance added to log list: " + instance + "(" + fileName + ")");
							mInstanceFiles.put(instance, fileName);
							FileTailer tailer = FileTailerPool.getTailerForFile(new File(fileName));
							if(instance != null) {
								tailer.addCustomField(instance);
							}
						} else {
							LOG.info("Check file update for instance: " + instance);
							String prevFileName = mInstanceFiles.get(instance);
							
							if(!FileTailerPool.isFileTailed(prevFileName)) {
								LOG.severe("Error - file is not tailed but it should: " + prevFileName);
							}
							
							if(prevFileName.equals(fileName)) {
								LOG.info("Instance already has the most recent file: " + fileName);
							} else {
								LOG.info("Instance has new the most recent file: " + fileName);
								
								mInstanceFiles.put(instance, fileName);
								FileTailer tailer = FileTailerPool.getTailerForFile(new File(fileName));
								if(instance != null) {
									tailer.addCustomField(instance);
								}
								
								tailer = FileTailerPool.getTailerForFile(prevFileName);
								if(tailer != null) {
									tailer.setActive(false);
								} else {
									LOG.severe("Error - Tailer object is not found for file: " + prevFileName);
								}
							}
						}
					}
					
					FileTailerPool.stopAllInactiveTailers();
					
					FileTailerPool.startAllTailers();
					
					Thread.sleep(FILELIST_REFRESH_INTERVAL);
				} catch (InterruptedException e) {
					LOG.info("Instance refresh thread interrupted");
					break;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			LOG.info("Instance refresh thread stopped");
		});
		mInstanceListRefreshThread.start();
	}
	
	@Override
	public void run() {
		LOG.info("Start log server on port: " + PORT_NUMBER);
		
		if(mClients.size() == 0) {
			startRefreshThread();
		}

		try {
			mServerSocket = new ServerSocket(PORT_NUMBER);
			while(isServerActivated) {
				Socket clientSocket = mServerSocket.accept();
				LOG.info("Client socket connected: " + clientSocket.getInetAddress());
				
				mClients.add(new ClientLogPooler(clientSocket));
			}
		} catch(SocketException e) {
			LOG.warning("Server interrupted");
		} catch (IOException e) {
			LOG.severe(Arrays.toString(e.getStackTrace()));
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
				LOG.severe(Arrays.toString(ignore.getStackTrace()));
			}
			FileTailerPool.stopAllTailers();
		}
	}
	
}
