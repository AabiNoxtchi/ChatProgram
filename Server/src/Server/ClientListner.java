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
    private ObjectOutputStream output; 
    private Resources resources;
    
    private User currentUser;
    
	
	public ClientListner(Socket socket,Resources resources) {
		this.socket=socket;
		this.resources=resources;
		
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
		                            	boolean registered= register(msg.getUser());		                            								
		                            	output.writeObject(registered);
	                                    break;
		                                
		                           case LogIn:  
		                        	   
	                                    boolean loggedIn= logIn(msg.getUser());		                                   										
										if(loggedIn) {
											output.writeObject(loggedIn);
											setloggedInUser(msg.getUser());										
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
			 resources.removeFromOnlineUserMapping(currentUser.getUserName());
			 System.out.println("notified Friends List User Status Changed ");
	}
	
	private void setloggedInUser(User user) {
		setcurrentUser(user,Status.Online);		
	    resources.putOnlineUserMapping(currentUser.getUserName(), output);
		//get online friends status msgs
		getOnlineFriendsStatus();
		notifyFriendsListUserStatusChanged();											
		getOfflineMsgs();
		
	}

	private void getOnlineFriendsStatus() {
		
		HashSet<String> friends=resources.getFriendsLists().get(currentUser.getUserName());
		
		System.out.println("checking friends");
		if(friends != null)
		{
			HashMap<String,ObjectOutputStream> onlineUsers=resources.getOnlineUserMapping();
			
		for(String friend : friends){
			System.out.println(friend);
			if(onlineUsers.containsKey(friend)) {//needs change better to send them all in one time ? //
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
		if(resources.getFriendsLists().containsKey(user.getUserName()))
		{
			if(! resources.getFriendsLists().get(user.getUserName()).contains(friend.getUserName()))
				resources.addToFriendsLists(user.getUserName(),friend.getUserName());		
		}else {
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
		if(resources.getOfflineMsgs().containsKey(userName)) {
			
			resources.addToOfflineMsgs(userName,msg);	
			
		}else {
			LinkedList<Message> msgs=new LinkedList<Message>();
			msgs.add(msg);
			resources.putToOfflineMsgs(userName,msgs);
		}
	}

	private boolean notifyOnline(Message msg, String userName) {
		try {
			if(userName==currentUser.getUserName()) {
				output.writeObject(msg);
			    return true;
			}
			else if(resources.getOnlineUserMapping().containsKey(userName))
			{										
				resources.getOnlineUserMapping().get(userName).writeObject(msg);//needs change //
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
		
		HashMap<String,HashSet<String>> friendslist=resources.getFriendsLists();
		
		if(friendslist.containsKey(currentUser.getUserName())) {
			if(friendslist.get(currentUser.getUserName()).contains(friend.getUserName()))
				return true;
		}
		return false;
	}

	private boolean logIn(User user) {
		
		if(resources.getUsersRegisterLoginHashCodes().containsValue(user.registerLoginhashCode()) && 
				! resources.getOnlineUserMapping().containsKey(user.getUserName()))
		{
			user.setStatus(Status.Online);						
			System.out.println("User "+user.getUserName()+" Logged in");
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
				try {
					
					output.writeObject(msg);
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			
			resources.removeFromOfflineMsgs(currentUser.getUserName());				
		}
	}

	private void notifyFriendsListUserStatusChanged() {
		
		HashMap<String,HashSet<String>> friendslist=resources.getFriendsLists();
		HashSet<String> friends=friendslist.get(currentUser.getUserName());
		if(friends!=null) {
			
		Message msg=new Message();
		msg.setType(MessageType.StatusChanged);
		msg.setUser(currentUser);		
		
		for(String friend:friends)
		{			
			notifyOnline(msg,friend);
		}
	  }
	}
}
