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
		Message msg=null;
			try {
				
			     while(msgs.size() < 1) wait(); 
			      msg=msgs.poll();
			     
					
				} catch (InterruptedException e) {					
					System.out.println("interrupted exception in getMsg clientSender");
				}
			
			return msg;			
		}
	
	private void sendObject(Object obj) {
		try {
			output.writeObject(obj);
			output.reset();			
			output.flush();
			
		} catch (IOException | NullPointerException e) {			
			System.out.println("io exception send obj clientSender ");
		}
	}	
	
	
	public void sendboolean(boolean bool) {
		sendObject(bool);
	} 	
	
	public synchronized void addToMsgsList(Message msg) {
		msgs.add(msg);	
		notify();
	}
	
	public void run () {
		
		while(!isInterrupted()) {
		
		Message msg=getMsg();
		sendObject(msg);
		
		}
	}
}
