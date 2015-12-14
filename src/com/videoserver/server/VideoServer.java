package com.videoserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class VideoServer {
	private static final int PORT1 = 8888;
	private static final int PORT2 = 8889;
	public static void main(String[] args) throws IOException{
		IoAcceptor acceptor = new NioDatagramAcceptor();
		
		LoggingFilter logger = new LoggingFilter();
        logger.setSessionOpenedLogLevel(LogLevel.INFO); 
        
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,30); //30s       
       
	    acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new FlashCrossdomainCodec()));
	   // acceptor.getFilterChain().addLast("logger",logger);
	    
	    acceptor.setHandler(new VideoServerHandler());
	    
	    acceptor.bind(new InetSocketAddress(PORT1));
	    acceptor.bind(new InetSocketAddress(PORT2));
	    System.out.println("HelloServer started on port " + PORT1);
	    System.out.println("HelloServer started on port " + PORT2);
	}
}
