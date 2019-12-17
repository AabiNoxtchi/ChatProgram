package Server;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import Messages.Message;

public class ClientSender extends Thread{
	
	private ObjectOutputStream output; 
	
	private LinkedList<Message> msgs=new LinkedList<Message>();
	
	public String username;
	
	
	public ClientSender(Socket socket) {
			
			try {
			     
	          output = new ObjectOutputStream(socket.getOutputStream());
	          
			}catch(IOException e) {
				e.printStackTrace();
			}
		}

	private synchronized Message getMsg() {
			try {
				
			     while(msgs.size()<1)wait();			     		     	
			     
					
				} catch (InterruptedException e) {					
					System.out.println("interrupted exception in getMsg clientSender");
				}
			
			Message msg=msgs.poll();
			//System.out.println("thread "+username+"polled new msg "+msg.getType());
			return msg;			
		}
	
	private void sendObject(Object obj) {
		try {
			
			output.writeObject(obj);
			
			
		} catch (IOException e) {			
			System.out.println("io exception clientSender ");
		}
		
	}	
	
	
	public void sendboolean(boolean bool) {
		sendObject(bool);
	} 	
	
	public synchronized void addToMsgsList(Message msg) {
		msgs.add(msg);	
		System.out.println("thread "+username+"recieved new msg "+msg.getType());
		notify();
	}
	
	public void run () {
		
		while(!isInterrupted()) {
		
		Message msg=getMsg();
		System.out.println("thread "+username+"sending new msg "+msg.getType());
		sendObject(msg);
		
		}
		
	}
	
}
