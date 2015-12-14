//package com.videoserver.server;
//
//import org.apache.mina.core.filterchain.IoFilter;
//import org.apache.mina.core.filterchain.IoFilterChain;
//import org.apache.mina.core.session.IdleStatus;
//import org.apache.mina.core.session.IoSession;
//import org.apache.mina.core.write.WriteRequest;
//
//public class MyIoFilter implements IoFilter{
//	@Override
//	public void destroy() throws Exception{
//		System.out.println("dstroy...");
//	}
//	@Override
//	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception{
//		nextFilter.exceptionCaught(session, cause);
//		System.out.println("exceptionCaught...");
//	}
//	@Override
//	public void filterClose(NextFilter nextFilter, IoSession session) throws Exception{
//		nextFilter.filterClose(session);
//		System.out.println("filterClose...");
//	}
//	@Override
//	public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception{
//		nextFilter.filterWrite(session, writeRequest);
//		System.out.println("filterWrite...");
//	}
//	@Override    
//	public void init() throws Exception {    
//		System.out.println("init...");    
//	}    
//	@Override    
//	public void messageReceived(NextFilter nextFilter, IoSession session,Object message) throws Exception {    
//		System.out.println("messageReceived...");    
//		nextFilter.messageReceived(session, message);    
//	 }    
//	@Override    
//	public void messageSent(NextFilter nextFilter, IoSession session,WriteRequest writeRequest) throws Exception {    
//		System.out.println("messageSent...");    
//		nextFilter.messageSent(session, writeRequest);    
//	}    
//	@Override    
//	public void onPostAdd(IoFilterChain parent, String name, NextFilter nextFilter) throws Exception {    
//		System.out.println("onPostAdd...");    
//	}    
//	@Override    
//	public void onPostRemove(IoFilterChain parent, String name,NextFilter nextFilter) throws Exception {    
//		System.out.println("onPostRemove...");    
//	}    
//	@Override    
//	public void onPreAdd(IoFilterChain parent, String name,NextFilter nextFilter) throws Exception {    
//		System.out.println("onPreAdd...");    
//	}    
//	@Override    
//	public void onPreRemove(IoFilterChain parent, String name,NextFilter nextFilter) throws Exception {    
//		System.out.println("onPreRemove...");    
//	}    
//	@Override    
//	public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {    
//		System.out.println("sessionClosed...");    
//		nextFilter.sessionClosed(session);    
//	}    
//	@Override    
//	public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {    
//		 System.out.println("sessionCreated...");    
//		 nextFilter.sessionCreated(session);    
//	}    
//	@Override    
//	public void sessionIdle(NextFilter nextFilter, IoSession session,IdleStatus status) throws Exception {    
//		 System.out.println("sessionIdle...");    
//		 nextFilter.sessionIdle(session, status);    
//	}    
//	 @Override    
//	 public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {    
//		 System.out.println("sessionOpened...");    
//		 nextFilter.sessionOpened(session);    
//	 }    
//
//}
