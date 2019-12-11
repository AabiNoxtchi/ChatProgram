package Messages;

import java.io.Serializable;

public class Message implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 6218223176771690605L;
	

    private MessageType type;
    
    private String msg;
	
	private User user; 
	
	 private String groupMembers;
	
	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(String groupMembers) {
		this.groupMembers = groupMembers;
	}

	

}
