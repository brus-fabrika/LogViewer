package com.revimedia.log.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LxpInstance {
	private final StringProperty mInstanceName = new SimpleStringProperty();
	private final BooleanProperty mInstanceChecked = new SimpleBooleanProperty();

	public LxpInstance(String name, boolean on) {
		setInstanceName(name);
		setInstanceChecked(on);
	}

	public final StringProperty instanceNameProperty() {
		return this.mInstanceName;
	}

	public final String getInstanceName() {
		return this.instanceNameProperty().get();
	}

	public final void setInstanceName(final String name) {
		this.instanceNameProperty().set(name);
	}

	public final BooleanProperty instanceCheckedProperty() {
		return this.mInstanceChecked;
	}

	public final boolean isInstanceChecked() {
		return this.instanceCheckedProperty().get();
	}

	public final void setInstanceChecked(final boolean on) {
		this.instanceCheckedProperty().set(on);
	}

	@Override
	public String toString() {
		return getInstanceName();
	}

}
