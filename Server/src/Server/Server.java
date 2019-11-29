package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import Messages.User;

public class Server {
	
	private static ServerSocket server;
	private static final int PORT =9003;
	
	// to do 
	private ArrayList<User> users;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try 
		{
			server=new ServerSocket(PORT);
			System.out.println("\n connected to port "+PORT);
			
			PopulateUsers();
			
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
	private static void PopulateUsers() {
		// TODO Auto-generated method stub
		
	}
	
	/*private static void handle()
	{
		Socket listner = null;
		
		Scanner input=null;
		try
		{
			
			listner=server.accept();
			
				input=new Scanner(listner.getInputStream());
			
			PrintWriter output=
					new PrintWriter(listner.getOutputStream(),true);
			int numMessages=0;
			String message=input.nextLine();
			
			
			while(!message.contentEquals("CLOSE"))
			{
				numMessages++;
				output.println(message+numMessages+" messages recieved");
				message=input.nextLine();
				
			}
			
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			
			input.close();
			try {
				listner.close();
			}catch(IOException e) {
				System.out.println("Unable to close secket connection");
				System.exit(1);
			}
		}
	}*/

}
