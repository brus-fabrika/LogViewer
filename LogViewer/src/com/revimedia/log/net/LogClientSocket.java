package com.revimedia.log.net;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.IFileTailerListener;
import com.revimedia.log.model.LxpInstanceList;
import com.revimedia.log.util.Configuration;

public class LogClientSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
//	final private int mPortNum = Configuration.getInstance().getPropertyAsInt("port", 4444);
	
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
//			LxpInstanceList lxps = (LxpInstanceList) mSocketReader.readObject();
//			
//			if(lxps.isEmpty()) {
//				log.severe("No LXP log files found on server");
//				return;
//			}
//			
//			String fileToListen = getLxpFile(lxps);
//			
//			if(fileToListen.isEmpty()) {
//				log.severe("No LXP instance <...> log files found on server");
//				return;
//			}
//			
//			log.info("Client tries to connect to file " + fileToListen);
//			
//			fileToListen += "\n";
//			mClientSocket.getOutputStream().write(fileToListen.getBytes());
			String line = (String) mSocketReader.readObject();
			while(line != null) {
				line = (String) mSocketReader.readObject();
				mLogListener.onFileUpdate(line);
			}
		} catch(SocketException e) {
			log.warning("Server interrupted");
		} catch (IOException e) {
			log.severe(Arrays.toString(e.getStackTrace()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getLxpFile(LxpInstanceList lxps) {
		
		String lxpName = Configuration.getInstance().getProperty("default_instance");
		
		if(lxps.getInstanceFiles(lxpName) == null) {
			log.severe("No logs found for lxp instance " + lxpName);
			return "";
		}
		
		String[] instanceFileList = (String[]) lxps.getInstanceFiles(lxpName).toArray(new String[0]);
		int mostRecentValue = -1;
		int mostRecentIndex = 0;
		for(int fileIndex = 0; fileIndex < instanceFileList.length; ++fileIndex) {
			File tmpFileObject = new File(instanceFileList[fileIndex]);
			String tmp = tmpFileObject.getName().replace(lxpName, "").replace(".log", "").replace("am", "0").replace("pm", "1");
			int fileNameValue = Integer.valueOf(tmp);
			if(fileNameValue > mostRecentValue) {
				mostRecentIndex = fileIndex;
				mostRecentValue = fileNameValue;
			}
		}
		
		log.info("Most recent file for default instance is " + instanceFileList[mostRecentIndex]);
		
		return instanceFileList[mostRecentIndex];
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
