package view;



import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import ClientSocket.ClientHome;
import Messages.Message;
import Messages.MessageType;
import Messages.Status;
import Messages.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;




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
	
	static Reciever reciever;
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
		reciever = new Reciever();
		reciever.start();
		
		splitPane.setDividerPositions(0.3246);
        leftPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.3246));
        tabPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.6546));

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
		
		boolean done = false;
		HBox hbox=null;
		Text name=null;
		Circle status=null;
		
		for (int i = 0; i < contactsList.size(); i++)
	    {
			hbox=contactsList.get(i);
			ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
			name = (Text)listelements.get(0);
		
	        if (name.getText().equals(friend.getUserName())) 
	        {
	        	 status = (Circle)listelements.get(1);
	        	done=true;
	        	break;
	        }
	    }
		if(!done) {
			 hbox=new HBox();
			 name=new Text(friend.getUserName());
			 name.setWrappingWidth(140);
	         status=new Circle();
	         status.setRadius(8.0f); 
	         hbox.getChildren().addAll(name,status);
		}
		
         if(friend.getStatus()==Status.Online) {status.setFill(Color.GREENYELLOW);}
         else status.setFill(Color.LIGHTGRAY);         
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
			 if(msgType!=null) {
		 User user=new User();
		 user.setUserName(userName);
		 Message msg=new Message();
		 msg.setType(msgType);		 
		 msg.setUser(user);
		 ClientHome.sendMsgs(msg);
			 }
		 User friend=new User();
		 userName=sortFriendName(userName);
		 System.out.println(userName);
		 friend.setUserName(userName);
		 SetFriendListItem(friend);
		 });
		 
		 return result.isPresent();
		
	}
	
	private String sortFriendName(String userName) {
		if(userName.contains(",")) {
			String[] groupMembers=userName.split(",");
			List<String> names= (List<String>) Arrays.asList(groupMembers);
			java.util.Collections.sort((java.util.List<String>) names );
			userName=String.join(",", names);
		}
		return userName;
	}
    
    @FXML
    public void iconAddNewFriendAction(MouseEvent event) {
    	
    	showDialog("","TextField","Add New Friend","User Name :",MessageType.FriendRequest);
    		
		
	}		
	
	@FXML
	public void iconCreateGroupAction (MouseEvent event) {
		showDialog("","TextField","Add New Group \nseperated with comma","Group Members :",null);
    		
		
	}
	
	
	@FXML
	public void iconLogoutAction (MouseEvent event) throws InterruptedException {
	       reciever.interrupt();   
		   Platform.exit();
	}
	
	@FXML
	public void chatwithcontact(MouseEvent event) {
		HBox hbox= contactsListTab.getSelectionModel().getSelectedItem();
		
		ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
		Text text = (Text)listelements.get(0);
		String tabName=text.getText();
		if(!chatTabs.contains(tabName))		 
		 openNewTab(tabName,null);
	}
	
	private void openNewTab(String tabName,Message msg) {
		Platform.runLater(
				  () -> {
				 
		System.out.println("starting chat with "+tabName);			
		ChatBoxController controller=null;
		 Tab newtab=new Tab(tabName);
		 try {
			 controller= new ChatBoxController(tabName,msg);
			 FXMLLoader loader=new FXMLLoader(getClass().getResource("ChatBox.fxml"));
			 loader.setController(controller);			
			 newtab.setContent(loader.load());	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 //tabs index start from 1,,ArrayList index from 0
		 int index= tabPane.getTabs().size();
		 tabPane.getTabs().add(tabPane.getTabs().size(), newtab); 		 
         tabPane.getSelectionModel().select( tabPane.getTabs().size()-1);         
		 chatTabs.add(index-1, tabName);
		 chatboxControllers.add(index-1,controller);
				  }
				);
	}

	private void sendChatMsgToTab(String tabName,Message recieved) {
		Platform.runLater(
				  () -> {
					  
			int index=chatTabs.indexOf(tabName);
			ChatBoxController controller=chatboxControllers.get(index);
			String chatMsg=recieved.getMsg();
			controller.setMsginBox(recieved.getUser().getUserName(), chatMsg);
		    
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
					  SetFriendListItem(recieved.getUser());
					  
				  });
	}
		 
		    
	class Reciever extends Thread{
		
		     public void run()
		     {
		    	 Message recieved;
		        	try {
		        		while(true)
				        {
		        			recieved = (Message)input.readObject();
						
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
								String tabName=recieved.getGroupMembers();								
								if(chatTabs.contains(tabName)) 
								{									
									sendChatMsgToTab(tabName,recieved);					
								}
								else 
								    openNewTab(tabName,recieved);
							}
					  }
		        	}catch (ClassNotFoundException|IOException e) {
						//e.printStackTrace();
		        		System.out.println("connection got exception ..");	
					}
		   }
   }
}

		
		
		