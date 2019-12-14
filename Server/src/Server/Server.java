package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	
	private static ServerSocket server;
	private static final int PORT =9003;
	
	

	public static void main(String[] args) {
		try 
		{
			
			server=new ServerSocket(PORT);
			System.out.println("\n connected to port "+PORT);
			
			Resources resources=new Resources();			
			resources.populateAllUsers();
			
			while(true) {
				Socket socket=server.accept();//??
				System.out.println("\nNew Client accepted.\n");
				
				new ClientListner(socket,resources).start();
				
			}
			
		}catch(IOException e)
		{
		    System.out.println("\nUnable to connect to port "+PORT);
		    System.exit(1);
	    }
	}
	
	

	
	
}
