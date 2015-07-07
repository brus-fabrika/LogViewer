package com.revimedia.log.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSocketTest {

	public static void main(String[] args) {
		// TODO: create client socket, connect to server
		// TODO: get some info from server and exit 

		try(Socket MyClient = new Socket("localhost", 4444)){
			System.out.println("Connected to localhost:4444");
			BufferedReader bis = new BufferedReader(new InputStreamReader(MyClient.getInputStream()));
			String line = bis.readLine();
			while(line != null) {
				System.out.println(line);
				line = bis.readLine();
			}
			System.out.println("Connection to localhost:4444 was closed");
		} catch (IOException e) {
			System.out.println(e);
		}	
	}

}
