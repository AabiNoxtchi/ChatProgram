package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	
	private static ServerSocket socket;
	private static final int PORT =9003;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try 
		{
			socket=new ServerSocket(PORT);
			System.out.println("\n connected to port "+PORT);
			
		}catch(IOException e)
		{
		    System.out.println("\nUnable to connect to port "+PORT);
		    System.exit(1);
	    }
		
		do 
		{
			handle();
		}while(true);		

	}
	
	private static void handle()
	{
		
		Socket link=null;
		Scanner input=null;
		try
		{
			link=socket.accept();
			input=new Scanner(link.getInputStream());
			PrintWriter output=
					new PrintWriter(link.getOutputStream(),true);
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
				link.close();
			}catch(IOException e) {
				System.out.println("Unable to close secket connection");
				System.exit(1);
			}
		}
	}

}
