package com.revimedia.log.test;

import java.io.File;

import com.revimedia.log.model.LxpInstanceList;

public class LxpInstanceListTest {

	public static void main(String[] args) {
		LxpInstanceList lxp = new LxpInstanceList(new File("D:\\ttt"));
		System.out.println(lxp.getInstances());
		
		System.out.println(lxp.getInstanceFiles((String)lxp.getInstances().toArray()[1]));
	}

}
