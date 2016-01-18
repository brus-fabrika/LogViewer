package com.revimedia.log.view;

import java.io.IOException;

import com.revimedia.log.util.Configuration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
	private void handleConnectConfigureMenu(ActionEvent event) {
		Stage connectionListStage = new Stage();
		connectionListStage.initStyle(StageStyle.UTILITY);
		connectionListStage.setResizable(false);
		connectionListStage.setTitle("Connection List");
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ConnectionsListView.fxml"));
			BorderPane connectionsListView = (BorderPane) loader.load();
			
			Scene scene = new Scene(connectionsListView);
			connectionListStage.setScene(scene);
			connectionListStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
