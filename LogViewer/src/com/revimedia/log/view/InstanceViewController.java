package com.revimedia.log.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
	
	private List<IInstanceVisibilityHandler> instanceHandlers = new ArrayList<>();
	
	@FXML
	private void initialize() {
		listView.setCellFactory(CheckBoxListCell.forListView(item -> item.instanceCheckedProperty()));
	}

	public void addInstance(LxpInstance instance) {
		instance.instanceCheckedProperty().addListener(
				(observable, wasSelected, isSelected) -> {
					if (isSelected) {
						System.out.println("Instance " + instance + " is set to " + isSelected);
					} else {
						System.out.println("Instance " + instance + " is set to " + isSelected);
					}
					for(IInstanceVisibilityHandler h: instanceHandlers) {
						h.instanceChecked(instance.getInstanceName(), isSelected);
					}
			}
		);
		
		instanceList.add(instance);
		
		listView.getItems().add(instanceList.get(instanceList.size()-1));
	}
	
	public void addInstance(String instanceName) {
		List<LxpInstance> filteredList = instanceList.filtered(new Predicate<LxpInstance>() {

			@Override
			public boolean test(LxpInstance arg0) {
				return arg0.getInstanceName().equals(instanceName);
			}
			
		});
		
		if(filteredList.size() == 0) {
			this.addInstance(new LxpInstance(instanceName, true));
		}
	}
	
	public void addInstanceHandler(IInstanceVisibilityHandler instanceHandler) {
		instanceHandlers.add(instanceHandler);
	}
	
}
