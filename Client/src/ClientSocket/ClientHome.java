package ClientSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import Messages.Message;
import Messages.MessageType;
import Messages.User;
import view.ChatController;

public class ClientHome {
	
	

	private static InetAddress host;
	private static int PORT=9003;
	
	static ObjectOutputStream output;
    static ObjectInputStream input;
    public static ObjectInputStream getInput() {
		return input;
	}


	static Scanner scanner=new Scanner(System.in);
	static String userIntry;
	static Socket socket=null;
	
	private static String currentuser;
	public static String getCurrentuser() {
		return currentuser;
	}
	
	
	public ClientHome() {
		
	
	}
	public static boolean accessServer(Message msg) {
		
		try {
			if(host==null)
			host=InetAddress.getLocalHost();
			if(socket==null)
			socket=new Socket(host,PORT);
			if(output==null)
			output = new ObjectOutputStream(socket.getOutputStream());
			if(input==null)
			input=new ObjectInputStream(socket.getInputStream());
			
			
		}catch(IOException e) {
			System.out.println("Host ID not found !");
			System.exit(1);
		}
		
		
		 if(msg.getType()==MessageType.LogIn) {
			 currentuser=msg.getUser().getUserName();
		}
		
		return writeObj(msg);
	}
	
public static void sendMsgs(Message msg) {
		
		try {
			if(host==null)
			host=InetAddress.getLocalHost();
			if(socket==null)
			socket=new Socket(host,PORT);
			if(output==null)
			output = new ObjectOutputStream(socket.getOutputStream());
			if(input==null)
			input=new ObjectInputStream(socket.getInputStream());
			
			output.writeObject(msg);
			
		}catch(IOException e) {
			System.out.println("Host ID not found !");
			System.exit(1);
		}
	}


private static boolean writeObj(Message msg) {
	try {
		output.writeObject(msg);	
		boolean done;		
		done = (boolean)input.readObject();		
		if(done)System.out.println("successfull");
	    else System.out.println("not successfull");
		return done;
	} catch (IOException |ClassNotFoundException e) {		
		e.printStackTrace();
	}
	return false;
}


}
