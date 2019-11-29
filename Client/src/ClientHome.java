
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Messages.Message;
import Messages.MessageType;
import Messages.User;
import Views.LogIn;
import Views.Register;
import Views.UserHome;

public class ClientHome {
	
	private static InetAddress host;
	private static int PORT=9003;
	
	public static void main(String[] args) {
		
		LogIn logIn=new LogIn();
		Message msg=logIn.logIn();
		try {
			host=InetAddress.getLocalHost();
		}catch(UnknownHostException e) {
			System.out.println("Host ID not found !");
			System.exit(1);
		}
		
		//User user=Login();
		accessServer(msg);
	
}
	private static void accessServer(Message msg) {
		
		Socket socket=null;
		
		//Scanner input=null;
		
	    ObjectOutputStream output;
	    ObjectInputStream input;
		
		try {
			socket=new Socket(host,PORT);
			
			
			//input=new Scanner(link.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			input=new ObjectInputStream(socket.getInputStream());
			//PrintWriter output=new PrintWriter(link.getOutputStream(),true);
			
			do {
				
				//Message msg=Login();
			
				output.writeObject(msg);
				boolean done=(boolean)input.readObject();
				if(done)System.out.println("Registered successfully");
				else System.out.println("not Registered");
				UserHome userHome=new UserHome();
				
				msg=userHome.newMsg();
				//to do
				//Message msg=userHome.newMsg();
				
				
			}while(true);			
			
		}catch(IOException |ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			//input.close();
			try {
				socket.close();
			}catch(IOException e)
			{
				System.out.println("\nUnable to close conection ");
				System.exit(1);;
			}
		}
		
	}

}
