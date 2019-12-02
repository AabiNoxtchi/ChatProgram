package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

import Messages.Message;
import Messages.MessageType;
import Messages.OfflineMsgs;
import Messages.Status;
import Messages.User;

public class Server {
	
	private static ServerSocket server;
	private static final int PORT =9003;
	
	// to do populate all users from data base
	private static ArrayList<User> allUsers = new ArrayList<User>();
	private static HashMap<String,Integer> usersHashCodes = new HashMap<String,Integer>();
	private static HashMap<String,LinkedList<Message>> offlineMsgs = new HashMap<String,LinkedList<Message>>();
	
	//every online user has to have his friends list fetched from data base 
	//private static ArrayList<User> onLineUsers = new ArrayList<User>();
	private static HashMap<String,ObjectOutputStream> onlineUserMapping=new HashMap<String,ObjectOutputStream>();

	public static void main(String[] args) {
		try 
		{
			server=new ServerSocket(PORT);
			System.out.println("\n connected to port "+PORT);
			
			PopulateAllUsers();
			
			while(true) {
				Socket socket=server.accept();
				System.out.println("\nNew Client accepted.\n");
				new ClientListner(socket).start();
			}
			
		}catch(IOException e)
		{
		    System.out.println("\nUnable to connect to port "+PORT);
		    System.exit(1);
	    }
	}

	public static class ClientListner extends Thread{
		
		private Socket socket;
		private ObjectInputStream input;   
	    private ObjectOutputStream output; 
	    
	    User currentUser;
		
		public ClientListner(Socket socket) {
			this.socket=socket;
			try {
			input = new ObjectInputStream(socket.getInputStream());        
	        output = new ObjectOutputStream(socket.getOutputStream());
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void run() {		
			
	           System.out.println("Inside ServerClient.run");
	           Message msg=null;
	          
	           
	           
	             try {
					
				
	            
	                while (socket.isConnected()) {
	                	msg = (Message)input.readObject();
							
							if (msg != null) {
			                     
			                        switch (msg.getType()) {
			                           case Register:
			                            	System.out.println("registering new user");
			                            	boolean registered= Register(msg.getUser());		                            								
											output.writeObject(registered);			
		                                    break;
			                                
			                           case LogIn:                      	   
		                                    boolean loggedIn= LogIn(msg.getUser(),output);		                            									
											output.writeObject(loggedIn); 
											currentUser=msg.getUser();
											getOfflineMsgs(msg.getUser());
				                            break;
				                            
			                           case FriendRequest:
			                        	   boolean friendRequestSent=sendFriendRequest(msg.getUser());
			                        	   System.out.println("recieved friend request");
			                        	   //need to make this msg of type Message
			                        	   //output.writeObject(friendRequestSent); 
			                        	   break;
			                        	   
			                           case ApprovedFriendRequest:			                        	  
			                        	   boolean approvedFriendRequestSent=addToFriendList(currentUser,msg.getUser());
			                        	   //need to make this msg of type Message
			                        	   //output.writeObject(approvedFriendRequestSent); 
			                        	   break;
			                        	   
										default:
											break;
			                        }
			                    }
							
						} 
	                } catch (ClassNotFoundException | IOException e) {					
						e.printStackTrace();
						
					} 
	             
	             onlineUserMapping.remove(currentUser.getUserName());
		}

		
		private boolean addToFriendList(User currentUser,User user) {
			currentUser.getFriendsList().add(user);
			
			Message msg=CheckFriendStatus(user);
			
			try {
				output.writeObject(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			
		}

		private Message CheckFriendStatus(User user) {
			
			if(onlineUserMapping.containsKey(user))
			user.setStatus(Status.Online);
			Message msg=new Message();
			msg.setType(MessageType.StatusChanged);
			msg.setUser(user);
			return msg;
			
		}

		private boolean notify(Message msg,User user) {
			if(onlineUserMapping.containsKey(user.getUserName()))
			{
				try {
					onlineUserMapping.get(user.getUserName()).writeObject(msg);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				return true;
			}else {
				
				if(offlineMsgs.containsKey(user.getUserName())) {
					
					offlineMsgs.get(user.getUserName()).add(msg);
					return true;
				}else {
					LinkedList<Message> msgs=new LinkedList<Message>();
					msgs.add(msg);
					offlineMsgs.put(user.getUserName(),msgs);
				}
				return false;
			}
			
		}

		private boolean sendFriendRequest(User user) {
			Message msg=new Message();
			msg.setType(MessageType.FriendRequest);
			msg.setUser(currentUser);
			boolean sentFriendRequest=notify(msg,user);
			addToFriendList(currentUser, user);
			return sentFriendRequest;
		}
		
		private boolean LogIn(User user,ObjectOutputStream output) {		
			if(usersHashCodes.containsKey(user.getUserName()))
			{
				user.setStatus(Status.Online);
				onlineUserMapping.put(user.getUserName(), output);				
				notifyFriendsListUserCameOnline(user);				
				System.out.println("User "+user.getUserName()+" Logged in");
				return true;
			}	
			return false;
		}
		
		private boolean Register(User user) {		
			if(usersHashCodes.containsValue(user.hashCode())) {			
				return false;			
			}else {
				RegisterNewUser(user);
				return true;
			}
		}
		
		public void RegisterNewUser(User user) {		
			allUsers.add(user);		
			Integer userhashcode=user.hashCode();
			usersHashCodes.put(user.getUserName(),userhashcode);
			System.out.println("adding new user : "+user.getUserName()+"\nuser hashcode :"+user.hashCode());
		}
		
		private void getOfflineMsgs(User user) {
			
			if(offlineMsgs.containsKey(user.getUserName())) {
				LinkedList<Message> msgs=offlineMsgs.get(user.getUserName());
				for(Message msg:msgs)
				{
					try {
						//onlineUserMapping.get(user.getUserName()).writeObject(msg);
						output.writeObject(msg);
						offlineMsgs.remove(user.getUserName());
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
			}
		}

		private void notifyFriendsListUserCameOnline(User user) {
			Message msg=new Message();
			msg.setType(MessageType.StatusChanged);
			msg.setUser(user);
			for(User friend:user.getFriendsList())
			{
				if(onlineUserMapping.containsKey(friend.getUserName()))
				{
					try {
						System.out.println("Sending user's offline msgs !");
						onlineUserMapping.get(friend.getUserName()).writeObject(msg);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
			}
			
		}

	}
	
	
	private static void PopulateAllUsers() {	
		//to do take the users from data base when server starts	
		for (User user:allUsers) {
			Integer userhashcode=user.hashCode();
			usersHashCodes.put(user.getUserName(),userhashcode);
		}
	}
	
	

	
		
	
	
}
