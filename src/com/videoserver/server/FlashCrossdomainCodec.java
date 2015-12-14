package com.videoserver.server;

import java.sql.Timestamp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.SynchronizedProtocolDecoder;
import org.apache.mina.filter.codec.SynchronizedProtocolEncoder;

public class FlashCrossdomainCodec implements ProtocolCodecFactory {
	static int count3 = 0;

	class FlashCrossdomainDecoder extends ProtocolDecoderAdapter {
		
		public void decode(IoSession session, IoBuffer in,
				ProtocolDecoderOutput out) throws Exception {
			
			FlashCrossdomainCodec.count3++;
			Timestamp now2 = new Timestamp(System.currentTimeMillis());
			System.out.println("--------------收到数据--------"
					+ FlashCrossdomainCodec.count3 + "---------"
					+ now2.toString() + "-------------------");
			try {
				if (in.hasRemaining()) {
					byte[] data = new byte[in.remaining()];
					in.get(data); // data已经获取接收数组的数据内容

					int[] data1 = new int[data.length];
					String data_string = "";
					for (int i = 0; i < data.length; i++) {
						String data_hex;
						if (data[i] < 0) {
							data1[i] = data[i] + 256;
							data_hex = Integer.toHexString(data1[i]);
							while (data_hex.length() < 2) {
								data_hex = "0" + data_hex;
							}
							data_string += data_hex;
						} else {
							data1[i] = data[i];
							data_hex = Integer.toHexString(data1[i]);
							while (data_hex.length() < 2) {
								data_hex = "0" + data_hex;
							}
							data_string += data_hex;
						}
					}
					String msg = analyticASCII(data_string);
					out.write(msg);
				}				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		 public String analyticASCII(String data){
			String hexStr = "";
			int length = (int)(data.length()/2);
			//当IMEI按ASCII值传输时
			int a = 0;int b = 2;
			for(int i=0;i<length;i++){
				Integer y = Integer.parseInt(data.substring(a, b),16);
				a += 2;b += 2;
				hexStr += (char)y.intValue();
			}
			return hexStr;
				
		 }
	}

	class FlashCrossdomainEncoder extends ProtocolEncoderAdapter {
		public void encode(IoSession session, Object message,
				ProtocolEncoderOutput out) throws Exception {
			try {
				out.write(IoBuffer.wrap(message.toString().getBytes("UTF-8")));
				System.out.println("writting "+message.toString());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private ProtocolEncoder encoder = new SynchronizedProtocolEncoder(
			new FlashCrossdomainEncoder());
	private ProtocolDecoder decoder = new SynchronizedProtocolDecoder(
			new FlashCrossdomainDecoder());

	public FlashCrossdomainCodec() {
	}

	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
		return encoder;
	}

	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
		return decoder;
	}

	public ProtocolDecoder getDecoder() throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder() throws Exception {
		return encoder;
	}

}
