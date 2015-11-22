package com.revimedia.log.model;

import java.util.HashSet;
import java.util.Set;

public class LogFilters {
	Set<ILogFilter> m_filterList = new HashSet<>();
	
	public boolean check(String log) {
		for(ILogFilter filter: m_filterList) {
			if(!filter.check(log)) return false;
		}
		return true;
	}
	
	public void addFilter(ILogFilter filter) {
		m_filterList.add(filter);
	}
}
