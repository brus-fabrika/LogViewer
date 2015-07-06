package com.revimedia.log.view;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.net.LogServerSocket;


public class DebugViewController implements IViewController {
	private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void loadLogData(File logFile) {
		log.info("File opened in SERVER mode");
		openSocket();
	}

	private void openSocket() {
		log.info("Start server socket thread");
		Thread socketThread = new Thread(new LogServerSocket(), "ServerSocket");
		socketThread.start();
	}
	
}
