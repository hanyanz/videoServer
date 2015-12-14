package yanziTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class H264_Sender {
	public static File file = null;
	private static int info2=0, info3=0;	
	public static DatagramSocket client = null;	    
	public static InetAddress addr = null;
	public static int port = 0;
	public static int DEFAULT_PORT = 8889;
	public static FileInputStream in = null;
	public static String COMMAND = "AB0001";
	
	public static void main(String arg[]) throws IOException, InterruptedException{
		String fileName = "D:\\test\\test.h264";
        InitSocket("127.0.0.1");
        
        Send(COMMAND.getBytes(),COMMAND.length());
//      byte[] recvBuf = new byte[50];
//      DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
//      client.receive(recvPacket);
//    
//      	String recvStr = new String(recvPacket.getData(),0,recvPacket.getLength());
//      	System.out.println(recvStr);
//      	if(! ("OK".equals(recvStr))){        		
//      		return;
//      	}
        
        file = new File(fileName);
        
        in = new FileInputStream(file);
        
        int seq_num = 0;
        int	bytes=0;
        
        
        float framerate = 25;
        int timestamp_increse=0,ts_current=0;
    	timestamp_increse=(int)(90000.0 / framerate); 
       
        byte[] sendbuf = new byte[1500];
        
        while(!(0 == in.available())){
        	
        	NALU_t n = new NALU_t();
        	GetAnnexbNALU(n); 
        	dump(n);
        	memset(sendbuf,0,1500);
        	
        	sendbuf[1] = (byte)(sendbuf[1]|96); 
        	//System.out.println("-----!"+sendbuf[1]);
        	sendbuf[0] = (byte)(sendbuf[0]|0x80); 
        	sendbuf[1] = (byte)(sendbuf[1]&254); 
        	sendbuf[11] = 10;	
   
        	if(n.len <= 1400){
        		sendbuf[1] = (byte)(sendbuf[1]|0x80); 
        		
        		System.arraycopy(intToByte(seq_num++), 0, sendbuf, 2, 2);
        		{
        			
    				byte temp = 0;
    				temp = sendbuf[3];
    				sendbuf[3] = sendbuf[2];
    				sendbuf[2] = temp;
        		}
        		
        		
        		sendbuf[12] =  (byte)(sendbuf[12]|((byte)n.forbidden_bit)<<7);
        		sendbuf[12] =  (byte)(sendbuf[12]|((byte)(n.nal_reference_idc>>5))<<5);
        		sendbuf[12] =  (byte)(sendbuf[12]|((byte)n.nal_unit_type));
        		
        		System.arraycopy(n.buf, 1, sendbuf, 13, n.len-1);
        		
        		ts_current = ts_current+timestamp_increse;
    			//rtp_hdr.timestamp = ts_current;
        		System.arraycopy(intToByte(ts_current), 0, sendbuf, 4, 4);
        		{
        			
    				byte temp = 0;
    				temp = sendbuf[4];
    				sendbuf[4] = sendbuf[7];
    				sendbuf[7] = temp;
    				
    				temp = sendbuf[5];
    				sendbuf[5] = sendbuf[6];
    				sendbuf[6] = temp;
        		}
    			bytes = n.len + 12 ;	
    			Thread.sleep(30);
    			Send(sendbuf, bytes);
    		
        	}
        	else if(n.len > 1400){
        		
        		int k = 0, l = 0;
        		k = n.len/1400; 
        		l = n.len%1400; 
        		int t = 0; 
        		ts_current = ts_current + timestamp_increse;
        		//rtp_hdr->timestamp=htonl(ts_current);
        		System.arraycopy(intToByte(ts_current), 0, sendbuf, 4, 4);
        		{
       			
    				byte temp = 0;
    				temp = sendbuf[4];
    				sendbuf[4] = sendbuf[7];
    				sendbuf[7] = temp;
    				
    				temp = sendbuf[5];
    				sendbuf[5] = sendbuf[6];
    				sendbuf[6] = temp;
    				
        		}
        		while(t <= k){
        			
        			System.arraycopy(intToByte(seq_num++), 0, sendbuf, 2, 2);
        			{
        				
        				byte temp = 0;
        				temp = sendbuf[3];
        				sendbuf[3] = sendbuf[2];
        				sendbuf[2] = temp;
        			}
        			if(0 == t){
        				
        				sendbuf[1] = (byte)(sendbuf[1]&0x7F); // M=0
        				
                		sendbuf[12] =  (byte)(sendbuf[12]|((byte)n.forbidden_bit)<<7);
                		sendbuf[12] =  (byte)(sendbuf[12]|((byte)(n.nal_reference_idc>>5))<<5);
                		sendbuf[12] =  (byte)(sendbuf[12]|(byte)(28));
                		
                		
                		sendbuf[13] = (byte)(sendbuf[13]&0xBF);//E=0
                		sendbuf[13] = (byte)(sendbuf[13]&0xDF);//R=0
                		sendbuf[13] = (byte)(sendbuf[13]|0x80);//S=1
                		sendbuf[13] = (byte)(sendbuf[13]|((byte)n.nal_unit_type));
                		
                		
                		System.arraycopy(n.buf, 1, sendbuf, 14, 1400);
                		bytes = 1400 + 14;
                		Thread.sleep(30);
                		Send(sendbuf, bytes);
                		t++;
        			}
        			
        			else if(k == t){        			
        				
        				sendbuf[1] = (byte)(sendbuf[1]|0x80);
        				
        				sendbuf[12] =  (byte)(sendbuf[12]|((byte)n.forbidden_bit)<<7);
                		sendbuf[12] =  (byte)(sendbuf[12]|((byte)(n.nal_reference_idc>>5))<<5);
                		sendbuf[12] =  (byte)(sendbuf[12]|(byte)(28));
                		
                		
                		sendbuf[13] = (byte) (sendbuf[13]&0xDF); //R=0
                		sendbuf[13] = (byte) (sendbuf[13]&0x7F); //S=0
                		sendbuf[13] = (byte) (sendbuf[13]|0x40); //E=1
                		sendbuf[13] = (byte) (sendbuf[13]|((byte)n.nal_unit_type));
                		
                		
                		System.arraycopy(n.buf, t*1400+1, sendbuf, 14, l-1);
                		bytes = l-1+14;
                		Thread.sleep(30);
                		Send(sendbuf, bytes);
                		t++;
        			}
        			else if(t < k && 0 !=t){
        				
        				sendbuf[1] = (byte)(sendbuf[1]&0x7F); // M=0
        				
        				
        				sendbuf[12] =  (byte)(sendbuf[12]|((byte)n.forbidden_bit)<<7);
                		sendbuf[12] =  (byte)(sendbuf[12]|((byte)(n.nal_reference_idc>>5))<<5);
                		sendbuf[12] =  (byte)(sendbuf[12]|(byte)(28));
                		
                		
                 		sendbuf[13] = (byte) (sendbuf[13]&0xDF); //R=0
                		sendbuf[13] = (byte) (sendbuf[13]&0x7F); //S=0
                		sendbuf[13] = (byte) (sendbuf[13]&0xBF); //E=0
                		sendbuf[13] = (byte) (sendbuf[13]|((byte)n.nal_unit_type));
                		
                		System.arraycopy(n.buf, t*1400+1, sendbuf, 14, 1400);
    					bytes=1400+14;						
    					Thread.sleep(30);
    					Send(sendbuf, bytes);
    					t++;
        			}
        		}
        	}
        }
        //free nalu
        in.close();
	}
	
	public static void InitSocket(String address, Integer targetPort) throws UnknownHostException, SocketException{
		client = new DatagramSocket(); 
		addr = InetAddress.getByName(address);
		port = targetPort;
	}
	
	public static void InitSocket(String address) throws UnknownHostException, SocketException{
		client = new DatagramSocket(); 
		addr = InetAddress.getByName(address);
		port = DEFAULT_PORT;
	}
	
	public static void Send(byte[] sendStr, int len) throws IOException{		
		DatagramPacket sendPacket = new DatagramPacket(sendStr ,len , addr , port);
		
        try{
        	System.out.println("Send:"+len);
        	client.send(sendPacket);
        }catch (IOException e){
        	e.printStackTrace();
        	return;
       }
	}
	
	
	public static int FindStartCode2 (byte[] Buf, int off){
		if(Buf[0+off]!=0 || Buf[1+off]!=0 || Buf[2+off] !=1) 
			return 0;
		else 
			return 1;
	}

	
	public static int FindStartCode3 (byte[] Buf, int off){
		 if(Buf[0+off]!=0 || Buf[1+off]!=0 || Buf[2+off] !=0 || Buf[3+off] !=1) 
			 return 0;
		 else 
			 return 1;
	}
	
	public static void dump(NALU_t n){
		System.out.println("len: "+ n.len + " nal_unit_type:"+n.nal_unit_type);
	}
	
	
    public static byte[] intToByte(int number){ 
        int temp = number; 
        byte[] b = new byte[4]; 
        for (int i = 0; i < b.length; i++){ 
            b[i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8; 
        } 
        return b; 
    } 
    
	public static void memset(byte[] buf, int value, int size){
		for (int i = 0; i < size;i++){
			buf[i] = (byte) value;
		}
	}
	
	public static int GetAnnexbNALU(NALU_t nalu){
		nalu.startcodeprefix_len = 3;
		int pos = 0;
		int StartCodeFound, rewind;
		
	    byte[] tempbytes = new byte[8000000];
	    
        try{
            
            int byteread = 0;
           
            
            if ((byteread = in.read(tempbytes, 0, 3)) != 3){
            	
            	return 0;
            }
           
            
	    	info2 = FindStartCode2(tempbytes, 0);
	    	if(info2 != 1){
	    		
	    		if ((byteread = in.read(tempbytes, 3, 1)) != 1){
	    			return 0;
	    		}
	    		info3 = FindStartCode3(tempbytes, 0);
	    		if (info3 != 1){
	    			return -1;
	    		}else{
	    			 
	    			nalu.startcodeprefix_len = 4;
	    			pos = 4;
	    		}
	    	}else{
	    		
	    		nalu.startcodeprefix_len = 3;
	    		pos = 3;
	    	}
	    	
    	  
    	    StartCodeFound = 0;
    	    info2 = 0;
    	    info3 = 0;
    	    
  
    	    while (!(0 != StartCodeFound)){
    	    	
    	      if(0 == in.available()){      	      
    	        	nalu.len = (pos-1)-nalu.startcodeprefix_len;
    	        	System.arraycopy(tempbytes, nalu.startcodeprefix_len, nalu.buf, 0, nalu.len);
    	        	nalu.forbidden_bit = nalu.buf[0] & 0x80; //1 bit
    	        	nalu.nal_reference_idc = nalu.buf[0] & 0x60; // 2 bit
    	        	nalu.nal_unit_type = (nalu.buf[0]) & 0x1f;// 5 bit   	    	    
    	        	return pos-1;
    	     }
    	    	
    	    	
    	      if ((byteread = in.read(tempbytes, pos++, 1)) != 1){
    	    	  return 0;
    	      }
    	      info3 = FindStartCode3(tempbytes, pos-4);
    	      if(info3 != 1)
    	        info2 = FindStartCode2(tempbytes, pos-3);
    	     if(info2 == 1 || info3 == 1){
    	    	 StartCodeFound = 1;
    	     }else{
    	    	 StartCodeFound = 0;
    	     }
    	   }    	   
    	    
    	    rewind = (info3 == 1)? -4 : -3; 	    
    	    in.skip(rewind);

    	    nalu.len = (pos+rewind) - nalu.startcodeprefix_len;
    	 
    	    System.arraycopy(tempbytes, nalu.startcodeprefix_len, nalu.buf, 0, nalu.len);
    	    nalu.forbidden_bit = nalu.buf[0] & 0x80; //1 bit
    	    nalu.nal_reference_idc = nalu.buf[0] & 0x60; // 2 bit
    	    nalu.nal_unit_type = (nalu.buf[0]) & 0x1f;// 5 bit
    	   
    	   return (pos+rewind);
            	
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
		return 0;
	}


	//NALU�ṹ
	static class NALU_t{
		int startcodeprefix_len;      //! 4 for parameter sets and first slice in picture, 3 for everything else (suggested)
		int len;                 		//! Length of the NAL unit (Excluding the start code, which does not belong to the NALU)
		int max_size;           		//! Nal Unit Buffer size
		int forbidden_bit;            //! should be always FALSE
		int nal_reference_idc;        //! NALU_PRIORITY_xxxx
		int nal_unit_type;            //! NALU_TYPE_xxxx
		byte[] buf = new byte[8000000];                   //! contains the first byte followed by the EBSP
		int lost_packets;  			//! true, if packet loss is detected
	}

}

