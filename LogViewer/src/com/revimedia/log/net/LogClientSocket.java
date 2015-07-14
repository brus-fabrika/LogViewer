package com.revimedia.log.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.IFileTailerListener;

public class LogClientSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
//	final private int mPortNum = Configuration.getInstance().getPropertyAsInt("port", 4444);
	
	private Socket mClientSocket;
	private String mHost;
	private int mPortNum;
	
	private IFileTailerListener mLogListener;
	
	
	private boolean isConnected = false;

	private BufferedReader mSocketReader;

	public LogClientSocket(String host, int port, IFileTailerListener logTailer){
		mHost = host;
		mPortNum = port;
		mLogListener = logTailer;
	}
	
	public boolean tryConnect() {
		log.info("Start log server on port: " + mPortNum);
		isConnected = false;
		try {
			mClientSocket = new Socket(mHost, mPortNum);
			isConnected = true;
			log.info("connection to server with "+ mHost +":" + mPortNum + " OK");
			mSocketReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isConnected;
	}
	
	@Override
	public void run() {
		if(!isConnected) {
			log.warning("No connection to server, cannot start client");
			return;
		}

		try {
			String line = mSocketReader.readLine();
			while(line != null) {
				System.out.println(line);
				line = mSocketReader.readLine();
				mLogListener.onFileUpdate(line);
			}
		} catch(SocketException e) {
			log.warning("Server interrupted");
		} catch (IOException e) {
			log.severe(Arrays.toString(e.getStackTrace()));
		}
	}

	public void disconnect() {
		if(!isConnected) {
			return;
		}
		
		if(mClientSocket != null) {
			try {
				mClientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
