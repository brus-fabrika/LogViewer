package com.revimedia.log.view;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
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
	private void handleConnectMenu() {
		if(isServerConnected) {
			if(mainApp.serverDisconnect()) {
				isServerConnected = false;
				// TODO: change menu label to connect
				Menu cons = mMainMenu.getMenus().get(1);
				MenuItem con = cons.getItems().get(0);
				
				con.setText("Connect");
				
			}
		} else {
			if(mainApp.serverConnect()) {
				isServerConnected = true;
				Menu cons = mMainMenu.getMenus().get(1);
				MenuItem con = cons.getItems().get(0);
				
				con.setText("Disconnect");
			}
		}
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
