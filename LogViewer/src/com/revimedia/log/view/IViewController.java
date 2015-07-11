package com.revimedia.log.view;

import java.io.File;

public interface IViewController {
	void loadLogData(File logFile);
	boolean loadLogData(String host, int port);
	void stopProcessLogging();
}
