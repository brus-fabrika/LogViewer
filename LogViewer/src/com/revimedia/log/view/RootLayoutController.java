package com.revimedia.log.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import com.revimedia.log.MainApp;

public class RootLayoutController {

	// Reference to the main application.
	private MainApp mainApp;
	
	private boolean isServerConnected = false;
	
	@FXML
	private MenuBar mMainMenu;
	
	@FXML
	private void handleFileOpenMenu() {
		mainApp.loadLogFile();
	}
	
	@FXML
	private void handleConnectMenu(ActionEvent event) {
		MenuItem con = (MenuItem) event.getSource();
		if(isServerConnected) {
			if(mainApp.serverDisconnect()) {
				isServerConnected = false;
				con.setText("Connect");
			}
		} else {
			if(mainApp.serverConnect()) {
				isServerConnected = true;
				con.setText("Disconnect");
			}
		}
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
