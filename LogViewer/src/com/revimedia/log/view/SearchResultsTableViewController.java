package com.revimedia.log.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;

import com.revimedia.log.model.LogEntry;

public class SearchResultsTableViewController {
	@FXML
	private TableView<LogEntry> mLogTable;
	@FXML
	private TableColumn<LogEntry, Number> mLineNumberColumn;
	@FXML
	private TableColumn<LogEntry, String> mPayloadColumn;
	@FXML
	private TableColumn<LogEntry, String> mTimeColumn;
	@FXML
	private TextField mRegexFilterText;
	
	private ObservableList<LogEntry> mLogs = FXCollections.observableArrayList();
	private boolean isRegexModeOff;
	
	
	private Pattern mRegexPattern;
	private String mRegex;
	private IViewController mParrentViewCtrl;
	
	@FXML
	private void initialize() {
		mLineNumberColumn.setCellValueFactory(cellData -> cellData.getValue().lineNumberProperty());
		mPayloadColumn.setCellValueFactory(cellData -> cellData.getValue().payloadProperty());
		mTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
		
		mLineNumberColumn.setMinWidth(50);
		
		mLogTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		mLogTable.setItems(mLogs);
		
		mLogTable.setRowFactory( tv -> {
			TableRow<LogEntry> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					LogEntry rowData = row.getItem();
					mParrentViewCtrl.selectLog(rowData);
				}
			});
			return row;
		});
		
		mLogTable.setOnKeyReleased(event -> {
			if(event.getCode() == KeyCode.ENTER) {
				LogEntry rowData = mLogTable.getSelectionModel().getSelectedItem();
				if(rowData != null) {
					mParrentViewCtrl.selectLog(rowData);
				}
			}
		});
	}
	
	@FXML
	private void onRegexUpdate() {
		System.out.println("SearchResultsTableViewController.onRegexUpdate()");
		
		isRegexModeOff = mRegexFilterText.getText().isEmpty();
		
		if(isRegexModeOff) {
			mRegexPattern = null;
			clearLogData();
			return;
		}
		
		if(mRegexFilterText.getText().equals(mRegex)) return;
		
		mRegex = "(" + mRegexFilterText.getText() + ")";
		mRegexPattern = Pattern.compile(mRegex);
		
		LogEntry[] mLogsList = mParrentViewCtrl.getAll();
		for(LogEntry log: mLogsList) {
			Matcher m = mRegexPattern.matcher(log.getPayload());
			if(m.find()) {
				addResultLog(log);
			}
		}
	}
	
	@FXML
	private void onSearchUpdateClicked(ActionEvent event) {
		mLogs.clear();
		
		LogEntry[] mLogsList = mParrentViewCtrl.getAll();
		for(LogEntry log: mLogsList) {
			Matcher m = mRegexPattern.matcher(log.getPayload());
			if(m.find()) {
				addResultLog(log);
			}
		}
	}
	
	@FXML
	private void onSearchClearClicked(ActionEvent event) {
		clearLogData();
	}
	
	public void addResultLog(LogEntry logLine) {
		mLogs.add(logLine);
	}

	public void clearLogData() {
		mRegexFilterText.clear();
		mLogs.clear();
		
	}
	
	public void setParentView(IViewController parentCtrl) {
		mParrentViewCtrl = parentCtrl;
	}

	public void onCtrlC() {
		System.out.println( "Search results: Ctrl-C pressed !!!" );
		
		ObservableList<LogEntry> selectedRows = mLogTable.getSelectionModel().getSelectedItems();
		StringBuilder clipContent = new StringBuilder();
		for(LogEntry log: selectedRows) {
			clipContent.append(String.format("%s\t%s\n",
				log.getTimeStamp() == null ? "" : log.getTimeStamp(),
				log.getPayload() == null ? "" : log.getPayload()));
		}
		
		if(clipContent.length() > 0) {
			final ClipboardContent content = new ClipboardContent();
			content.putString(clipContent.toString());
			Clipboard.getSystemClipboard().setContent(content);
		}
		
	}
}
