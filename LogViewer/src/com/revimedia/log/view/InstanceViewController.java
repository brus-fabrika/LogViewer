package com.revimedia.log.view;

import com.revimedia.log.model.LxpInstance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;

public class InstanceViewController {

	@FXML
	private ListView<LxpInstance> listView;
	
	ObservableList<LxpInstance> instanceList = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {

		instanceList.add(new LxpInstance("HART", false));
		instanceList.add(new LxpInstance("BASE", true));
		instanceList.add(new LxpInstance("BETA", true));
		instanceList.add(new LxpInstance("BETA64", false));
		instanceList.add(new LxpInstance("BMF", false));

		listView.setCellFactory(CheckBoxListCell.forListView(item -> item.instanceCheckedProperty()));
		
		instanceList.forEach(instance -> instance.instanceCheckedProperty().addListener(
			(observable, wasSelected, isSelected) -> {
				if (isSelected) {
					System.out.println("Instance " + instance + " is set to " + isSelected);
				} else {
					System.out.println("Instance " + instance + " is set to " + isSelected);
				}
		}));
		
		listView.getItems().addAll(instanceList);
	}

}
