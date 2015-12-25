package com.videoserver;

import com.videoserver.saveFile.SaveVideoFile;
import com.videoserver.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BrowserTest {
	private static String COMMAND1 = "FE0001";
	private static String COMMAND2 = "EF";
	private static int DEFALUT_PORT = 8888;
	private static DatagramSocket client;
	private static InetAddress addr;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		 client = new DatagramSocket();
		 addr = InetAddress.getByName("127.0.0.1");

		 start();

		File file = new File("D:/videoFiles/0001Browser/"+ DateUtil.getToday());
		if(!file.exists() || !file.isDirectory()){
			file.mkdir();
		}

		SaveVideoFile saveVideoFile = new SaveVideoFile();
		saveVideoFile.saveFile(file, 1234);

		String remoteAddress = client.getRemoteSocketAddress().toString();
		Integer port = Integer.parseInt( remoteAddress.substring(remoteAddress.indexOf(":")));

		 stop(port);
		client.close();
	}
	
	private static void start() throws IOException{
		DatagramPacket sendPacket = new DatagramPacket(COMMAND1.getBytes() ,COMMAND1.length() , addr , DEFALUT_PORT);
		client.send(sendPacket);
	}
	private static void stop(Integer port) throws IOException{
		COMMAND2 += port;
		DatagramPacket sendPacket = new DatagramPacket(COMMAND2.getBytes() ,COMMAND2.length() , addr , DEFALUT_PORT);
		client.send(sendPacket);
	}
	

}
