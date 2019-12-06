package Messages;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;

public class User implements Serializable{
	
    private String firstName;
    
    private String lastName;
	
	private String email;
	
    private String UserName;
	
	private String Password;	
    
    private Status status=Status.OffLine;
    
   // private HashSet<User> friendsList=new HashSet<User>();
    
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	/*public HashSet<User> getFriendsList() {
		return friendsList;
	}
	public void setFriendsList(HashSet<User> friendsList) {
		this.friendsList = friendsList;
	}*/
	
	
	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(o==null)
			return false;
		User user=(User)o;
		return getFirstName().equals(user.getFirstName())&&
				getLastName().equals(user.getLastName())&&
				getEmail().equals(user.getLastName())&&
				getUserName().equals(user.getUserName())&&
				getPassword().equals(user.getPassword());
	}
	
	@Override
	public int hashCode() {
		
		int result=17;
		result*=getUserName().hashCode();//getFirstName().hashCode()+getLastName().hashCode()+getEmail().hashCode()+getPassword().hashCode();
		return result;
	}
	
	


}
