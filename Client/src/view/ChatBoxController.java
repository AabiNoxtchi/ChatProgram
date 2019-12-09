
package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ClientSocket.ClientHome;
import Messages.Message;
import Messages.MessageType;
import Messages.User;


/**
 * FXML Controller class
 *
 */
public class ChatBoxController implements Initializable {

    @FXML
    private VBox chatBox;
    
    @FXML
    private TextField txtFieldMsg;
   
    @FXML
    private ListView<HBox> listviewChat;
   
  
   // ArrayList<Message> History = new ArrayList<>();
    private String recipient;
    private Message Msg;
    
    public ChatBoxController(String recipient) {
    	this.recipient=recipient;
    }
    public ChatBoxController(String recipient,Message msg) {
    	this.recipient=recipient;
    	this.Msg=msg;
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	if (Msg!=null)setMsginBox(Msg.getUser().getUserName(),Msg.getMsg());
    	
    }

    
    private void sendMessageAction(String chatMsg) {
    	
        if (!chatMsg.equals("")) {

            //Message msg = new Message();
        	
           
               setMsginBox("You",chatMsg);
        	
        	
                //add to history
               
                User user=new User();
                user.setUserName(recipient);
                Message msg=new Message();
                msg.setUser(user);
                msg.setMsg(chatMsg);
                msg.setType(MessageType.ChatMessage);
                ClientHome.sendMsgs(msg);
                
                
           
        }
    }

    public void setMsginBox(String sender,String chatMsg) {
    	 HBox cell = new HBox();
         // VBox vbox = new VBox();

          Label sendLabel = new Label(sender+" : "+chatMsg);
          sendLabel.setMaxWidth(300);
          sendLabel.setWrapText(true);
          
          cell.getChildren().add(sendLabel);
          
          listviewChat.getItems().add(cell);
          listviewChat.scrollTo(cell);
          txtFieldMsg.setText(null);
            
		
	}


	public void reciveMsg(Message message) throws IOException {

       
    }

    @FXML
    private void txtFieldOnKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
        	String chatMsg=txtFieldMsg.getText().trim();
            sendMessageAction(chatMsg);
        }
    }

   
}
