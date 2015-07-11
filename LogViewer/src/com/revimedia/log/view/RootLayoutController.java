package com.revimedia.log.view;

import javafx.fxml.FXML;

import com.revimedia.log.MainApp;

public class RootLayoutController {

	// Reference to the main application.
	private MainApp mainApp;
	
	private boolean isServerConnected = false;
	
	@FXML
	private void handleFileOpenMenu() {
		mainApp.loadLogFile();
	}
	
	@FXML
	private void handleConnectMenu() {
		if(isServerConnected) {
			if(mainApp.serverDisconnect()) {
				isServerConnected = false;
				// TODO: change menu label to connect
			}
		} else {
			if(mainApp.serverConnect()) {
				isServerConnected = true;
				// TODO: change menu label to disconnect
			}
		}
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
