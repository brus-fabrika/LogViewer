package com.revimedia.log.view;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.FileTailerListener;
import com.revimedia.log.model.LogEntry;

public class LogViewController implements FileTailerListener{

	private static final int FILE_POOLING_INTERVAL = 1000;
	@FXML
	private TableView<LogEntry> mLogTable;
	@FXML
	private TableColumn<LogEntry, String> mPayloadColumn;
	@FXML
	private TableColumn<LogEntry, String> mTimeColumn;
	@FXML
	private TextField mRegexFilterText;
	
	private ArrayList<LogEntry> mLogsList = new ArrayList<>();
	
	private ObservableList<LogEntry> mLogs = FXCollections.observableArrayList();
	
	private String mRegex;
	
	FileTailer mLogFileTailer;
	
	@FXML
	private void initialize() {
		mPayloadColumn.setCellValueFactory(cellData -> cellData.getValue().payloadProperty());
		mTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
	}
	
	@FXML
	private void onRegexUpdate() {
		System.out.println("LogViewTableViewController.onRegexUpdate()");
		
		if(mRegexFilterText.getText().isEmpty()) return;
		if(mRegexFilterText.getText().equals(mRegex)) return;
		
		mRegex = "(" + mRegexFilterText.getText() + ")";
		
		System.out.println("LogViewTableViewController.onRegexUpdate(): Regex = " + mRegex);
		
		Pattern pattern = Pattern.compile(mRegex);
		
		mLogs.clear();
		
		for(LogEntry log: mLogsList) {
			Matcher m = pattern.matcher(log.getPayload());
			if(m.find()) {
				mLogs.add(log);
			}
		}
	}

	public void loadLogData(File logFile) {
		mRegexFilterText.clear();
		mLogs.clear();
		
		mLogsList = new ArrayList<>();
		
		mLogTable.setItems(mLogs);
		
		if(mLogFileTailer != null) {
			mLogFileTailer.stopTailing();
			try {
				mLogFileTailer.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mLogFileTailer = new FileTailer(logFile, FILE_POOLING_INTERVAL, true);
		mLogFileTailer.addLogFileTailerListener(this);
		
		mLogFileTailer.start();
		
	}

	public ObservableList<LogEntry> getLogs() {
		return mLogs;
	}

	@Override
	public void newFileLine(String line) {
		System.out.println( line );
		LogEntry e = new LogEntry(line);
		mLogsList.add(e);
		mLogs.add(e);
	}

	public void stopProcessLogging() {
		System.out.println("Stop process the log");
		mLogFileTailer.stopTailing();
	}
}
