package com.videoserver.server;

import java.io.File;
import java.sql.Timestamp;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.videoserver.saveFile.SaveVideoFile;
import com.videoserver.sendFile.SendVideoFile;
import com.videoserver.util.DateUtil;

public class VideoServerHandler extends IoHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(VideoServerHandler.class);
	
	@Override
	public void exceptionCaught(IoSession ssn, Throwable cause) {
			logger.error("", cause);
	        ssn.close(false);
	}

	@Override
	public void sessionOpened(IoSession ssn) throws Exception {
	    System.out.println("session open for " + ssn.getRemoteAddress());
	}
	
	@Override
	public void sessionClosed(IoSession ssn) throws Exception {
		Timestamp now = new Timestamp(System.currentTimeMillis());
	    System.out.println("session closed from " + ssn.getRemoteAddress()+" at "+now);
	}

	/**在接收到数据以后，处理数据，并返回相应的信息给客户端;
	 * 接收到客户端要发送视频数据的请求“AB”以及客户端对应的IMEI,如 AB_0001,则创建保存文件目录；
	 * 设备或者浏览器客户端请求发送视频数据结束“CD”，则关闭当前连接；
	 * 接收浏览器的视频请求; 发送相应目录下的最新视频文件到指定IP的1234端口；
	 * 接收到设备发送的视频数据，则保存到相应的文件中；
	 * 不符合上述条件则返回"error"
	 * */
	public void messageReceived(IoSession ssn, Object msg) throws Exception {    
		String message = msg.toString();
		System.out.println("get message: "+message);
		
		if(message.startsWith("AB")){ //AB0001表示0001号设备请求上发视频数据
			logger.info("received video command for sending videoInfo from device "+ssn.getRemoteAddress());
			message = message.substring(2, message.length());
			ssn.setAttribute("client", ssn.getRemoteAddress());
			File file = new File("D:/videoFiles/"+message+"/"+ DateUtil.getToday());
			if(!file.exists() && !file.isDirectory()){
				file.mkdir();
			}
			
			//TODO:这里增加一个获取空闲端口的函数，并把端口返回给设备，用于此次接收和保存文件
			int port = 8881;
			ssn.write(new String("OK_"+ port));
			new SaveVideoFile().saveFile(file, port);			
			
		}else if(message.startsWith("FE")){ //给浏览器传输视频文件FE0001
			logger.info("received vide command from browser "+ssn.getRemoteAddress());
			message = message.substring(2,message.length());	
			
			//TODO:浏览器请求的处理是逻辑待定 try catch ssn.write("error")
			
			SendVideoFile sendVideoFile = new SendVideoFile();
			ssn.setAttribute("sendVideoFile", sendVideoFile);
			try{
				sendVideoFile.sendVideoFile(ssn.getRemoteAddress().toString(),message.toString());
			}catch(Exception e){
				throw e;
			}finally{
				ssn.write("error");
			}			
			
		}else if("EF".equals(message)){
			//关闭传输视频文件给浏览器的连接
			if(ssn.getAttribute("sendVideoFile") != null){
				((SendVideoFile)ssn.getAttribute("sendVideoFile")).stopSendFile();
			}
			ssn.close(false);
					
		}else{
			System.out.println("receive data without hands");
			ssn.write("error");
			ssn.close(false);
		}
		
	}

}
