
package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ClientSocket.ClientHome;
import Messages.FileTransfer;
import Messages.Message;
import Messages.MessageType;

/**
 * FXML Controller class
 *
 */
public class ChatBoxController implements Initializable {//load history in initialize ////save chat history :setMsginBox

    @FXML
    private VBox chatBox;
    @FXML
    private TextField txtFieldMsg;
    @FXML 
    private ListView<HBox> listviewChat;
   
    private String recipients;
    private Message Msg;
    
    public ChatBoxController(String tabName,Message msg) {
    	this.recipients=tabName;
    	this.Msg=msg;
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	//maybe here should load history of the chat //
    	if (Msg!=null)
    		{
    		     setMsginBox(Msg.getUser().getUserName(),Msg.getMsg(),Msg.getFileTransfer());
    		}
    }

    
    private void sendMessageAction(String chatMsg) {
    	
        if (!chatMsg.equals("")) {
                setMsginBox("",chatMsg,null);
                sendMsgToServer(chatMsg,MessageType.ChatMessage,null);
                
        }
    }

    private void sendMsgToServer(String chatMsg, MessageType msgType,FileTransfer fileTransfer) {
    	Message msg=new Message();
        msg.setGroupMembers(recipients);       
        msg.setType(msgType);
        if(msgType==MessageType.ChatMessage) {
        	 msg.setMsg(chatMsg);
        }else {
        	msg.setFileTransfer(fileTransfer);
        }
        ClientHome.sendMsgs(msg);
		
	}


	public void setMsginBox(String sender,String chatMsg,FileTransfer fileTransfer) {
		
		
		//save chat history : chatMsg +sender + tab name = this.recipients(if its group or individual )
        
        //if !chatMsg.isempty =>sender+tabName(recepients)+chatMsg //
  		//else sender+tabName+file(if file is saved there's path)(if not saved byte[] +name) //
		//in the listneres the user may save the file and it will have file path assigned to it // 
		
		if(sender.isEmpty())sender="You";
		if(chatMsg==null)chatMsg="";
		
    	  HBox cell = new HBox();     	  
          Label sendLabel = new Label(sender+" : "+chatMsg);
          
          if(fileTransfer==null) {
          sendLabel.setMaxWidth(300);
          sendLabel.setWrapText(true);         
          cell.getChildren().add(sendLabel);
         }else {
        	 HBox fileHBox=new HBox();
        	 Image img = new Image("/resources/file.png",35,35,true,false,true);
        	 //(String url, double requestedWidth,double requestedHeight,boolean preserveRatio,boolean smooth,boolean backgroundLoading)
        	 ImageView imgView = new ImageView(); 
        	 imgView.setImage(img);
        	 
        	 Label fileName = new Label(fileTransfer.getFileName());
        	 
        	 fileHBox.getChildren().addAll(imgView,fileName);
        	 String style = "-fx-background-color: Beige;";
        	 fileHBox.setStyle(style);
        	 cell.getChildren().addAll(sendLabel,fileHBox);
        	 
        	 //the listner for file containing items //
        	 fileHBox.setOnMouseClicked(event->{
        		 
        		 if(fileTransfer.getAbsolutePath()!=null) {
        			  try {
        				    Desktop desktop = Desktop.getDesktop();
						    desktop.open(new File(fileTransfer.getAbsolutePath()));
					} catch (IOException e) {
						e.printStackTrace();
					}
        		 }else {
        			 
        			    FileChooser chooser = new FileChooser();
        			    chooser.setTitle("Choose location To Save file"); 
        			    chooser.setInitialFileName(fileTransfer.getFileName());
        			    File selectedFile = chooser.showSaveDialog(null);
        			    if(selectedFile!=null) {
        			    //not sure whether to bother to check if user changed name cause the path is what matters
        			    System.out.println("selectedFile.getAbsolutePath() = "+selectedFile.getAbsolutePath()+ "\n"
        			    		+ "selectedFile.toPath() = "+selectedFile.toPath()+"\n"
        			    		+ "fileTransfer.getFileContent().length = "+fileTransfer.getFileContent().length);
        			    try {
        			    	
        			    	fileTransfer.setAbsolutePath(selectedFile.getAbsolutePath());        			    	
							Files.write(selectedFile.toPath(), fileTransfer.getFileContent());
						} catch (IOException e) {
							e.printStackTrace();
						}
        			    }
        		 }
        		 
        	 });
         }
    	 
          listviewChat.getItems().add(cell);
          listviewChat.scrollTo(cell);
          txtFieldMsg.setText(null);
	}

    @FXML
    private void txtFieldOnKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
        	if(!txtFieldMsg.getText().isEmpty()) {
        	String chatMsg=txtFieldMsg.getText().trim();
            sendMessageAction(chatMsg);
        	}
        }
    }
    
    @FXML
    private void iconAddAttachmentAction(MouseEvent event) {
    	
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Resource File");
    	fileChooser.getExtensionFilters().addAll(
    		     new FileChooser.ExtensionFilter("Text Files", "*.txt")
    		    ,new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg", "*.jpeg")
    		);
    	 Stage stage = ((Stage) ((Node) (event.getSource())).getScene().getWindow());
    	 File selectedFile = fileChooser.showOpenDialog(stage);
    	 
    	 if (selectedFile != null) {
    		 
    		 try {
				byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
				FileTransfer fileTransfer=new FileTransfer();
				fileTransfer.setFileName(selectedFile.getName());
				fileTransfer.setFileContent(fileContent);
				
				sendMsgToServer("",MessageType.FileTransfer,fileTransfer);
				fileTransfer.setAbsolutePath(selectedFile.getAbsolutePath());
                
                setMsginBox("","", fileTransfer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
    }
}









