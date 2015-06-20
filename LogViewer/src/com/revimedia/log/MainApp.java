package com.revimedia.log;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.revimedia.log.view.LogViewController;
import com.revimedia.log.view.RootLayoutController;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private LogViewController logViewController;
	
	/**
	 * Constructor
	 */
	public MainApp() {
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");

		this.primaryStage.getIcons().add(new Image("file:resources/images/address-book.png"));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		this.primaryStage.setWidth(width/1.5);
		this.primaryStage.setHeight(height/1.5);
		
		initRootLayout();

		showLogView();
		
		this.primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				logViewController.stopProcessLogging();
			}
		});
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
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
			loader.setLocation(MainApp.class.getResource("view/LogView.fxml"));
			BorderPane logView = (BorderPane) loader.load();

			logViewController = loader.getController();
			
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
		File logFile = fileChooser.showOpenDialog(primaryStage);
		if(logFile != null) {
			logViewController.loadLogData(logFile);
		}
		
	}
}
