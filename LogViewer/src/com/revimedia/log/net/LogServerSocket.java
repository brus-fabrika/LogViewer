package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.FileTailerListener;
import com.revimedia.log.util.Configuration;

public class LogServerSocket implements Runnable, FileTailerListener {
	private static final long FILE_POOLING_INTERVAL =
			Configuration.getInstance().getPropertyAsInt("pooling_interval", 4444);;

	private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private File mLogFile;

	private FileTailer mLogFileTailer;

	private Thread mLogFileTailerThread;

	private PrintWriter mOutWriter;
	
	public LogServerSocket(File logFile){
		this.mLogFile = logFile;
	}
	
	@Override
	public void run() {
		int portNum = Configuration.getInstance().getPropertyAsInt("default_port", 4444);
		log.info("Try to create socket on port: ");
		try (ServerSocket serverSocket = new ServerSocket(portNum);) {
			Socket clientSocket = serverSocket.accept();
			log.info("Socket connected in SERVER mode");
			
			mOutWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			
			mLogFileTailer = new FileTailer(mLogFile, FILE_POOLING_INTERVAL, true);
			mLogFileTailer.addLogFileTailerListener(this);
			
			mLogFileTailerThread = new Thread(mLogFileTailer);
			mLogFileTailerThread.start();
			
			mLogFileTailerThread.join();
			
			mOutWriter.flush();
			clientSocket.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNewFileLine(String line) {
		mOutWriter.println(line);
		
	}

}
