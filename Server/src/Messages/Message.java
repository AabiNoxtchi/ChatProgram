package Messages;

import java.io.Serializable;

public class Message implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 6218223176771690605L;
	

    private MessageType type;
    
    private String msg;
    
   // private byte[] fileContent;
	
	private User user; 
	
	private FileTransfer fileTransfer;
	
	 private String groupMembers;//tabName or the recepient or recepients of the msg
	
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

	

}
