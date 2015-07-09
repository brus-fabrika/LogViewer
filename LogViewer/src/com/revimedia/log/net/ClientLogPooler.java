package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.revimedia.log.model.FileTailer;
import com.revimedia.log.model.FileTailerPool;
import com.revimedia.log.model.IFileTailerListener;

public class ClientLogPooler implements IFileTailerListener{

	final private Socket mClientSocket;
	final private FileTailer mLogFileTailer;
	private PrintWriter mOutWriter;
	
	public ClientLogPooler(Socket clientSocket, File logFile) {
		mClientSocket = clientSocket;
		mLogFileTailer = FileTailerPool.getTailerForFile(logFile);
		
		try {
			mOutWriter = new PrintWriter(mClientSocket.getOutputStream(), true);
			mLogFileTailer.addLogFileTailerListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		mOutWriter.flush();
		try {
			mClientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onFileUpdate(String line) {
		mOutWriter.println(line);
	}

}
