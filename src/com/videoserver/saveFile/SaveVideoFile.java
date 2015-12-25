package com.videoserver.saveFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.videoserver.util.DateUtil;
import com.videoserver.util.FileHelper;

public class SaveVideoFile {
	private static final Logger logger = LoggerFactory.getLogger(SaveVideoFile .class);
	private  File saveFile;
	private  DatagramSocket server = null;	    
	private  BufferedOutputStream buffOutStream;
	private  DatagramPacket packet;
	private  File pathFile;
	public void saveFile(File pathFile,int port) throws IOException{
		this.pathFile = pathFile;
		saveFile = FileHelper.getFileToSave(pathFile);
		try{
			InitSocket(port);
			byte[] buf = new byte[1500];
			
			while(true){
				packet = new DatagramPacket(buf, buf.length);
				server.receive(packet);
				if(packet.getLength() == 0){
					buffOutStream.close();
					server.close();
					break;
				}
				unpackAndSaveData(packet.getData(), packet.getLength());
				
			}
		
		}catch(Exception e){
			System.out.println("error when saving videoFile "+e.getMessage());
			logger.error( "error when saving videoFile to "+ saveFile.getAbsolutePath()+ " " + e.getCause().toString());
		}finally{
			if(buffOutStream != null){
				buffOutStream.close();
			}
			if(server != null){
				server.close();
			}
		}
		
	}
	
	private void unpackAndSaveData(byte[] buf, int length){
		byte[] recvbuf = new byte[1500];
		byte[] startcode = {0x00, 0x00, 0x00,0x01};
		
		memset(recvbuf, 0, 1500);
		System.arraycopy(buf,0, recvbuf,0, length);
		System.out.println("包长度： "+ length);
		
		try{
			if((recvbuf[12]&0x1F) == 28){
				if((recvbuf[1]&0x80) == 0x80){
					System.out.println("收到FUS最后一个包 "+(length-14)+"nalu_unit_type为 "+(byte)(recvbuf[13]&0x1F));
					buffOutStream.write(recvbuf, 14, length-14);
					buffOutStream.flush();
					if(saveFile.length() >= (10<<20)){
						saveFile = new File(pathFile,DateUtil.getNowTime()+".h264");
						saveFile.createNewFile();
					}
				}else if((recvbuf[13]&0x80) == 0x80){
					System.out.println("收到FUS第一个包 "+(length-14)+"nalu_unit_type为 "+(byte)(recvbuf[13]&0x1F));
					byte naluHdr = (byte) (recvbuf[12]&0xE0);
					naluHdr = (byte) (naluHdr | ((byte) (recvbuf[13]&0x1F)));	
					
					buffOutStream.write(startcode);
					buffOutStream.write((byte)naluHdr);
					buffOutStream.write(recvbuf, 14, length-14);
					buffOutStream.flush();
				}else{
					System.out.println("收到一个FUS包 "+(length-14)+"nalu_unit_type为 "+(byte)(recvbuf[13]&0x1F));
					buffOutStream.write(recvbuf, 14, length-14);
					buffOutStream.flush();
				}
				
			}else{
				System.out.println("收到单一单元模式数据 "+(length-12)+"nalu_unit_type为 "+(byte)(recvbuf[12]&0x1F));
				buffOutStream.write(startcode);
				buffOutStream.write(recvbuf, 12, length-12);
				buffOutStream.flush();
				if(saveFile.length() >= (10<<20)){
					saveFile = new File(pathFile,DateUtil.getNowTime()+".h264");
					saveFile.createNewFile();
				}
			}
		}catch(IOException e1){
			System.out.println(e1.getCause());
		}
		
		
	}

	//初始化socket
	private void InitSocket(Integer targetPort){
		try{
			server = new DatagramSocket(targetPort); 
			server.setSoTimeout(100*1000);
			buffOutStream = new BufferedOutputStream(new FileOutputStream(saveFile)); 
		}catch(Exception e){
			System.out.println(e.getMessage());
		}	
		 
	}
	
	
	// 重置buf的值
	private void memset(byte[] buf, int value, int size){
		for (int i = 0; i < size;i++){
			buf[i] = (byte) value;
		}
	}

}
