package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Messages.*;


public class Server {
	
	private static ServerSocket server;
	private static final int PORT =9003;
	
	// to do populate all users from data base
	private static ArrayList<User> allUsers = new ArrayList<User>();
	
	//HashMap<user.getuserName.hashcode , user.registerhashcode>
	private static HashMap<Integer,Integer> usersRegisterLoginHashCodes = new HashMap<Integer,Integer>();
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
	            	 
	                while (!socket.isClosed()) {
	                	
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
											//get online friends status msgs
											getOnlineFriendsStatus();
											notifyFriendsListUserStatusChanged();											
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
			                           case FileTransfer:			                        	  
			                        	   forwardChatMsgs(msg);
			                        	   System.out.println("recieved  msg "+msg.getType());
			                        	   if(msg.getType()==MessageType.FileTransfer) {
			                        		   System.out.println("filetransfer.content.length = "+msg.getFileTransfer().getFileContent().length);
			                        	   }
			                        	   break;
			                        	   
			                           case LogOut:			                        	  
			                        	   
			                        	   if(input!=null)
			       							input.close();
			       							if(socket!=null)
			       								
			       							socket.close();
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
							if(input!=null)
							input.close();
							if(socket!=null)
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
				// System.exit(1);
		}
		
		private void getOnlineFriendsStatus() {
			HashSet<String> friends=friendsLists.get(currentUser.getUserName());
			System.out.println("checking friends");
			if(friends != null)
				
			for(String friend : friends){
				System.out.println(friend);
				if(onlineUserMapping.containsKey(friend)) {
					User user=new User();
					user.setUserName(friend);
					user.setStatus(Status.Online);
					Message msg=new Message();
					msg.setType(MessageType.StatusChanged);
					msg.setUser(user);
					notifyOnline(msg,currentUser.getUserName());
				}
			}
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
			
			String msgRecepient=msg.getGroupMembers();
			User user=new User();
			user.setUserName(currentUser.getUserName());
			msg.setUser(user);
			if (msgRecepient.contains(",")) {
				String[] recepients=msgRecepient.split(",");
				msgRecepient=currentUser.getUserName()+","+msgRecepient;
				
				for(String r:recepients)
				{	
					if(! r.equals(currentUser.getUserName())) {	
					String recepient=sortFriendName(msgRecepient,r);
					msg.setGroupMembers(recepient);
					notify(msg,r);	
				  }
				}
			}else {
				msg.setGroupMembers(currentUser.getUserName());		
			    notify(msg,msgRecepient);
			}
		}
		
		private String sortFriendName(String msgRecepient,String r) {
				
			    String[] recepients=msgRecepient.split(",");
				List<String> names= (List<String>) Arrays.asList(recepients);
				java.util.Collections.sort((java.util.List<String>) names );
				String recepient=String.join(",", names);
				String toReplace="";
				if(recepient.indexOf(r)==0)
			         toReplace=r+",";
				else 
					 toReplace=","+r;
					
				recepient=recepient.replace(toReplace,"");
				return recepient;
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
				notifyOnline(msg,user.getUserName());						
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
			if(usersRegisterLoginHashCodes.containsValue(user.registerLoginhashCode()) && !onlineUserMapping.containsKey(user.getUserName()))
			{
				user.setStatus(Status.Online);
				putOnlineUserMapping(user.getUserName(), output);				
				System.out.println("User "+user.getUserName()+" Logged in");
				return true;
			}	
			return false;
		}

		private boolean Register(User user) {		
			if(user.registerLoginhashCode()==0 || 
					usersRegisterLoginHashCodes.containsKey(user.hashCode())||
					usersRegisterLoginHashCodes.containsValue(user.registerLoginhashCode())) 
			{			
				return false;			
			}else {
				addAllUsers(user);
				putUsersRegisterLoginHashCodes(user.hashCode(),user.registerLoginhashCode());
							
				
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
				
				notifyOnline(msg,friend);
			}
		  }
		}
	}


	
	private static void PopulateAllUsers() {	
		//to do take the users from data base when server starts and put them in ArrayList<User> allUsers	
		for (User user:allUsers) {
			Integer userRegisterLoginhashcode=user.registerLoginhashCode();
			usersRegisterLoginHashCodes.put(user.hashCode(),userRegisterLoginhashcode);
			//usersLogInHashCodes.add(user.loginhashCode());
		}
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
	
	
	private synchronized static void putUsersRegisterLoginHashCodes(int usernamehashCode, int registerLoginhashCode) {
		usersRegisterLoginHashCodes.put(usernamehashCode,registerLoginhashCode);
		System.out.println("registered new user : " + usernamehashCode + " , " + usersRegisterLoginHashCodes.get(usernamehashCode));
	}
	
	private synchronized static void addAllUsers(User user){
		allUsers.add(user);	
		int index=allUsers.indexOf(user);
		System.out.print("added new user : "+allUsers.get(index).getUserName());
		
	}
	
}
