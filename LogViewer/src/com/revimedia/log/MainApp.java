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
import com.revimedia.log.view.LogViewController;
import com.revimedia.log.view.RootLayoutController;

public class MainApp extends Application {

	{
		Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
		log.setLevel(Level.FINE);
		Handler h = new ConsoleHandler();
		h.setLevel(Level.FINE);
		log.addHandler(h);
	}
	
	private Stage primaryStage;
	private BorderPane rootLayout;

	private LogViewController logViewController;
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
		this.primaryStage.setTitle(mAppConfig.getPropertyValue("app.name"));
		String iconPath = getClass().getClassLoader().getResource("images").toString();
		this.primaryStage.getIcons().add(new Image(iconPath + File.separator + mAppConfig.getPropertyValue("app.icon")));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		this.primaryStage.setWidth(width/1.5);
		this.primaryStage.setHeight(height/1.5);
		
		initRootLayout();

		showLogView();
		
		this.primaryStage.setOnCloseRequest( event -> logViewController.stopProcessLogging() );
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

			logViewController = loader.getController();
			
			final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
			
			logView.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
				if (keyComb1.match(event)) {
					logViewController.onCtrlC();
				}
			});
			
			
			
			// Set person overview into the center of root layout.
			rootLayout.setCenter(logView);

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
		fileChooser.setInitialDirectory(new File(mAppConfig.getPropertyValue("default_path")));
		File logFile = fileChooser.showOpenDialog(primaryStage);
		if(logFile != null) {
			logViewController.loadLogData(logFile);
		}
		
	}
}
