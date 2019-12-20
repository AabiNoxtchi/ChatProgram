package Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import Messages.Message;
import Messages.User;

public class Resources {
	
	// to do populate all users from data base
		private ArrayList<User> allUsers = new ArrayList<User>();
		
		public ArrayList<User> getUsers(Predicate<User> userPredicate) {
			//return allUsers.;
			return (ArrayList<User>) allUsers.stream().filter(userPredicate) .collect(Collectors.toList());
		}

		private HashMap<Integer,Integer> usersRegisterLoginHashCodes = new HashMap<Integer,Integer>();
		private HashMap<String,LinkedList<Message>> offlineMsgs = new HashMap<String,LinkedList<Message>>();
		
		//every user has to have his friends list fetched from data base 	
		private HashMap<String,HashSet<String>> friendsLists = new HashMap<String,HashSet<String>>();
		
		private HashMap<String,HashSet<String>> declinedfriendsLists = new HashMap<String,HashSet<String>>();
		
		private HashMap<String,ClientSender> onlineUserMapping=new HashMap<String,ClientSender>();
		
		

		
		
		public synchronized void putInDeclinedFriendsLists(String userName, HashSet<String> friendList) {
			getDeclinedfriendsLists().put(userName,friendList);
			 notify();
			
		}
		
		public synchronized void addToDeclinedFriendsLists(String userName, String friendName) {
			getDeclinedfriendsLists().get(userName).add(friendName);
			 notify();
			
		}
		
		public HashMap<String,HashSet<String>> getDeclinedfriendsLists() {
			return declinedfriendsLists;
		}
		
		
		public synchronized void putInfriendsLists(String userName, HashSet<String> friendList) {
			getFriendsLists().put(userName,friendList);
			 notify();
			
		}
		
		public synchronized void addToFriendsLists(String userName, String friendName) {
			getFriendsLists().get(userName).add(friendName);
			 notify();
			
		}
		
		public HashMap<String,HashSet<String>> getFriendsLists() {
			return friendsLists;
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
		
		public HashMap<String,LinkedList<Message>> getOfflineMsgs() {
			return offlineMsgs;
		}

		public synchronized void removeFromOnlineUserMapping(String userName) {
			 getOnlineUserMapping().remove(userName);
			 notify();
		}

		public synchronized void putOnlineUserMapping(String userName, ClientSender sender) {
			getOnlineUserMapping().put(userName,sender);
			 notify();
			
		}
		
		public HashMap<String,ClientSender> getOnlineUserMapping() {
			return onlineUserMapping;
		}
		
		public synchronized void putUsersRegisterLoginHashCodes(int usernamehashCode, int registerLoginhashCode) {
			getUsersRegisterLoginHashCodes().put(usernamehashCode,registerLoginhashCode);
			 notify();
		}
		
		public HashMap<Integer,Integer> getUsersRegisterLoginHashCodes() {
			return usersRegisterLoginHashCodes;
		}
		
		public void populateAllUsers() {	
			//to do take the users from data base when server starts and put them in ArrayList<User> allUsers	
			for (User user:allUsers) {
				Integer userRegisterLoginhashcode=user.registerLoginhashCode();
				getUsersRegisterLoginHashCodes().put(user.hashCode(),userRegisterLoginhashcode);
			}
		}
		
		public synchronized void addinAllUsers(User user){
			allUsers.add(user);	
			 notify();
			
		}

		
}
