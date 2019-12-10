package view;


import javafx.scene.control.Label;
import javafx.scene.control.Labeled;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.xml.soap.Node;

import ClientSocket.ClientHome;
import Messages.Message;
import Messages.MessageType;
import Messages.Status;
import Messages.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Pair;



public class ChatController implements Initializable{
	
	@FXML
	private Label homeLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab homeBox;
    @FXML
    private SplitPane splitPane;
    @FXML
    private VBox leftPane;
    @FXML
	private ListView<String> requestsListview=new ListView<String>();	
	@FXML
	private ListView<HBox> contactsListTab=new ListView<HBox>();
	
	private static String currentuser;
	
	static ObjectInputStream input;
	static ArrayList<String> chatTabs=new ArrayList<String>();
	static ArrayList<ChatBoxController> chatboxControllers=new ArrayList<ChatBoxController>();
	ObservableList<String> friendrequestsList =FXCollections.observableArrayList();
	ObservableList<HBox> contactsList = FXCollections.observableArrayList();	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		currentuser=ClientHome.getCurrentUser();
		homeLabel.setText(currentuser);
		input=ClientHome.getInput();
		SetFriendsList();
		SetRequestsList();
		Reciever reciever = new Reciever();
		reciever.start();
		
		splitPane.setDividerPositions(0.3246);
        leftPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.3246));
        tabPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.6546));
//      tabPane= new TabPane();

        try {
            homeBox.setContent(FXMLLoader.load(getClass().getResource("HomeBox.fxml")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

	}
	
	 private void SetRequestsList() {
		 requestsListview.setItems(friendrequestsList);
		
	}

	private void SetFriendsList() {
		ArrayList<User> friends=new ArrayList<User>();// fill contactsList from data base // 
		
	
		for(User friend : friends) {
			 SetFriendListItem(friend);
		}
		
		
		contactsListTab.setItems(contactsList);
			
	}
	    
	private void SetFriendListItem(User friend) {
		
		System.out.println("inside SetFriendListItem(User friend) method ");
		boolean done = false;
		HBox hbox=null;
		Text name=null;
		//CheckBox status = null;
		Circle status=null;
		System.out.println("contactslist size "+contactsList.size());
		
		
		for (int i = 0; i < contactsList.size(); i++)
	    {
			
//			System.out.println("contactslist.get("+i+").gettext() = "+contactsList.get(i).getText());
//			System.out.println("friend.getusername() =  "+friend.getUserName());
//			System.out.println(contactsList.get(i).getText().equals(friend.getUserName()));
//			System.out.println(contactsList.get(i).getText()==friend.getUserName());
			hbox=contactsList.get(i);
			ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
			name = (Text)listelements.get(0);
		
	        if (name.getText().equals(friend.getUserName())) //contactsList.get(i).get(0).getText().equals(friend.getUserName()))
	        {
	        	 status = (Circle)listelements.get(1);
	        	 //status.setSelected(true);
	        	 
	        	//status=contactsList.get(i);
//	        	contactsList.get(i).setSelected(true);
	        	done=true;
//	        	break;
	        }
	    }
		if(!done) {
			 hbox=new HBox();
			 name=new Text(friend.getUserName());
			 name.setWrappingWidth(140);
	         status=new Circle();
	         status.setRadius(8.0f); 
	         hbox.getChildren().addAll(name,status);
	         System.out.println("found no match = "+contactsList.size());
		}
         //int index=contactsList.indexOf(status);
         //System.out.println("index of friend = "+index);
         //if(index!=-1)status=contactsList.get(index);
         if(friend.getStatus()==Status.Online) {status.setFill(Color.GREENYELLOW);}//status.setSelected(true);
         else status.setFill(Color.LIGHTGRAY);
         //status.setDisable(true);
         //status.setStyle("-fx-opacity: 1");
         
         if(!done)
         contactsList.add(hbox);
		
		
	}

	@FXML
	private void ApproveFriendRequests(MouseEvent event) {
		 String friendName=requestsListview.getSelectionModel().getSelectedItem().toString();
    	 if(showDialog(friendName,"Text","Accept Friend Request","Friend request from : ",MessageType.ApprovedFriendRequest))
    	 {
    		 friendrequestsList.remove(friendName); 	   
		     System.out.println("approved friend request from  "+friendName);
    	 }
	}
	
	private boolean showDialog(String name,String TextFieldType,String title,String text,MessageType msgType) {
		
		
		
		 Dialog<String> dialog = new Dialog<>();
		 dialog.setTitle(title);
		
		 ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		 dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
		
		 GridPane grid = new GridPane();
		 grid.setHgap(10);
		 grid.setVgap(10);
		 grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
		
		 
		 if(TextFieldType=="TextField")
		 {
			 TextField txtFieldUserName = new TextField();
			 grid.add(new Text(text), 0, 0);
			 grid.add(txtFieldUserName, 1, 0);
			   
			 dialog.getDialogPane().setContent(grid);
			
			 // Request focus on the txtFieldUserName field by default.
			 Platform.runLater(() ->  txtFieldUserName.requestFocus());
			
			 dialog.setResultConverter(dialogButton -> {
			     if (dialogButton == addButtonType) {
			    	 
			    		 return new String( txtFieldUserName.getText());
			     }
			     return null;
			 });
		 }
		 else
		 {
			 Text txtFieldUserName = new Text();
		     txtFieldUserName.setText(name);
		     grid.add(new Text(text), 0, 0);
			 grid.add(txtFieldUserName, 1, 0);
			   
			 dialog.getDialogPane().setContent(grid);
			
			 // Request focus on the txtFieldUserName field by default.
			 Platform.runLater(() ->  txtFieldUserName.requestFocus());
			
			 dialog.setResultConverter(dialogButton -> {
			     if (dialogButton == addButtonType) {
			    	
						 return new String (txtFieldUserName.getText());
					
			        
			     }
			     return null;
			 });
		 }
		
		
		
		
		 Optional<String> result = dialog.showAndWait();
		 result.ifPresent(userName->{		
		 User user=new User();
		 user.setUserName(userName);
		 Message msg=new Message();
		 msg.setType(msgType);		 
		 msg.setUser(user);
		 ClientHome.sendMsgs(msg);
		 
		 User friend=new User();
		 friend.setUserName(userName);
		 SetFriendListItem(friend);
		 
		 //contactsList.add(userName);
		 
		 });
		 
		 return result.isPresent();
		
	}
    
    @FXML
    public void iconAddNewFriendAction(MouseEvent event) {
    	
    	if(showDialog("","TextField","Add New Friend","User Name :",MessageType.FriendRequest))
    		System.out.println("adding new friend  ");
		
	}		
	
	@FXML
	public void iconCreateGroupAction (MouseEvent event) {	}
	
	@FXML
	public void iconLogoutAction (MouseEvent event) {	}
	
	@FXML
	public void chatwithcontact(MouseEvent event) {
		HBox hbox= contactsListTab.getSelectionModel().getSelectedItem();
		
		ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
		Text text = (Text)listelements.get(0);
		String name=text.getText();
		if(chatTabs.contains(name))
		 System.out.println("clicked on " +name );
		else 
		 openNewTab(name,null);
	}
	
	private void openNewTab(String name,Message msg) {
		Platform.runLater(
				  () -> {
				 
		System.out.println("starting chat with "+name);			
		ChatBoxController controller=null;		
		 Tab newtab=new Tab(name);
		 try {
			 controller= new ChatBoxController(name,msg);
			 FXMLLoader loader=new FXMLLoader(getClass().getResource("ChatBox.fxml"));
			 loader.setController(controller);			
			 newtab.setContent(loader.load());	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 //tabs index start from 1,,ArrayList index from 0
		 int index= tabPane.getTabs().size();
		 System.out.println("tab pane index = "+tabPane.getTabs().size());
		 tabPane.getTabs().add(tabPane.getTabs().size(), newtab); 
		 
        tabPane.getSelectionModel().select( tabPane.getTabs().size()-1); 
        
		 chatTabs.add(index-1, name);
		 chatboxControllers.add(index-1,controller);
		 System.out.println("chatboxcontrollers at "+ (index-1) +" = "+chatboxControllers.get(index-1).getClass().getName());
				  }
				);
	}

	private void sendChatMsgToTab(String sender,Message recieved) {
		Platform.runLater(
				  () -> {
					  
		    System.out.println("looking for " +sender+" controller" );
			
			int index=chatTabs.indexOf(recieved.getUser().getUserName());
		    System.out.println("index for sender controller = "+index);
		    
			ChatBoxController controller=chatboxControllers.get(index);
			String chatMsg=recieved.getMsg();
			controller.setMsginBox(sender, chatMsg);
		    
				  });
	}
	
	private void addFriendRequest(Message recieved) {
		Platform.runLater(
				  () -> {
		requestsListview.getItems().add(recieved.getUser().getUserName());//exception just in first client ???Not on FX thread :solved
				  });
		
	}
	
	private void setContactsListStatusChanged(Message recieved) {
		Platform.runLater(
				  () -> {
					  System.out.println(recieved.getUser().getUserName()+" is "+recieved.getUser().getStatus());	
					 //green or grey something in contacts list view to show status 
					  SetFriendListItem(recieved.getUser());
					  
				  });
	}
		 
		    
	class Reciever extends Thread{
		
		 
		     public void run()
		     {
		    	 System.out.println("Starting thread reciever from server..");	
		    	 
		        	try {
		        		while(true)
				        {
				        	Message recieved = (Message)input.readObject();
				        	System.out.println("recieved msg"+recieved.getType());							
						
							if(recieved.getType()==MessageType.FriendRequest)
							{
								    addFriendRequest(recieved);									
							}
							else if(recieved.getType()==MessageType.StatusChanged) 
							{	
								setContactsListStatusChanged(recieved);
															
							}
							else if(recieved.getType()==MessageType.ChatMessage)
							{								
								String sender=recieved.getUser().getUserName();								
								if(chatTabs.contains(sender)) 
								{									
									sendChatMsgToTab(sender,recieved);					
								}
								else 
								    openNewTab(sender,recieved);
							}
					  }
		        	}catch (ClassNotFoundException|IOException e) {
						e.printStackTrace();
					}
		   }

			
   }

}

		
		
		