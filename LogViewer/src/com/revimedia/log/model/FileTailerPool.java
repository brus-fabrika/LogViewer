package com.revimedia.log.model;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.revimedia.log.util.Configuration;

public class FileTailerPool {
	private static final long FILE_POOLING_INTERVAL =
		Configuration.getInstance().getPropertyAsInt("pooling_interval", 5000);
	
	private static HashMap<String, FileTailer> mTailerPool = new HashMap<>();
	
	private static Set<IFileTailerListener> mClients = new HashSet<>();
	
	private static FileTailerPool mInstance;
	
	private void addFileTailerForFile(File logFile) {
		if(mTailerPool.containsKey(logFile.getAbsolutePath())) return;

		FileTailer fileTailer = new FileTailer(logFile, FILE_POOLING_INTERVAL, false);
		
		for(IFileTailerListener l: mClients) {
			fileTailer.addLogFileTailerListener(l);
		}

		mTailerPool.put(logFile.getAbsolutePath(), fileTailer);
	}
	
	public static boolean isFileTailed(String fileName) {
		return mTailerPool.containsKey(fileName);
	}
	
	public static FileTailer getTailerForFile(File logFile) {
		
		if(mInstance == null) {
			mInstance = new FileTailerPool();
		}
		
		mInstance.addFileTailerForFile(logFile);

		return mTailerPool.get(logFile.getAbsolutePath());
	}

	public static FileTailer getTailerForFile(String fileName) {
		return mTailerPool.get(fileName);
	}
	
	public static void addNewListener(IFileTailerListener clientLogPooler) {
		boolean isNew = mClients.add(clientLogPooler);
		if(isNew) {
			for(FileTailer tailer : mTailerPool.values()) {
				tailer.addLogFileTailerListener(clientLogPooler);
			}
		}
	}

	public static void stopAllTailers() {
		for(FileTailer tailer : mTailerPool.values()) {
			tailer.stopTailing();
			try {
				tailer.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void startAllTailers() {
		for(FileTailer tailer : mTailerPool.values()) {
			if(!tailer.isAlive()) {
				tailer.start();
			}
		}
	}
}
