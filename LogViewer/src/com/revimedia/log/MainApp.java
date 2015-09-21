package com.revimedia.log;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.revimedia.log.util.Configuration;
import com.revimedia.log.view.IViewController;
import com.revimedia.log.view.LogViewController;
import com.revimedia.log.view.RootLayoutController;
import com.revimedia.log.view.SearchResultsTableViewController;

public class MainApp extends Application {

	{
		Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
		log.setUseParentHandlers(false);
		log.setLevel(Level.FINE);
		Handler h = new ConsoleHandler();
		h.setLevel(Level.FINE);
		log.addHandler(h);
	}
	
	private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private Stage primaryStage;
	private BorderPane rootLayout;

	private IViewController logViewController;
	SearchResultsTableViewController mSearchViewCtrl;
	
	private Configuration mAppConfig;
	
	/**
	 * Constructor
	 */
	public MainApp() {
		mAppConfig = Configuration.getInstance();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		String iconPath = getClass().getClassLoader().getResource("images").toString();
		this.primaryStage.getIcons().add(new Image(iconPath + File.separator + mAppConfig.getProperty("app.icon")));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		
		if(getParameters().getRaw().contains("server")) {
			this.primaryStage.setWidth(width/4);
			this.primaryStage.setHeight(height/4);
		} else {
			this.primaryStage.setWidth(width/2);
			this.primaryStage.setHeight(height/2);
		}
		
		initRootLayout();

		if(getParameters().getRaw().contains("client")) {
			this.primaryStage.setTitle(mAppConfig.getProperty("app.name") + " - Client");
			showLogView();
		} else if(getParameters().getRaw().contains("server")) {
			this.primaryStage.setTitle(mAppConfig.getProperty("app.name") + " - Server");
			showDebugView();
		} else {
		}
	}

	private void showDebugView() {
		log.config("App is loaded in SERVER mode, show only debug info");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/DebugView.fxml"));
			BorderPane debugView = (BorderPane) loader.load();

			logViewController = loader.getController();
			
			this.primaryStage.setOnCloseRequest( event -> logViewController.stopProcessLogging() );
			
			// Set person overview into the center of root layout.
			rootLayout.setCenter(debugView);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			RootLayoutController ctrl = loader.getController();
			ctrl.setMainApp(this);
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showLogView() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/LogView.fxml"));
			BorderPane logView = (BorderPane) loader.load();

			LogViewController logViewCtrl = loader.getController();
			
			FXMLLoader loader2 = new FXMLLoader();
			loader2.setLocation(getClass().getResource("view/SearchResultsTableView.fxml"));
			BorderPane searchResultsView = (BorderPane) loader2.load();
			
			
			mSearchViewCtrl = loader2.getController();
			
			final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
			
			logView.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
				if (keyComb1.match(event)) {
					logViewCtrl.onCtrlC();
				}
			});
			
			this.primaryStage.setOnCloseRequest( event -> logViewCtrl.stopProcessLogging() );
			
			// Set person overview into the center of root layout.
			rootLayout.setCenter(logView);
			
			rootLayout.setBottom(searchResultsView);
			
//			searchResultsView.setVisible(false);
			
			logViewController = logViewCtrl;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void loadLogFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(mAppConfig.getProperty("default_path")));
		File logFile = fileChooser.showOpenDialog(primaryStage);
		if(logFile != null) {
			logViewController.loadLogData(logFile);
		}
	}

	public boolean serverConnect() {
		log.info("connect");
		String host = mAppConfig.getProperty("host");
		int port = mAppConfig.getPropertyAsInt("port", 4444);
		return logViewController.loadLogData(host, port);
	}

	public boolean serverDisconnect() {
		log.info("disconnect");
		logViewController.stopProcessLogging();
		return true;
	}
}
