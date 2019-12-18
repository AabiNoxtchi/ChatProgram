package Messages;

import java.io.Serializable;

public class User implements Serializable{
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private Status status;
	
	private String UserName;
	
	private String Password;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
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
	
	@Override
	public boolean equals(Object o) {
		if(this==o)
			return true;
		if(o==null)
			return false;
		User user=(User)o;
		return
				getUserName().equals(user.getUserName())&& getStatus()!=null?getStatus().equals(user.getStatus()):true;
				
	}
	
	@Override
	public int hashCode() {
		
		int result=1;
		result*=getUserName().hashCode();
		return result;
	}
	
public int registerLoginhashCode() {
		
		int result=1;
		result*=(getUserName().hashCode()+getPassword().hashCode());
		return result;
	}
}
