package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.revimedia.log.model.FileTailerPool;
import com.revimedia.log.model.IFileTailerListener;

public class ClientLogPooler implements IFileTailerListener{

	final private Socket mClientSocket;
	private ObjectOutputStream mOutWriter;
	
	public ClientLogPooler(Socket clientSocket, File logFile) {
		mClientSocket = clientSocket;
		FileTailerPool.addNewListener(this);
		try {
			mOutWriter = new ObjectOutputStream(mClientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ClientLogPooler(Socket clientSocket) {
		mClientSocket = clientSocket;
		FileTailerPool.addNewListener(this);
		try {
			mOutWriter = new ObjectOutputStream(mClientSocket.getOutputStream());
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
	
	@Override
	public String toString() {
		return mClientSocket.getLocalAddress() + ":" + mClientSocket.getLocalPort();
	}

}
