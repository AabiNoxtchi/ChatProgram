package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import Messages.User;

public class Server {
	
	private static ServerSocket server;
	private static final int PORT =9003;
	
	// to do populate all users from data base
	private static ArrayList<User> allUsers = new ArrayList<User>();
	private static HashSet<Integer> usersHashCodes = new HashSet<Integer>();
	private static ArrayList<User> onLineUsers = new ArrayList<User>();
	
	
	public static void main(String[] args) {
		try 
		{
			server=new ServerSocket(PORT);
			System.out.println("\n connected to port "+PORT);
			
			PopulateAllUsers();
			
			while(true) {
				Socket socket=server.accept();
				System.out.println("\nNew Client accepted.\n");
				ClientListner clientListner=new ClientListner(socket);
				clientListner.start();
			}
			
		}catch(IOException e)
		{
		    System.out.println("\nUnable to connect to port "+PORT);
		    System.exit(1);
	    }
		
	}


	//to do take the users from data base when server starts
	private static void PopulateAllUsers() {
		// TODO Auto-generated method stub
		//get all users from data base
		
		for (User user:allUsers) {
			Integer userhashcode=user.hashCode();
			usersHashCodes.add(userhashcode);
		}
		
	}
	
	public static boolean CheckIfExist(User user) {
		/*for(User u:allUsers) {
			System.out.println("user in allUsers : "+u.getUserName());
			System.out.println("user equals u : "+u.equals(user));
			System.out.println("user == u : "+(u==user));
		}
		for(Integer u:usersHashCodes) {
			System.out.println("user hashcode in usersHashCodes : "+u);
			System.out.println("user hashcode  : "+user.hashCode());
			
		}*/
		
		/*if(usersHashCodes.contains(user.hashCode())) {
			System.out.println("Server.CheckIfExist\ndoes exist");
			return true;
			}*/
		System.out.println(user.hashCode());
		return usersHashCodes.contains(user.hashCode());
	}


		public static void RegisterNewUser(User user) {
		// TODO Auto-generated method stub
		allUsers.add(user);
		
		Integer userhashcode=user.hashCode();
		usersHashCodes.add(userhashcode);
		System.out.println("adding user type"+user.getClass().getName()+"\nuser hashcode :"+user.hashCode());
		
	}
		
		public static void LogInOnlineUsers(User user){
			
			onLineUsers.add(user);
		}


	
	

}
