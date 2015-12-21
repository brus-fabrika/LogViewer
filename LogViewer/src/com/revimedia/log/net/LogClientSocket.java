package com.revimedia.log.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.IFileTailerListener;

public class LogClientSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private Socket mClientSocket;
	private String mHost;
	private int mPortNum;
	
	private IFileTailerListener mLogListener;
	
	private boolean isConnected = false;

	private ObjectInputStream mSocketReader;

	public LogClientSocket(String host, int port, IFileTailerListener logTailer){
		mHost = host;
		mPortNum = port;
		mLogListener = logTailer;
	}
	
	public boolean tryConnect() {
		log.info("Connecting to log server " + mHost + ":" + mPortNum);
		isConnected = false;
		try {
			mClientSocket = new Socket(mHost, mPortNum);
			isConnected = true;
			log.info("connection to server with "+ mHost +":" + mPortNum + " OK");
			mSocketReader = new ObjectInputStream(mClientSocket.getInputStream());
		} catch (ConnectException e) {
			log.severe("Server " + mHost + ":" + mPortNum + " refused connection");
		} catch (UnknownHostException e) {
			log.severe("Log server host is unknown: " + mHost);
		} catch (IOException e) {
			log.severe("Log server socket creation error");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			log.severe("Log server port number is incorrect: " + mPortNum);
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
			String line = (String) mSocketReader.readObject();
			while(line != null) {
				line = (String) mSocketReader.readObject();
				mLogListener.onFileUpdate(line);
			}
		} catch(SocketException e) {
			log.warning("Server interrupted");
		} catch (IOException e) {
			log.log(Level.SEVERE, "Socket read error", e);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "Socket read error", e);
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
				log.log(Level.SEVERE, "Socket close error", e);
			}
		}
	}
}
