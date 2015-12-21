package com.revimedia.log.model;

import java.util.HashSet;
import java.util.Set;

public class InstanceFilter implements ILogFilter {

	Set<String> mInstanceList = new HashSet<>();
	
	@Override
	public boolean check(String log) {
		
		for(String instance: mInstanceList) {
			if(log.startsWith(instance)) {
				return false;
			}
		}
		
		return true;
	}
	
	public void addCondition(String cond) {
		mInstanceList.add(cond + ">");
	}
	
	public void removeCondition(String cond) {
		mInstanceList.remove(cond + ">");
	}

}
