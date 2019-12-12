package Messages;


import java.io.Serializable;

public class FileTransfer implements Serializable{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3918756331560393733L;

	private String fileName;
	
	 private byte[] fileContent;
	 
	 private String absolutePath;
	 
	 public byte[] getFileContent() {
			return fileContent;
		}

		public void setFileContent(byte[] fileContent) {
			this.fileContent = fileContent;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getAbsolutePath() {
			return absolutePath;
		}

		public void setAbsolutePath(String absolutePath) {
			this.absolutePath = absolutePath;
		}


}
