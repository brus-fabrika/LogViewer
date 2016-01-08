package com.revimedia.log;

import javafx.application.Application;
import javafx.stage.Stage;

import com.revimedia.log.view.MainAppCtrl;

public class MainApp extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		boolean isServer = getParameters().getRaw().contains("server");

		MainAppCtrl mainCtrl = new MainAppCtrl();
		mainCtrl.start(primaryStage, isServer);
	}

}
