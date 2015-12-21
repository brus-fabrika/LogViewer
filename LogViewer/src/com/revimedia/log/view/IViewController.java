package com.revimedia.log.view;

import java.io.File;

import com.revimedia.log.model.LogEntry;

public interface IViewController {
	void loadLogData(File logFile);
	boolean loadLogData(String host, int port);
	void stopProcessLogging();
	LogEntry[] getAll();
	void clearLogData();
	void selectLog(LogEntry rowData);
}
