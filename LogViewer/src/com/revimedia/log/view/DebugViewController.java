package com.revimedia.log.view;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.LogEntry;
import com.revimedia.log.net.LogServerSocket;


public class DebugViewController implements IViewController {
	private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Thread mSocketThread;
	private LogServerSocket mLogServer;

	@Override
	public void loadLogData(File logFile) {
		log.info("File opened in SERVER mode");
		openSocketForFile(logFile);
	}

	private void openSocketForFile(File logFile) {
		log.info("Start server socket thread");
		mLogServer = new LogServerSocket();
		mSocketThread = new Thread(mLogServer, "ServerSocket");
		mSocketThread.start();
	}

	@Override
	public void stopProcessLogging() {
		if(mLogServer != null) {
			mLogServer.stopServer();
			try {
				mSocketThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("Log server terminated");
	}

	@Override
	public boolean loadLogData(String host, int port) {
		log.info("try to start server");
		mLogServer = new LogServerSocket();
		mSocketThread = new Thread(mLogServer, "ServerSocket");
		mSocketThread.start();
		
		return true;
	}

	@Override
	public LogEntry[] getAll() {
		return null; //TODO throw NotYetImplemented
	}

	@Override
	public void clearLogData() {
		// TODO Auto-generated method stub
	}

	@Override
	public void selectLog(LogEntry rowData) {
		// TODO Auto-generated method stub
		
	}
	
}
