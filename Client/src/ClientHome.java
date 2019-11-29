
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Messages.Message;
import Messages.MessageType;
import Messages.User;

public class ClientHome {
	
	private static InetAddress host;
	private static int PORT=9003;
	
	public static void main(String[] args) {
		try {
			host=InetAddress.getLocalHost();
		}catch(UnknownHostException e) {
			System.out.println("Host ID not found !");
			System.exit(1);
		}
		
		accessServer();
	
}
	private static void accessServer() {
		
		Socket socket=null;
		
		//Scanner input=null;
		Scanner userIntry=null;
	    ObjectOutputStream output;
		
		try {
			socket=new Socket(host,PORT);
			
			
			//input=new Scanner(link.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			//PrintWriter output=new PrintWriter(link.getOutputStream(),true);
			userIntry=new Scanner(System.in);
			
			String userName,password;
			do {
				System.out.println("Register : \nUserName");
				userName=userIntry.nextLine();
				System.out.println("Password");
				password=userIntry.nextLine();
				
				User user=new User();
				user.setUserName(userName);
				user.setPassword(password);
				Message msg=new Message();
				msg.setType(MessageType.Register);		
				msg.setUser(user);
				
				
			
				output.writeObject(msg);
				
				
				
				
				
			}while(true);			
			
		}catch(IOException e) {
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
