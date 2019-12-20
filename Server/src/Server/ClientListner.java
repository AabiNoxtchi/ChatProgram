package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
			                            
		                           case SearchUsers :
		                        	   
		                        	   searchUsers(msg);
		                        	   break;
			                            
		                           case FriendRequest:	
		                        	   sendFriendRequest(msg.getUser());
		                        	   addToFriendList(currentUser,msg.getUser());
		                        	   break;
		                        	   
		                           case ApprovedFriendRequest: 
		                        	   addToFriendList(currentUser,msg.getUser());
		                        	   break;
		                        	   
		                           case DeclinedFriendRequest: 
		                        	   addToDeclinedFriendList(currentUser,msg.getUser());
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
					
                	System.out.println("some exception in client Listner  "+currentUser.getUserName());
					
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
					}
				}
	}
	
	private void searchUsers(Message msg) {
		String usersSearchString = msg.getUsersSearchString();
		ArrayList<User> filteredUsers = resources.getUsers(e->e.getUserName().toLowerCase().startsWith(usersSearchString.toLowerCase()));
		msg.setSearchUsers(filteredUsers);
		notifyOnline(msg, currentUser.getUserName());
	}

	private void addToDeclinedFriendList(User user, User friend) {
		
	       if(!user.getUserName().contains(",")) {
				
				String recepients=friend.getUserName();
				if (recepients.contains(",")) {
					if(!recepients.contains(currentUser.getUserName()))recepients=currentUser.getUserName()+","+recepients;
					    String[] recepientsArr=recepients.split(",");
						List<String> names= (List<String>) Arrays.asList(recepientsArr);
						java.util.Collections.sort((java.util.List<String>) names );
						recepients=String.join(",", names);
					    friend.setUserName(recepients);
				}
	       }
					
			Map<String,HashSet<String>> declinedfriendsList = resources.getDeclinedfriendsLists();
		     
		     if(declinedfriendsList.containsKey(user.getUserName()))
				{
					if(! declinedfriendsList.get(user.getUserName()).contains(friend.getUserName()))
						resources.addToDeclinedFriendsLists(user.getUserName(),friend.getUserName());
				}
		     else {
					HashSet<String> declinedfriendList=new HashSet<String>();				
					declinedfriendList.add(friend.getUserName());
					resources.putInDeclinedFriendsLists(user.getUserName(),declinedfriendList);
				}
	}

	private void logOut() {
		 System.out.println("user "+currentUser.getUserName()+" logging out");
		 currentUser.setStatus(Status.OffLine);
		 notifyFriendsListUserStatusChanged();
		 clientSender.interrupt();
		 resources.removeFromOnlineUserMapping(currentUser.getUserName());
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
		
		if(friends != null)
		{
			HashMap<String,ClientSender> onlineUsers=resources.getOnlineUserMapping();
			
		for(String friend : friends){
			if (friend.contains(",")) {
					String[] recepients=friend.split(",");
					
					friend=currentUser.getUserName()+","+friend;
					
					String[] recepientsArr=friend.split(",");
					List<String> names= (List<String>) Arrays.asList(recepientsArr);
					java.util.Collections.sort((java.util.List<String>) names );
				    friend=String.join(",", names);
						
					for(String r:recepients)
					{	
						if(! r.equals(currentUser.getUserName())) {
							
							if(resources.getDeclinedfriendsLists().get(r)==null ||
									(!resources.getDeclinedfriendsLists().get(r).contains(friend)))
							{
								if(onlineUsers.containsKey(r)) {//needs change maybe its better to send them all in one time ? //
									User user=new User();
									user.setUserName(r);
									user.setStatus(Status.Online);
									Message msg=new Message();//////////////////new msg ////////////new user///status changed
									msg.setType(MessageType.StatusChanged);
									msg.setUser(user);
									notifyOnline(msg,currentUser.getUserName());
								}
							}
					  }
					}
			}else
			{
				if( resources.getDeclinedfriendsLists().get(friend) == null ||
						(!resources.getDeclinedfriendsLists().get(friend).contains(currentUser.getUserName())))
				{
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
	}
}

	private void forwardChatMsgs(Message msg) {
		
		String msgRecepient=msg.getGroupMembers();
		msg.setUser(currentUser);
		if (msgRecepient.contains(",")) {
			String[] recepients=msgRecepient.split(",");
			msgRecepient=currentUser.getUserName()+","+msgRecepient;
			
			String[] recepientsArr=msgRecepient.split(",");
			List<String> names= (List<String>) Arrays.asList(recepientsArr);
			java.util.Collections.sort((java.util.List<String>) names );
			String recepient=String.join(",", names);
			
			msg.setGroupMembers(msgRecepient);
			for(String r:recepients)
			{	
				if(! r.equals(currentUser.getUserName())) {
					if(resources.getDeclinedfriendsLists().get(r)==null ||
							(!resources.getDeclinedfriendsLists().get(r).contains(recepient)))
					{
				       notify(msg,r);
					}
			  }
			}
		}else {
			if( resources.getDeclinedfriendsLists().get(msgRecepient) == null ||
					(!resources.getDeclinedfriendsLists().get(msgRecepient).contains(currentUser.getUserName())))
			{
			msg.setGroupMembers(currentUser.getUserName());		
		    notify(msg,msgRecepient);
			}
		}
	}
	
	private void addToFriendList(User user,User friend) {
		
		if(!user.getUserName().contains(",")) {
			
			String recepients=friend.getUserName();
			if (recepients.contains(",")) {
				int index=recepients.indexOf(currentUser.getUserName());
				boolean exists=false;
				if(index>-1) {
					if(index==0 && recepients.indexOf(",")==currentUser.getUserName().length())exists=true;
					else if(index>0) {
						if(index+currentUser.getUserName().length()==recepients.length())exists=true;
						else if(recepients.charAt(index-1) == ',' && recepients.charAt(index+currentUser.getUserName().length()) == ',')exists=true;
					}
				}
				if(!exists)recepients=currentUser.getUserName()+","+recepients;
				    String[] recepientsArr=recepients.split(",");
					List<String> names= (List<String>) Arrays.asList(recepientsArr);
					java.util.Collections.sort((java.util.List<String>) names );
					recepients=String.join(",", names);
				    friend.setUserName(recepients);
			}
				
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
		}
		     String friendName=friend.getUserName();
		     if (friendName.contains(",")) {
					String[] recepientsArr=friendName.split(",");
					for(String r:recepientsArr)
					{	
						if(! r.equals(currentUser.getUserName())) {
							User newFriend=new User();							
							newFriend.setUserName(r);
							if(!isFriendDeclined(newFriend,friend) && checkFriendsLists(newFriend,friend)) { //if r has current user as friend and he's not blocked
							Message msg=checkFriendStatus(user);
							notifyOnline(msg,r);
							Message msg2=checkFriendStatus(newFriend);
							notifyOnline(msg2,user.getUserName());
							
							}
					  }
					}
		         }
		     else {
		    	 if(!isFriendDeclined(friend,user) && checkFriendsLists(friend,user)) {
				    Message msg=checkFriendStatus(user);
					notifyOnline(msg,friend.getUserName());	
					Message msg2=checkFriendStatus(friend);	
					notifyOnline(msg2,user.getUserName());	
		    	 }
		     }
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
			
			String recepients=user.getUserName();
			if (recepients.contains(",")) {
				String[] recepientsArr=recepients.split(",");
				
				recepients=currentUser.getUserName()+","+recepients;
				user.setUserName(recepients);
				msg.setUser(user);
				for(String r:recepientsArr)
				{	
					if(! r.equals(currentUser.getUserName())) {
					    notify(msg,r);
				  }
				}
			}else {
					msg.setUser(currentUser);
					notify(msg,user.getUserName());
				}
	}
	
	private boolean checkFriendsLists(User user,User friend) {
		if(friendslist==null)friendslist = resources.getFriendsLists();
			
			if(friendslist.containsKey(user.getUserName())) {
				if(friendslist.get(user.getUserName()).contains(friend.getUserName())) return true;
			}
		return false;
	}
	
	private boolean isFriendDeclined(User user,User friend) {
		HashMap<String,HashSet<String>> declinedfriendsLists = resources.getDeclinedfriendsLists();
			
			if(declinedfriendsLists.containsKey(user.getUserName())) {
				if(declinedfriendsLists.get(user.getUserName()).contains(friend.getUserName())) return true;
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
			if (friend.contains(",")) {
				String[] recepientsArr=friend.split(",");
				for(String r:recepientsArr) {
					if(!friends.contains(r)) {
						if(resources.getDeclinedfriendsLists().get(r) == null ||
								(!resources.getDeclinedfriendsLists().get(r).contains(currentUser.getUserName())))
						         notifyOnline(msg,r);
					}
				   }
				}else {
			         if(resources.getDeclinedfriendsLists().get(friend) == null ||
			        		 (resources.getDeclinedfriendsLists().get(friend) != null && !resources.getDeclinedfriendsLists().get(friend).contains(currentUser.getUserName()))) {
			        	      notifyOnline(msg,friend);}
				}
		}
	  }
	}
}
