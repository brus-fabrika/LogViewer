package com.revimedia.log.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.revimedia.log.model.IFileTailerListener;
import com.revimedia.log.model.LxpInstanceList;

public class LogClientSocket implements Runnable {
	final private Logger log = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
//	final private int mPortNum = Configuration.getInstance().getPropertyAsInt("port", 4444);
	
	private Socket mClientSocket;
	private String mHost;
	private int mPortNum;
	
	private IFileTailerListener mLogListener;
	
	
	private boolean isConnected = false;

	//private BufferedReader mSocketReader;
	private ObjectInputStream mSocketReader;

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
//			mSocketReader = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
			mSocketReader = new ObjectInputStream(mClientSocket.getInputStream());
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
			
			//ObjectOutputStream out = new ObjectOutputStream(mClientSocket.getOutputStream());
			
//			ObjectInputStream in = new ObjectInputStream(mClientSocket.getInputStream());
			LxpInstanceList lxps = (LxpInstanceList) mSocketReader.readObject();
			
			String fileToListen = getLxpFile(lxps) + "\n";
			
			log.info("Client tries to connect to file " + fileToListen);
			
			mClientSocket.getOutputStream().write(fileToListen.getBytes());
			
//			String line = mSocketReader.readLine();
			String line = (String) mSocketReader.readObject();
			while(line != null) {
				System.out.println(line);
//				line = mSocketReader.readLine();
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
		
		return (String) lxps.getInstanceFiles("HART").toArray()[0];
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
