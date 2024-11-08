package Messages;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 6218223176771690605L;
	
	private ArrayList<User> searchUsers;
	
	private String usersSearchString;

    private MessageType type;
    
    private String msg;
    
    private String groupMembers;//tabName or the recepient/recepients of the msg
	
	private User user; 
	
	private FileTransfer fileTransfer;
	
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

	public FileTransfer getFileTransfer() {
		return fileTransfer;
	}

	public void setFileTransfer(FileTransfer fileTransfer) {
		this.fileTransfer = fileTransfer;
	}

	public ArrayList<User> getSearchUsers() {
		return searchUsers;
	}

	public void setSearchUsers(ArrayList<User> searchUsers) {
		this.searchUsers = searchUsers;
	}

	public String getUsersSearchString() {
		return usersSearchString;
	}

	public void setUsersSearchString(String usersSearchString) {
		this.usersSearchString = usersSearchString;
	}

	
	

}
