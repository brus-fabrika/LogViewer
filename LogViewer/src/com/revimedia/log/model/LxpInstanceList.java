package com.revimedia.log.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LxpInstanceList implements Serializable {
	private static final long serialVersionUID = 3865756189694456388L;

	private Map<String, Set<String>> mInstances = new HashMap<>();
	
	public LxpInstanceList() {
		
	}
	
	public Set<String> getInstances() {
		return mInstances.keySet();
	}

	public Set<String> getInstanceFiles(String instanceName) {
		return mInstances.get(instanceName);
	}
	
	public void refresh() {
		
	}
	
}
