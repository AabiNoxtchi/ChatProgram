
package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ClientSocket.ClientHome;
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
    		     setMsginBox(Msg.getUser().getUserName(),Msg.getMsg());
    		}
    }

    
    private void sendMessageAction(String chatMsg) {
    	
        if (!chatMsg.equals("")) {
                setMsginBox("You",chatMsg);
                Message msg=new Message();
                msg.setGroupMembers(recipients);
                msg.setMsg(chatMsg);
                msg.setType(MessageType.ChatMessage);
                ClientHome.sendMsgs(msg);
        }
    }

    public void setMsginBox(String sender,String chatMsg) {
    	  HBox cell = new HBox();
          Label sendLabel = new Label(sender+" : "+chatMsg);
          sendLabel.setMaxWidth(300);
          sendLabel.setWrapText(true);
          cell.getChildren().add(sendLabel);
          listviewChat.getItems().add(cell);
          listviewChat.scrollTo(cell);
          txtFieldMsg.setText(null);
          
          //save chat history : chatMsg /sender and tab name= this.recipients(if its group)
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

   
}
