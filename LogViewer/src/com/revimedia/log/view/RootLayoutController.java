package com.revimedia.log.view;

import javafx.fxml.FXML;

import com.revimedia.log.MainApp;

public class RootLayoutController {

	// Reference to the main application.
	private MainApp mainApp;
	
	@FXML
	private void handleFileOpenMenu() {
		mainApp.loadLogFile();
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
