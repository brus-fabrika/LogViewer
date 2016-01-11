package com.revimedia.log.view;

import com.revimedia.log.util.Configuration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class RootLayoutController {

	// Reference to the main application.
	private MainAppCtrl mainApp;
	
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
	
	@FXML
	private void handleDefaultConnectMenu(ActionEvent event) {
		if(isServerConnected) {
			if(mainApp.serverDisconnect()) {
				mainApp.getPrimaryStage().setTitle(Configuration.getInstance().getProperty("app.name") + " - Client");
				isServerConnected = false;
			}
		} else {
			if(mainApp.serverConnect()) {
				mainApp.getPrimaryStage().setTitle(Configuration.getInstance().getProperty("app.name") + " - Client connected");
				isServerConnected = true;
			}
		}
	}
	
	@FXML
	private void handleClearLogMenu(ActionEvent event) {
		mainApp.clearLogView();
	}
	
	public void setMainApp(MainAppCtrl mainApp) {
		this.mainApp = mainApp;
	}
}
