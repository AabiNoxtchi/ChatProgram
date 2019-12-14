package Server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import Messages.Message;
import Messages.User;

public class Resources {
	
	// to do populate all users from data base
		private ArrayList<User> allUsers = new ArrayList<User>();
		
		//HashMap<user.getuserName.hashcode , user.registerhashcode>
		private HashMap<Integer,Integer> usersRegisterLoginHashCodes = new HashMap<Integer,Integer>();
		private HashMap<String,LinkedList<Message>> offlineMsgs = new HashMap<String,LinkedList<Message>>();
		
		//every user has to have his friends list fetched from data base 	
		private HashMap<String,HashSet<String>> friendsLists = new HashMap<String,HashSet<String>>();
		
		//private ArrayList<User> onLineUsers = new ArrayList<User>();
		private HashMap<String,ObjectOutputStream> onlineUserMapping=new HashMap<String,ObjectOutputStream>();
		
		
		
		public HashMap<Integer,Integer> getUsersRegisterLoginHashCodes() {
			return usersRegisterLoginHashCodes;
		}
		
		//public HashMap<String,ObjectOutputStream> getOnlineUserMapping() {
		//	return onlineUserMapping;
		//}
		
		public HashMap<String,ObjectOutputStream> getOnlineUserMapping() {
			return onlineUserMapping;
		}
				
		public HashMap<String,LinkedList<Message>> getOfflineMsgs() {
			return offlineMsgs;
		}

		public HashMap<String,HashSet<String>> getFriendsLists() {
			return friendsLists;
		}

		public void populateAllUsers() {	
			//to do take the users from data base when server starts and put them in ArrayList<User> allUsers	
			for (User user:allUsers) {
				Integer userRegisterLoginhashcode=user.registerLoginhashCode();
				getUsersRegisterLoginHashCodes().put(user.hashCode(),userRegisterLoginhashcode);
				//usersLogInHashCodes.add(user.loginhashCode());
			}
		}
		
		public synchronized void removeFromOnlineUserMapping(String userName) {
			 getOnlineUserMapping().remove(userName);
			 notify();
					
		}

		public synchronized void putInfriendsLists(String userName, HashSet<String> friendList) {
			getFriendsLists().put(userName,friendList);
			 notify();
			
		}
		
		public synchronized void addToFriendsLists(String userName, String friendName) {
			getFriendsLists().get(userName).add(friendName);
			 notify();
			
		}
		
		public synchronized void removeFromOfflineMsgs(String userName) {
			getOfflineMsgs().remove(userName);
			 notify();
			
		}
		
		public synchronized void putToOfflineMsgs(String userName, LinkedList<Message> msgs) {
			getOfflineMsgs().put(userName,msgs);	
			 notify();
			
		}
		
		public synchronized void addToOfflineMsgs(String userName, Message msg) {
			getOfflineMsgs().get(userName).add(msg);	
			 notify();
			
		}
		
		public synchronized void putOnlineUserMapping(String userName, ObjectOutputStream output) {
			getOnlineUserMapping().put(userName,output);
			 notify();
			
		}
		
		
		public synchronized void putUsersRegisterLoginHashCodes(int usernamehashCode, int registerLoginhashCode) {
			getUsersRegisterLoginHashCodes().put(usernamehashCode,registerLoginhashCode);
			 notify();
		}
		
		public synchronized void addinAllUsers(User user){
			allUsers.add(user);	
			 notify();
			
		}
		
}
