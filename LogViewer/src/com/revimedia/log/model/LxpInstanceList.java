package com.revimedia.log.model;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LxpInstanceList implements Serializable {
	private static final long serialVersionUID = 3865756189694456388L;

	private Map<String, Set<String>> mInstances = new HashMap<>();
	private File mRootDir;
	
	public LxpInstanceList() {
		
	}
	
	public LxpInstanceList(File rootDir) {
		mRootDir = rootDir;
		scan();
	}
	
	public File getRootDir() {
		return mRootDir;
	}

	public void setRootDir(File mRootDir) {
		this.mRootDir = mRootDir;
	}
	
	public void scan() {
		mInstances.clear();
		Pattern pattern = Pattern.compile("([A-Z]+)(\\d.+)([a,p]m[1,2])\\.log");
		for(File file: mRootDir.listFiles()) {
			if(!file.isDirectory()){
				Matcher m = pattern.matcher(file.getName());
				if(m.find()) {
					if(!mInstances.containsKey(m.group(1))) {
						mInstances.put(m.group(1), new HashSet<String>());
					}
					mInstances.get(m.group(1)).add(file.getAbsolutePath());
				}
			}
		}
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
