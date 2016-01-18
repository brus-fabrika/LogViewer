package com.revimedia.log.net;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RemoteConnection {
	final private StringProperty connectionName;
	final private StringProperty address;
	final private IntegerProperty port;
	
	public RemoteConnection() {
		this(null, null, 0);
	}
	
	/**
	 * @param connectionName
	 * @param address
	 * @param port
	 */
	public RemoteConnection(String connectionName, String address, int port) {
		this.connectionName = new SimpleStringProperty(connectionName);
		this.address = new SimpleStringProperty(address);
		this.port = new SimpleIntegerProperty(port);
	}

	public StringProperty connectionNameProperty() {
		return connectionName;
	}
	
	public String getConnectionName() {
		return connectionName.get();
	}

	public void setConnectionName(String connectionName) {
		this.connectionName.set(connectionName);
	}

	public StringProperty address() {
		return address;
	}
	
	public String getAddress() {
		return address.get();
	}

	public void setAddress(String address) {
		this.address.set(address);
	}

	public IntegerProperty port() {
		return port;
	}
	
	public int getPort() {
		return port.get();
	}

	public void setPort(int port) {
		this.port.set(port);
	}
	
	
}
