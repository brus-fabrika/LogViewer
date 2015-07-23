package com.revimedia.log.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.FileTailerPool;
import com.revimedia.log.model.IFileTailerListener;
import com.revimedia.log.model.LxpInstanceList;

public class ClientLogPooler implements IFileTailerListener{

	final private Socket mClientSocket;
	private FileTailer mLogFileTailer;
	private ObjectOutputStream mOutWriter;
	
	private LxpInstanceList mInstanceList = new LxpInstanceList(new File("D:\\ttt"));
	
	public ClientLogPooler(Socket clientSocket, File logFile) {
		mClientSocket = clientSocket;
		mLogFileTailer = FileTailerPool.getTailerForFile(logFile);
		
		try {
			mOutWriter = new ObjectOutputStream(mClientSocket.getOutputStream());
			mLogFileTailer.addLogFileTailerListener(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ClientLogPooler(Socket clientSocket) {
		mClientSocket = clientSocket;
		mInstanceList.scan();
		
		try {
			mOutWriter = new ObjectOutputStream(mClientSocket.getOutputStream());
			mOutWriter.writeObject(mInstanceList);
			BufferedReader in = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
			String fileToListen = in. readLine();
			
			System.out.println("Filename received from client: " + fileToListen);
			
			if(!(fileToListen == null || fileToListen.isEmpty())) {
				mLogFileTailer = FileTailerPool.getTailerForFile(new File(fileToListen));
				mLogFileTailer.addLogFileTailerListener(this);
			} else {
				// TODO: add exception here as we don't want to have this object anymore
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			mOutWriter.flush();
			mClientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onFileUpdate(String line) {
		try {
			mOutWriter.writeObject(line + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
