package Views;

import java.util.Scanner;

import Messages.Message;
import Messages.MessageType;
import Messages.User;

public class LogIn {
	String userName,password;
	Scanner userIntry=new Scanner(System.in);
	public Message logIn() {
		
		System.out.println("Logging In");
		System.out.println("User Name : ");
		userName=userIntry.nextLine();
		System.out.println("Password : ");
		userName=userIntry.nextLine();
		
		User user=new User();
		user.setUserName(userName);
		user.setPassword(password);
		
		
		Message msg=new Message();
		msg.setUser(user);
		msg.setType(MessageType.LogIn);
		return msg;
	}

}
