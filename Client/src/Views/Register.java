package Views;

import java.util.Scanner;

import Messages.Message;
import Messages.MessageType;
import Messages.User;

public class Register {
	Scanner userIntry=null;
	String firstName,lastName,email,userName,password;
	public Message register() {
		
		userIntry=new Scanner(System.in);
		
		System.out.println("Register : \n First NameName");
		firstName=userIntry.nextLine();
		System.out.println("Last Name");
		lastName=userIntry.nextLine();
		System.out.println("email");
		email=userIntry.nextLine();
		System.out.println("UserName");
		userName=userIntry.nextLine();
		System.out.println("Password");
		password=userIntry.nextLine();
		
		User user=new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setUserName(userName);
		user.setPassword(password);
		Message msg=new Message();
		msg.setType(MessageType.Register);		
		msg.setUser(user);
		
		return msg;
		
	}

}
