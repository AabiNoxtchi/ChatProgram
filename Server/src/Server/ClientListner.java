package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Messages.Message;
import Messages.MessageType;
import Messages.Status;
import Messages.User;

public class ClientListner extends Thread{
	
	private Socket socket;
	private ObjectInputStream input;   
    private Resources resources;
    private ClientSender clientSender;
    private HashMap<String,HashSet<String>> friendslist;
    
    private User currentUser;
    
	
	public ClientListner(Socket socket,Resources resources,ClientSender clientSender) {
		this.socket=socket;
		this.resources=resources;
		this.clientSender=clientSender;
		
		try {
		  input = new ObjectInputStream(socket.getInputStream());        
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
		                            	boolean registered= register(msg.getUser());		                            								
		                            	clientSender.sendboolean(registered);
	                                    break;
		                                
		                           case LogIn: 
		                        	   
	                                    boolean loggedIn= logIn(msg.getUser());	
	                                    clientSender.sendboolean(loggedIn);
										if(loggedIn) {											
											setloggedInUser(msg.getUser());										
										}
											
			                            break;
			                            
		                           case FriendRequest:			                        	 
		                        	   sendFriendRequest(msg.getUser());
		                        	   break;
		                        	   
		                           case ApprovedFriendRequest:                       	  
		                        	   addToFriendList(currentUser,msg.getUser());
		                        	   addToFriendList(msg.getUser(),currentUser);
		                        	   break;
		                        	   
		                           case ChatMessage:
		                           case FileTransfer:			                        	  
		                        	   forwardChatMsgs(msg);
		                        	   break;
		                        	   
		                           case LogOut:	
		                        	   
		                        	   logOut();
		                        	   
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
					
					e.printStackTrace();
					
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
						//?? System.exit(1);
					}
				}
	}
	
	private void logOut() {
		 currentUser.setStatus(Status.OffLine);
		 clientSender.interrupt();
		 resources.removeFromOnlineUserMapping(currentUser.getUserName());
	     notifyFriendsListUserStatusChanged();			 
	}

	private void setloggedInUser(User user) {
		user.setStatus(Status.Online);
		user.setPassword("");
		currentUser=user;
		clientSender.username=currentUser.getUserName();
		clientSender.start();
	    resources.putOnlineUserMapping(currentUser.getUserName(), clientSender);
		getOnlineFriendsStatus();
		notifyFriendsListUserStatusChanged();											
		getOfflineMsgs();
		
	}

	private void getOnlineFriendsStatus() {
		
		HashSet<String> friends=resources.getFriendsLists().get(currentUser.getUserName());
		
		System.out.println("checking friends");
		if(friends != null)
		{
			HashMap<String,ClientSender> onlineUsers=resources.getOnlineUserMapping();
			
		for(String friend : friends){
			System.out.println(friend);
			if(onlineUsers.containsKey(friend)) {//needs change maybe its better to send them all in one time ? //
				User user=new User();
				user.setUserName(friend);
				user.setStatus(Status.Online);
				Message msg=new Message();//////////////////new msg ////////////new user///status changed
				msg.setType(MessageType.StatusChanged);
				msg.setUser(user);
				notifyOnline(msg,currentUser.getUserName());
			}
		}
	}
}


	private void forwardChatMsgs(Message msg) {
		
		String msgRecepient=msg.getGroupMembers();
		msg.setUser(currentUser);
		if (msgRecepient.contains(",")) {
			String[] recepients=msgRecepient.split(",");
			
			msgRecepient=currentUser.getUserName()+","+msgRecepient;
			msg.setGroupMembers(msgRecepient);
			for(String r:recepients)
			{	
				System.out.println("r = "+r);
				
				if(! r.equals(currentUser.getUserName())) {
					
				notify(msg,r);
				
			  }
			}
		}else {
			msg.setGroupMembers(currentUser.getUserName());		
		    notify(msg,msgRecepient);
		}
	}

	
	private void addToFriendList(User user,User friend) {
		
		if(friendslist==null)friendslist = resources.getFriendsLists();
		
	     if(checkFriendsLists(user,friend))return;
	     else if(friendslist.containsKey(user.getUserName()))
			{
				if(! friendslist.get(user.getUserName()).contains(friend.getUserName()))
					resources.addToFriendsLists(user.getUserName(),friend.getUserName());		
			}
	     else {
				HashSet<String> friendList=new HashSet<String>();				
				friendList.add(friend.getUserName());
				resources.putInfriendsLists(user.getUserName(),friendList);				
			}
			    Message msg=checkFriendStatus(friend);	
				notifyOnline(msg,user.getUserName());						
	}

	private Message checkFriendStatus(User user) {
		
		if(user.getUserName()==currentUser.getUserName())
			user.setStatus(Status.Online);				
		else {
		if(resources.getOnlineUserMapping().containsKey(user.getUserName()))
		   user.setStatus(Status.Online);
		else
			user.setStatus(Status.OffLine);
		}
		Message msg=new Message();/////////////////////////////new msg//////////////////////////////
		msg.setType(MessageType.StatusChanged);
		msg.setUser(user);
		return msg;
		
	}

	private void notify(Message msg,String userName) {
		
			if(!notifyOnline(msg,userName))	notifyOffline(msg,userName);
		
	}

	private void notifyOffline(Message msg, String userName) {
		if(resources.getOfflineMsgs().containsKey(userName)) {
			
			resources.addToOfflineMsgs(userName,msg);	
			
		}else {
			LinkedList<Message> msgs=new LinkedList<Message>();
			msgs.add(msg);
			resources.putToOfflineMsgs(userName,msgs);
		}
	}

	private boolean notifyOnline(Message msg, String userName) {
		
			if(userName==currentUser.getUserName()) 
			{
				clientSender.addToMsgsList(msg);
			    return true;
			}
			else if(resources.getOnlineUserMapping().containsKey(userName))
			{	
				resources.getOnlineUserMapping().get(userName).addToMsgsList(msg);
				return true;
			}
		
		return false;
	}

	private void sendFriendRequest(User user) {
			if(checkFriendsLists(currentUser,user))return;
			Message msg=new Message();/////////////////////////new msg//////////////////////
			msg.setType(MessageType.FriendRequest);
			msg.setUser(currentUser);
			notify(msg,user.getUserName());
	}
	
	private boolean checkFriendsLists(User user,User friend) {
		if(friendslist==null)friendslist = resources.getFriendsLists();
			
			if(friendslist.containsKey(user.getUserName())) {
				if(friendslist.get(user.getUserName()).contains(friend.getUserName())) return true;
			}
		return false;
	}

	private boolean logIn(User user) {
		
		if(resources.getUsersRegisterLoginHashCodes().containsValue(user.registerLoginhashCode()) && 
				! resources.getOnlineUserMapping().containsKey(user.getUserName()))
		{
			
			return true;
		}	
		return false;
	}

	private boolean register(User user) {		
		if(user.registerLoginhashCode()==0 || 
				resources.getUsersRegisterLoginHashCodes().containsKey(user.hashCode())||
				resources.getUsersRegisterLoginHashCodes().containsValue(user.registerLoginhashCode())) 
		{			
			return false;			
		}else {
			resources.addinAllUsers(user);
			resources.putUsersRegisterLoginHashCodes(user.hashCode(),user.registerLoginhashCode());
						
			
			return true;
		}
	}
	
	private void getOfflineMsgs() {
		
		HashMap<String,LinkedList<Message>> offlineMsgs=resources.getOfflineMsgs();
		
		if(offlineMsgs.containsKey(currentUser.getUserName())) {
			LinkedList<Message> msgs=offlineMsgs.get(currentUser.getUserName());
			for(Message msg:msgs)
			{
					notifyOnline(msg,currentUser.getUserName());
			}
			
			resources.removeFromOfflineMsgs(currentUser.getUserName());				
		}
	}

	private void notifyFriendsListUserStatusChanged() {
		
		HashMap<String,HashSet<String>> friendslist=resources.getFriendsLists();
		HashSet<String> friends=friendslist.get(currentUser.getUserName());
		if(friends!=null) {
			
		Message msg=new Message();            //////////////new msg ///////////////////////
		msg.setType(MessageType.StatusChanged);
		msg.setUser(currentUser);		
		
		for(String friend:friends)
		{			
			notifyOnline(msg,friend);
		}
	  }
	}
}
