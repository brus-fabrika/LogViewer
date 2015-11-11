package com.revimedia.log.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;

import com.revimedia.log.model.LxpInstance;

public class InstanceViewController {

	@FXML
	private ListView<LxpInstance> listView;
	
	ObservableList<LxpInstance> instanceList = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {
		listView.setCellFactory(CheckBoxListCell.forListView(item -> item.instanceCheckedProperty()));
		addInstance(new LxpInstance("BETA64", false));
	}

	public void addInstance(LxpInstance instance) {
		instance.instanceCheckedProperty().addListener(
				(observable, wasSelected, isSelected) -> {
					if (isSelected) {
						System.out.println("Instance " + instance + " is set to " + isSelected);
					} else {
						System.out.println("Instance " + instance + " is set to " + isSelected);
					}
			}
		);
		
		instanceList.add(instance);
		
		listView.getItems().add(instanceList.get(instanceList.size()-1));
	}
	
}
