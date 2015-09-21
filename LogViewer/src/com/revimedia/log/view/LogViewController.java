package com.revimedia.log.view;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.IFileTailerListener;
import com.revimedia.log.model.LogEntry;
import com.revimedia.log.net.LogClientSocket;

public class LogViewController implements IFileTailerListener, IViewController{

	private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private static final int FILE_POOLING_INTERVAL = 5000;
	@FXML
	private TableView<LogEntry> mLogTable;
	@FXML
	private TableColumn<LogEntry, Number> mLineNumberColumn;
	@FXML
	private TableColumn<LogEntry, String> mPayloadColumn;
	@FXML
	private TableColumn<LogEntry, String> mTimeColumn;
	@FXML
	private TableColumn<LogEntry, String> mInstanceColumn;
	@FXML
	private TextField mRegexFilterText;
	
	private ArrayList<LogEntry> mLogsList = new ArrayList<>();
	
	private ObservableList<LogEntry> mLogs = FXCollections.observableArrayList();
	
	private String mRegex;
	private Pattern mRegexPattern;
	private boolean isRegexModeOff = true;
	
	private FileTailer mLogFileTailer;
	private Thread mLogFileTailerThread;

	private LogClientSocket mClientSocket;
	
	@FXML
	private void initialize() {
		mLineNumberColumn.setCellValueFactory(cellData -> cellData.getValue().lineNumberProperty());
		mPayloadColumn.setCellValueFactory   (cellData -> cellData.getValue().payloadProperty());
		mTimeColumn.setCellValueFactory      (cellData -> cellData.getValue().timestampProperty());
		mInstanceColumn.setCellValueFactory  (cellData -> cellData.getValue().instanceProperty());
		mLineNumberColumn.setMinWidth(50);
		
		mLogTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		mLogTable.setItems(mLogs);
	}
	
	@FXML
	private void onRegexUpdate() {
		System.out.println("LogViewTableViewController.onRegexUpdate()");
		
		isRegexModeOff = mRegexFilterText.getText().isEmpty();
		
		if(isRegexModeOff) {
			mRegexPattern = null;
			mLogs.clear();
			if(!mLogsList.isEmpty()) {
				mLogs.setAll(mLogsList);
			}
			return;
		}
		
		if(mRegexFilterText.getText().equals(mRegex)) return;
		
		mRegex = "(" + mRegexFilterText.getText() + ")";
		mRegexPattern = Pattern.compile(mRegex);
		
		mLogs.clear();
		
		for(LogEntry log: mLogsList) {
			Matcher m = mRegexPattern.matcher(log.getPayload());
			if(m.find()) {
				mLogs.add(log);
			}
		}
	}
	
	@Override
	public void loadLogData(File logFile) {
		clearLogView();
		
		if(mLogFileTailer != null) {
			mLogFileTailer.stopTailing();
			try {
				mLogFileTailerThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mLogFileTailer = new FileTailer(logFile, FILE_POOLING_INTERVAL, true);
		mLogFileTailer.addLogFileTailerListener(this);
		
		mLogFileTailerThread = new Thread(mLogFileTailer);
		mLogFileTailerThread.start();
	}

	public ObservableList<LogEntry> getLogs() {
		return mLogs;
	}

	@Override
	public void onFileUpdate(String line) {
		LogEntry e = new LogEntry(line, mLogsList.size()+1);
		mLogsList.add(e);
		
		if(isRegexModeOff || mRegexPattern.matcher(e.getPayload()).find()) {
			mLogs.add(e);
		}
	}

	public void onCtrlC() {
		System.out.println( "Ctrl-C pressed !!!" );
		
		ObservableList<LogEntry> selectedRows = mLogTable.getSelectionModel().getSelectedItems();
		StringBuilder clipContent = new StringBuilder();
		for(LogEntry log: selectedRows) {
			clipContent.append(String.format("%s\t%s\t%s\n",
				log.getInstance() == null ? "" : log.getTimeStamp(),
				log.getTimeStamp() == null ? "" : log.getTimeStamp(),
				log.getPayload() == null ? "" : log.getPayload()));
		}
		
		if(clipContent.length() > 0) {
			final ClipboardContent content = new ClipboardContent();
			content.putString(clipContent.toString());
			Clipboard.getSystemClipboard().setContent(content);
		}
	}
	
	@Override
	public void stopProcessLogging() {
		if(mLogFileTailer != null) {
			mLogFileTailer.stopTailing();
		}
		if(mClientSocket != null) {
			mClientSocket.disconnect();
		}
	}
	
	private void clearLogView() {
		mRegexFilterText.clear();
		mLogs.clear();
		isRegexModeOff = true;
		mRegexPattern = null;
		
		mLogsList = new ArrayList<>();
	}

	@Override
	public boolean loadLogData(String host, int port) {
		clearLogView();
		
		log.info("connect to server with "+ host +":" + port);
		
		mClientSocket = new LogClientSocket(host, port, this);
		boolean isConnected = mClientSocket.tryConnect();
		if(isConnected) {
			new Thread(mClientSocket).start();
		} else {
			log.severe("connect to server with "+ host +":" + port + " FAILED");
		}
		
		return isConnected;
	}
}
