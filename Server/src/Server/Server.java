package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import Messages.*;


public class Server {
	
	private static ServerSocket server;
	private static final int PORT =9003;
	
	// to do populate all users from data base
	private static ArrayList<User> allUsers = new ArrayList<User>();
	
	//HashMap<user.getuserName.hashcode , user.registerhashcode>
	private static HashMap<Integer,Integer> usersRegisterHashCodes = new HashMap<Integer,Integer>();
	private static HashSet<Integer> usersLogInHashCodes = new HashSet<Integer>();
	private static HashMap<String,LinkedList<Message>> offlineMsgs = new HashMap<String,LinkedList<Message>>();
	
	//every user has to have his friends list fetched from data base 	
	private static HashMap<String,HashSet<String>> friendsLists = new HashMap<String,HashSet<String>>();
	
	//private static ArrayList<User> onLineUsers = new ArrayList<User>();
	private static HashMap<String,ObjectOutputStream> onlineUserMapping=new HashMap<String,ObjectOutputStream>();

	public static void main(String[] args) {
		try 
		{
			server=new ServerSocket(PORT);
			System.out.println("\n connected to port "+PORT);
			
			PopulateAllUsers();
			
			while(true) {
				Socket socket=server.accept();//??
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
	           
	             try {
	            	 
	            	 Message msg=null;
	            	 
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
		                                    boolean loggedIn= LogIn(msg.getUser());		                            									
											output.writeObject(loggedIn);
											if(loggedIn) {
											setcurrentUser(msg.getUser(),Status.Online);											
											notifyFriendsListUserStatusChanged();
											//send all friends status msgs
											getOfflineMsgs();
											}
												
				                            break;
				                            
			                           case FriendRequest:			                        	 
			                        	   sendFriendRequest(msg.getUser());
			                        	   System.out.println("recieved friend request");			                        	  
			                        	   break;
			                        	   
			                           case ApprovedFriendRequest:                       	  
			                        	   addToFriendList(currentUser,msg.getUser());
			                        	   addToFriendList(msg.getUser(),currentUser);
			                        	   break;
			                        	   
			                           case ChatMessage:			                        	  
			                        	   forwardChatMsgs(msg);			                        	  
			                        	   break;
			                        	   
										default:
											break;
			                        }
			                    }
							
						} 
	                } catch (ClassNotFoundException | IOException e) {					
						
						e.printStackTrace();//??
						
					} finally {
						   
						   	           
						try {
							System.out.println("closing connections ");
							input.close();
							socket.close();
							System.out.println("closed connections ");
						}catch(IOException e) {
							System.out.println("couldnt close connections ");
							e.printStackTrace();
							System.exit(1);
						}
					}
	             
	          // currentUser.setStatus(Status.OffLine);
	             User user=setEmptyUser();
				 setcurrentUser(user,Status.OffLine);							
			     notifyFriendsListUserStatusChanged();	
				 removeFromOnlineUserMapping(currentUser.getUserName());
				 System.out.println("notified Friends List User Status Changed ");
		}
		
		private User setEmptyUser() {
			 
			return null;
		}

		private void setcurrentUser( User user ,Status status ) {
			if(status==Status.Online) {
				user.setPassword("");
				currentUser=user;
			}else {	
				 user=new User();
				 user.setUserName(currentUser.getUserName());
				 user.setStatus(status);				
				currentUser=user;
			}
			
		}

		private void forwardChatMsgs(Message msg) {
			
			User user=msg.getUser();
			msg.setUser(currentUser);			
			notify(msg,user.getUserName());			
		}
		
		private void addToFriendList(User user,User friend) {
			if(checkFriendsLists(friend))return;
			if(friendsLists.containsKey(user.getUserName()))
			{
				if(!friendsLists.get(user.getUserName()).contains(friend.getUserName()))
					addToFriendsLists(user.getUserName(),friend.getUserName());		
			}else {
				HashSet<String> friendList=new HashSet<String>();				
				friendList.add(friend.getUserName());
				putInfriendsLists(user.getUserName(),friendList);				
			}
			        Message msg=CheckFriendStatus(friend);	
			        
					if(onlineUserMapping.containsKey(user.getUserName())) {						
						notifyOnline(msg,user.getUserName());						
					}
		}

		private Message CheckFriendStatus(User user) {
			
			if(user.getUserName()==currentUser.getUserName())
				user.setStatus(Status.Online);				
			else {
			if(onlineUserMapping.containsKey(user.getUserName()))
			   user.setStatus(Status.Online);
			else
				user.setStatus(Status.OffLine);
			}
			Message msg=new Message();
			msg.setType(MessageType.StatusChanged);
			msg.setUser(user);
			return msg;
			
		}

		private void notify(Message msg,String userName) {
			
				if(!notifyOnline(msg,userName))			
				 notifyOffline(msg,userName);
			
		}

		private void notifyOffline(Message msg, String userName) {
			if(offlineMsgs.containsKey(userName)) {
				if(msg.getType()==MessageType.StatusChanged)
				{
					int index= offlineMsgs.get(userName).indexOf(msg);
					System.out.println("index = "+index);
					if(index!=-1) {
						
						////
						replaceOfflineMsgsLinkedListItem(msg,userName,index);
						
						
					}
					
				}else
				addToOfflineMsgs(userName,msg);	
			}else {
				LinkedList<Message> msgs=new LinkedList<Message>();
				msgs.add(msg);
				putToOfflineMsgs(userName,msgs);
			}
		}

		private boolean notifyOnline(Message msg, String userName) {
			try {
				if(userName==currentUser.getUserName()) {
					output.writeObject(msg);
				    return true;
				}
				else if(onlineUserMapping.containsKey(userName))
				{										
					onlineUserMapping.get(userName).writeObject(msg);
					return true;
				}
			} catch (IOException e) {				
				e.printStackTrace();
			}
			
			return false;
		}

		private void sendFriendRequest(User user) {
			if(checkFriendsLists(user))return;
			Message msg=new Message();
			msg.setType(MessageType.FriendRequest);
			msg.setUser(currentUser);
			notify(msg,user.getUserName());
		}
		
		private boolean checkFriendsLists(User friend) {
			if(friendsLists.containsKey(currentUser.getUserName())) {
				if(friendsLists.get(currentUser.getUserName()).contains(friend.getUserName()))
					return true;
			}
			return false;
		}

		private boolean LogIn(User user) {		
			if(usersLogInHashCodes.contains(user.loginhashCode()) && !onlineUserMapping.containsKey(user.getUserName()))
			{
				user.setStatus(Status.Online);
				putOnlineUserMapping(user.getUserName(), output);				
				System.out.println("User "+user.getUserName()+" Logged in");
				return true;
			}	
			return false;
		}

		private boolean Register(User user) {		
			if(usersRegisterHashCodes.containsKey(user.hashCode())||usersRegisterHashCodes.containsValue(user.registerhashCode())) {			
				return false;			
			}else {
				addAllUsers(user);
				//Integer userRegisterHashcode=user.registerhashCode();
				putUsersRegisterHashCodes(user.hashCode(),user.registerhashCode());
				addUsersLogInHashCodes(user.loginhashCode());				
				
				return true;
			}
		}
		
		private void getOfflineMsgs() {
			
			if(offlineMsgs.containsKey(currentUser.getUserName())) {
				LinkedList<Message> msgs=offlineMsgs.get(currentUser.getUserName());
				for(Message msg:msgs)
				{
					try {
						
						output.writeObject(msg);
						
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
				removeFromOfflineMsgs(currentUser.getUserName());				
			}
		}

		private void notifyFriendsListUserStatusChanged() {
			if(friendsLists.containsKey(currentUser.getUserName())) {
				
			Message msg=new Message();
			msg.setType(MessageType.StatusChanged);
			msg.setUser(currentUser);
			
			for(String friend:friendsLists.get(currentUser.getUserName()))
			{
				
				notify(msg,friend);
				/*if(onlineUserMapping.containsKey(friend))
				{
					try {
						System.out.println("Sending user's friends his status changed  = "+msg.getUser().getStatus());
						onlineUserMapping.get(friend).writeObject(msg);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}*/
			}
		  }
			
		}
		
	}


	
	private static void PopulateAllUsers() {	
		//to do take the users from data base when server starts and put them in ArrayList<User> allUsers	
		for (User user:allUsers) {
			Integer userRegisterhashcode=user.registerhashCode();
			usersRegisterHashCodes.put(user.hashCode(),userRegisterhashcode);
			usersLogInHashCodes.add(user.loginhashCode());
		}
	}
	
	private synchronized static void replaceOfflineMsgsLinkedListItem(Message msg, String userName, int index) {
		offlineMsgs.get(userName).remove(index);
		offlineMsgs.get(userName).add(msg);
		System.out.println("replacing offline msgs linked list for user "+userName+" ,msg user name "+msg.getUser().getUserName());
		
	}

	private synchronized static void removeFromOnlineUserMapping(String userName) {
		 onlineUserMapping.remove(userName);
				
	}

	private synchronized static void putInfriendsLists(String userName, HashSet<String> friendList) {
		friendsLists.put(userName,friendList);
		
	}
	
	private synchronized static void addToFriendsLists(String userName, String friendName) {
		friendsLists.get(userName).add(friendName);
		
	}
	
	private synchronized static void removeFromOfflineMsgs(String userName) {
		offlineMsgs.remove(userName);
		
	}
	
	private synchronized static void putToOfflineMsgs(String userName, LinkedList<Message> msgs) {
		offlineMsgs.put(userName,msgs);	
		
	}
	
	private synchronized static void addToOfflineMsgs(String userName, Message msg) {
		offlineMsgs.get(userName).add(msg);	
		
	}
	
	private synchronized static void putOnlineUserMapping(String userName, ObjectOutputStream output) {
		onlineUserMapping.put(userName, output);
		
	}
	
	private synchronized static void addUsersLogInHashCodes(int loginhashCode) {
		usersLogInHashCodes.add(loginhashCode);
		System.out.println("added new user log in data : " + loginhashCode + " : " + usersLogInHashCodes.contains(loginhashCode));
		
	}
	
	private synchronized static void putUsersRegisterHashCodes(int usernamehashCode, int registerhashCode) {
		usersRegisterHashCodes.put(usernamehashCode,registerhashCode);
		System.out.println("registered new user : " + usernamehashCode + " , " + usersRegisterHashCodes.get(usernamehashCode));
	}
	
	private synchronized static void addAllUsers(User user){
		allUsers.add(user);	
		int index=allUsers.indexOf(user);
		System.out.print("added new user : "+allUsers.get(index).getUserName());
		
	}
	
}
