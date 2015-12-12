package com.revimedia.log.view;

import java.io.File;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.FileTailerPool;
import com.revimedia.log.model.IFileTailerListener;
import com.revimedia.log.model.InstanceFilter;
import com.revimedia.log.model.LogEntry;
import com.revimedia.log.model.LogFilters;
import com.revimedia.log.net.LogClientSocket;

public class LogViewController implements IFileTailerListener
					, IViewController {

	final private static Logger LOG = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
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
	
	private ObservableList<LogEntry> mLogs = FXCollections.observableArrayList();
	
	private FileTailer mLogFileTailer;

	private LogClientSocket mClientSocket;
	
	private LogFilters mLogFilters = new LogFilters();
	private InstanceFilter mInstanceFilter = new InstanceFilter();
	
	private INewInstanceLogHandler mNewInstanceLogHandler;
	
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
	
	@Override
	public void loadLogData(File logFile) {
		clearLogView();
		
		if(mLogFileTailer != null) {
			mLogFileTailer.stopTailing();
			mLogFileTailer.setActive(false);
			FileTailerPool.stopAllInactiveTailers();
		}
		
		mLogFileTailer = FileTailerPool.getTailerForFile(logFile);
		
		Pattern pattern = Pattern.compile("([A-Z]+)(\\d.+)([a,p]m[1,2])");
		Matcher m = pattern.matcher(logFile.getName());
		
		if(m.find()) {
			LOG.info("File opened for instance " + m.group(1));
			mLogFileTailer.addCustomField(m.group(1));
		} else {
			mLogFileTailer.addCustomField("?");
		}

		mLogFileTailer.addLogFileTailerListener(this);
		
		FileTailerPool.startAllTailers();
	}

	public ObservableList<LogEntry> getLogs() {
		return mLogs;
	}

	@Override
	public void onFileUpdate(String line) {
		boolean passed = mLogFilters.check(line);
		if(passed) {
			LogEntry e = new LogEntry(line, mLogs.size()+1);
			mLogs.add(e);
			mNewInstanceLogHandler.handleNewInstance(e.getInstance());;
		}
	}

	public void onCtrlC() {
		LOG.info( "Ctrl-C pressed !!!" );
		
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
		mLogs.clear();
	}

	@Override
	public boolean loadLogData(String host, int port) {
		clearLogView();
		
		LOG.info("connect to server with "+ host +":" + port);
		
		mClientSocket = new LogClientSocket(host, port, this);
		boolean isConnected = mClientSocket.tryConnect();
		if(isConnected) {
			new Thread(mClientSocket).start();
		} else {
			LOG.severe("connect to server with "+ host +":" + port + " FAILED");
		}
		
		return isConnected;
	}

	@Override
	public LogEntry[] getAll() {
		return mLogs.toArray(new LogEntry[0]);
	}

	public void instanceChecked(String instanceName, boolean isChecked) {
		LOG.info("Toggle filter for instance " + instanceName);
		if(isChecked) {
			mInstanceFilter.removeCondition(instanceName);
		} else {
			mInstanceFilter.addCondition(instanceName);
		}
	}

	public void addNewInstanceHandler(INewInstanceLogHandler handler) {
		mNewInstanceLogHandler = handler;
	}
}
