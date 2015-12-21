package com.revimedia.log.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
	private final IntegerProperty mLineNumber = new SimpleIntegerProperty();
	private final StringProperty mPayload = new SimpleStringProperty();
	private final StringProperty mTimeStamp = new SimpleStringProperty();
	private final StringProperty mInstance = new SimpleStringProperty();
	
	public LogEntry() {
		this(null, 0);
	}

	public LogEntry(String payload, int lineNumber) {
		parseLog(payload, lineNumber);
	}

	private void parseLog(String payload, int lineNumber) {
		this.mLineNumber.set(lineNumber);
		
		String[] ss = payload.split(">", 3);
		if(ss.length == 3) {
			this.mInstance.set(ss[0]);
			this.mTimeStamp.set(ss[1]);
			this.mPayload.set(ss[2]);
		} else if(ss.length == 2) {
			this.mInstance.set(ss[0]);
			this.mPayload.set(ss[1]);
		} else {
			this.mInstance.set("?");
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

	public IntegerProperty lineNumberProperty() {
		return mLineNumber;
	}
	
	public Integer getLineNumber() {
		return mLineNumber.getValue();
	}
	
	public void setLineNumber(Integer number) {
		this.mLineNumber.setValue(number);
	}

	public StringProperty instanceProperty() {
		return mInstance;
	}
	
	public String getInstance() {
		return mInstance.getValue();
	}
	
	public void setLineNumber(String instance) {
		this.mInstance.setValue(instance);
	}
}
