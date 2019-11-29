package Messages;

import java.io.Serializable;

public class User implements Serializable{
	
	private String UserName;
	
	private String Password;
	
	private String firstName;
	
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
	private String lastName;
	
	private String email;
	
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
		result*=getFirstName().hashCode()+getLastName().hashCode()+getEmail().hashCode()+getUserName().hashCode()+getPassword().hashCode();
		return result;
	}

}
