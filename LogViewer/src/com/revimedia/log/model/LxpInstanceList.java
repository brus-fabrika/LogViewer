package com.revimedia.log.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.revimedia.log.util.Configuration;

public class LxpInstanceList implements Serializable {
	private static final long serialVersionUID = 3865756189694456388L;

	private Map<String, Set<String>> mInstances = new HashMap<>();
	private File mRootDir;
	
	public LxpInstanceList() {
		if(mRootDir == null) {
			String rootFolder = Configuration.getInstance().getProperty("default_path");
			mRootDir = new File(rootFolder);
		}
	}
	
	public LxpInstanceList(File rootDir) {
		this();
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
						System.out.println("Instance found " + m.group(1));
						mInstances.put(m.group(1), new HashSet<String>());
					}
					mInstances.get(m.group(1)).add(file.getAbsolutePath());
				}
			}
		}
	}
	
	public boolean isEmpty() {
		return mInstances.isEmpty();
	}
	
	public Set<String> getInstances() {
		return mInstances.keySet();
	}
	
	public String getInstanceForFile(String fileName) {
		for(String instance: mInstances.keySet()) {
			if(mInstances.get(instance).contains(fileName)) {
				return instance;
			}
		}
		
		return null;
	}

	public Set<String> getInstanceFiles(String instanceName) {
		return mInstances.get(instanceName);
	}
	
	public String getMostRecentInstanceFile(String instanceName) throws FileNotFoundException {
		if(!mInstances.containsKey(instanceName) || mInstances.get(instanceName).isEmpty()) {
			throw new FileNotFoundException("Instance " + instanceName + " has no files");
		}
		
		String[] fileList = mInstances.get(instanceName).toArray(new String[0]);
		Arrays.sort(fileList);
		
		return fileList[fileList.length - 1];
	}
	
	public Set<String> getMostRecentFileList() throws FileNotFoundException {
		if(mInstances.isEmpty()) {
			throw new FileNotFoundException("Instance list is empty");
		}
		
		Set<String> resultList = new HashSet<>();
		
		for(String instanceName : mInstances.keySet()) {
			resultList.add(getMostRecentInstanceFile(instanceName));
		}
		
		return resultList;
	}
	
	public void refresh() {
		System.out.println("Instance list refresh...");
	}
	
}
