package com.revimedia.log.model;

import java.io.File;
import java.util.HashMap;

import com.revimedia.log.util.Configuration;

public class FileTailerPool {
	private static final long FILE_POOLING_INTERVAL =
			Configuration.getInstance().getPropertyAsInt("pooling_interval", 5000);
	
	private static HashMap<String, FileTailer> mTailerPool = new HashMap<>();
	
	private static FileTailerPool mInstance;
	
	private void addFileTailerForFile(File logFile) {
		if(mTailerPool.containsKey(logFile.getAbsolutePath())) return;
		
		FileTailer fileTailer = new FileTailer(logFile, FILE_POOLING_INTERVAL, true);
		fileTailer.start();
		
		mTailerPool.put(logFile.getAbsolutePath(), fileTailer);
	}
	
	public static FileTailer getTailerForFile(File logFile) {
		
		if(mInstance == null) {
			mInstance = new FileTailerPool();
		}
		
		mInstance.addFileTailerForFile(logFile);

		return mTailerPool.get(logFile.getAbsolutePath());
	}
	
}
