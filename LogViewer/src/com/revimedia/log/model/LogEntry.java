package com.revimedia.log.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
	private final StringProperty mPayload = new SimpleStringProperty();
	private final StringProperty mTimeStamp = new SimpleStringProperty();

	public LogEntry() {
		this(null);
	}

	public LogEntry(String payload) {
		String[] ss = payload.split(">", 2);
		if(ss.length == 2) {
			this.mTimeStamp.set(ss[0]);
			this.mPayload.set(ss[1]);
		} else {
			this.mPayload.set(ss[0]);
		}
	}

	public String getPayload() {
		return mPayload.get();
	}

	public void setPayload(String payload) {
		this.mPayload.set(payload);
	}

	public StringProperty payloadProperty() {
		return mPayload;
	}
	
	public String getTimeStamp() {
		return mTimeStamp.get();
	}

	public void setTimeStamp(String payload) {
		this.mTimeStamp.set(payload);
	}

	public StringProperty timestampProperty() {
		return mTimeStamp;
	}
}
