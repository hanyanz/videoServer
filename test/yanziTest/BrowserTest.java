package yanziTest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BrowserTest {
	private static String COMMAND1 = "FE0001";
	private static String COMMAND2 = "EF";
	private static int DEFALUT_PORT = 1234;
	private static DatagramSocket client;
	private static InetAddress addr;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		 client = new DatagramSocket();
		 addr = InetAddress.getByName("127.0.0.1");
		 start();
		 Thread.sleep(30*1000);
		 stop();
	}
	
	private static void start() throws IOException{
		DatagramPacket sendPacket = new DatagramPacket(COMMAND1.getBytes() ,COMMAND1.length() , addr , DEFALUT_PORT);
		client.send(sendPacket);
	}
	private static void stop() throws IOException{
		DatagramPacket sendPacket = new DatagramPacket(COMMAND2.getBytes() ,COMMAND2.length() , addr , DEFALUT_PORT);
		client.send(sendPacket);
	}
	

}
