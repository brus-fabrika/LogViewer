package com.revimedia.log.view;

import java.io.IOException;
import java.util.Optional;

import com.revimedia.log.net.RemoteConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class ConnectionsListViewController {

	private Stage mConnectionEditStage = new Stage();

	private ObservableList<RemoteConnection> remoteConnectionList = FXCollections
			.observableArrayList();

	@FXML
	private ListView<RemoteConnection> remoteConnectionListView;

	@FXML
	private Button mOkButton;

	public ConnectionsListViewController() {
		remoteConnectionList.add(new RemoteConnection("hello", "192.168.0.1", 8888));
		remoteConnectionList.add(new RemoteConnection("andrey", "192.168.0.2", 4444));
	}

	@FXML
	private void initialize() {
		remoteConnectionListView.setCellFactory(new Callback<ListView<RemoteConnection>, ListCell<RemoteConnection>>() {
			@Override
			public ListCell<RemoteConnection> call(ListView<RemoteConnection> p) {
				ListCell<RemoteConnection> cell = new ListCell<RemoteConnection>() {
					@Override
					protected void updateItem(RemoteConnection t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getConnectionName());
						}
					}
				};
				return cell;
			}
		});
		
		remoteConnectionListView.setItems(remoteConnectionList);
	}
	
	@FXML
	private void handleAddButtonClick(ActionEvent event) {
		mConnectionEditStage.initStyle(StageStyle.UTILITY);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass()
					.getResource("ConnectionEditView.fxml"));
			BorderPane connectionEditView = (BorderPane) loader.load();

			Scene scene = new Scene(connectionEditView);
			mConnectionEditStage.setScene(scene);
			mConnectionEditStage.setResizable(false);
			mConnectionEditStage.setTitle("Add new connection");
			mConnectionEditStage.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void handleEditButtonClick(ActionEvent event) {

	}

	@FXML
	private void handleDeleteButtonClick(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("Are you ok with this?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// ... user chose OK
		} else {
			// ... user chose CANCEL or closed the dialog
		}
	}

	@FXML
	private void handleOKButtonClick(ActionEvent event) {
		Stage thisStage = (Stage) mOkButton.getScene().getWindow();
		thisStage.close();
	}
}
