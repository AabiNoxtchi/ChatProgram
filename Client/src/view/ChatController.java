package view;



import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


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
    private Tab requestsTab;
    @FXML
	private ListView<String> requestsListview=new ListView<String>();	
	@FXML
	private ListView<HBox> contactsListTab=new ListView<HBox>();
	
	private static String currentuser;
	
	static Reciever reciever;
	static ObjectInputStream input;
	static ArrayList<String> chatTabs=new ArrayList<String>();
	static ArrayList<ChatBoxController> chatboxControllers=new ArrayList<ChatBoxController>();
	static HashMap<String,Integer> notificationNumbers=new HashMap<String, Integer>();
	
	    // fill unhandled friend requests from data base in SetRequestsList //
		// save new friend requests in AddToFriendRequests
		// remove from data base in RemoveFromFriendRequests
		// or on iconLogoutAction which sends to stop in Main view you can update all requests at once in data base. 
	ObservableList<String> friendrequestsList =FXCollections.observableArrayList();	
	ObservableList<HBox> contactsList = FXCollections.observableArrayList();
	
	    // fill contactsList from data base in SetFriendsList //  AddToFriends
	ArrayList<User> friends=new ArrayList<User>();
	
	int friendRequestNotificationsNumber=0;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		 
		currentuser=ClientHome.getCurrentUser();
		homeLabel.setText(currentuser);
		input=ClientHome.getInput();
		setFriendsList();
		setRequestsList();
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
	
	
	
	@FXML
	public void requestsTabAction(Event event) {
		 if (requestsTab.isSelected()) {
			 if(requestsTab.getGraphic()!=null) {
				friendRequestNotificationsNumber=0;
				requestsTab.setGraphic(null);
				requestsTab.setText("Requests");
			 }
		 }
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
	public void iconLogoutAction (MouseEvent event)  {
		   Platform.exit();
	}
	
	@FXML
	public void chatwithcontact(MouseEvent event) {
		HBox hbox= contactsListTab.getSelectionModel().getSelectedItem();
		
		ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
		Text text = (Text)listelements.get(0);
		String tabName=text.getText();
		
		 //tabs index start from 1,,ArrayList index from 0
		int index=chatTabs.indexOf(tabName);
		//if(!chatTabs.contains(tabName))
		if(index==-1)
		 openNewTab(tabName,null);
		else {
			tabPane.getSelectionModel().select( index+1); 
		}
	}
	
	private void addToFriends(User friend) {
		 friends.add(friend);//need to save in data base
		
	}
	
	private void addToFriendRequests(String friendName) {
		requestsListview.getItems().add(friendName);
		
	}
	
	private void removeFromFriendRequests(String friendName) {
		 friendrequestsList.remove(friendName); 
		 //remove from data base requests if exist
	}
	
	private String getPaddedString(String tabName) {
		if(tabName.length()<10)
		return String.format("%-" + 10 + "s", tabName);
		else return tabName;
	}
	
	private HBox createNotification(int number,String tabName) {
		HBox hbox=new HBox();
		StackPane p = new StackPane();
        Label label = new Label(""+number);
        label.setStyle("-fx-text-fill:white");
        Circle circle = new Circle(8, Color.RED);
        circle.setStrokeWidth(2.0);
        circle.setStyle("-fx-background-insets: 0 0 -1 0, 0, 1, 2;");
        circle.setSmooth(true);
        p.getChildren().addAll(circle, label);
        p.setMinWidth(6);
        tabName=getPaddedString(tabName);
        Label name=new Label(tabName);        
        hbox.getChildren().addAll(name,p);
        return hbox;
    }
	
    private void setRequestsList() {
    	
    	//friendrequestsList = if theres any requests in data base
		 requestsListview.setItems(friendrequestsList);		
	}

	private void setFriendsList() {
	
		// friends = fill contactsList from data base here // 
		
		for(User friend : friends) {
			 setFriendListItem(friend);
		}
		
		contactsListTab.setItems(contactsList);
			
	}
	    
	private void setFriendListItem(User friend) {
		
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
         else if(friend.getUserName().contains(",")) { status.setFill(Color.BLANCHEDALMOND); }
         else status.setFill(Color.LIGHTGRAY);
         if(!done)
         contactsList.add(hbox);
         
	}

	@FXML
	private void approveFriendRequests(MouseEvent event) {
		 String friendName=requestsListview.getSelectionModel().getSelectedItem().toString();
    	 if(showDialog(friendName,"Text","Accept Friend Request","Friend request from : ",MessageType.ApprovedFriendRequest))
    	 {
    		 removeFromFriendRequests(friendName);
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
			 //adding friend name or group members
			 TextField txtFieldUserName = new TextField();
			 grid.add(new Text(text), 0, 0);
			 grid.add(txtFieldUserName, 1, 0);
			   
			 dialog.getDialogPane().setContent(grid);
			
			 // Request focus on the txtFieldUserName field by default.
			 Platform.runLater(() ->  txtFieldUserName.requestFocus());
			
			// txtFieldUserName.setOnInputMethodTextChanged(value);
			// TextField textField = new TextField();
			 txtFieldUserName.textProperty().addListener((observable, oldValue, newValue) -> {
			     System.out.println("textfield changed from " + oldValue + " to " + newValue);
			 });
			 dialog.setResultConverter(dialogButton -> {
			     if (dialogButton == addButtonType) {
			    	 if(txtFieldUserName.getText().isEmpty()) {}			    	 
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
			 userName=sortFriendName(userName);
			 User friend=new User();
			 friend.setUserName(userName);
			 
			 if(!friends.contains(friend))
			 {
					 if(msgType!=null) {
				 
						 Message msg=new Message();
						 msg.setType(msgType);		 
						 msg.setUser(friend);
						 ClientHome.sendMsgs(msg);
					 }
				 addToFriends(friend);				
				 setFriendListItem(friend);
			 }
		 });
		 
		 return result.isPresent();
	}

//	private String sortFriendName(String userName) {
//		if(userName.contains(",")) {
//			String[] groupMembers=userName.split(",");
//			List<String> names= (List<String>) Arrays.asList(groupMembers);
//			java.util.Collections.sort((java.util.List<String>) names );
//			userName=String.join(",", names);
//		}
//		return userName;
//	}
	
	private String sortFriendName(String msgRecepient) {
		
		int index=msgRecepient.indexOf(currentuser);
		if(index>-1) {
			String toReplace="";
			if(msgRecepient.indexOf(currentuser)==0)
		         toReplace=currentuser+",";
			else 
				 toReplace=","+currentuser;
			
			msgRecepient=msgRecepient.replace(toReplace,"");
		}
	
	    String[] recepients=msgRecepient.split(",");
		List<String> names= (List<String>) Arrays.asList(recepients);
		java.util.Collections.sort((java.util.List<String>) names );
		String recepient=String.join(",", names);
		
		System.out.println("recepient = "+recepient);
		return recepient;
}

    
	private void openNewTab(String tabName,Message msg) {
		Platform.runLater(
				  () -> {
					  
					  User friend=new User();
					  friend.setUserName(tabName);
					  if(!checktabNameisFriend(friend)) {
						  //
						  //for groups or chat from people who are not friends
						  //
						  System.out.println("user "+tabName+" is not in friend list");	
						  setFriendListItem(friend);
						  }
					  
			ChatBoxController controller=null;
			String paddedTabName=getPaddedString(tabName);
					
			Tab newtab=new Tab(paddedTabName);
			
		 try {
			 controller= new ChatBoxController(tabName,msg);
			 FXMLLoader loader=new FXMLLoader(getClass().getResource("ChatBox.fxml"));
			 loader.setController(controller);			
			 newtab.setContent(loader.load());	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 //tabs size start from 1,,ArrayList index from 0
		 int index= tabPane.getTabs().size();
		 System.out.println("index = "+index);
		 tabPane.getTabs().add(index, newtab); 		 
		 chatTabs.add(index-1, tabName);
		 chatboxControllers.add(index-1,controller);
		 
		 if(msg==null) {
			 tabPane.getSelectionModel().select( index);  
		 }else {
			if(!newtab.isSelected()) {
			    notificationNumbers.put( tabName, 1);
				HBox icon =   createNotification(1,paddedTabName);
				newtab.setText("");
				newtab.setGraphic(icon);
				
			}
		 }
		 
		 newtab.setOnSelectionChanged(event -> {
		        if (newtab.isSelected()) {
		        	notificationNumbers.put( tabName, 0);
					newtab.setGraphic(null);
					newtab.setText(paddedTabName);
		        }
		    });
		
		  });
	}

	private boolean checktabNameisFriend(User friend) {
		if(friends.contains(friend))return true; 
		return false;
	}

	private void sendChatMsgToTab(String tabName,Message recieved) {
		Platform.runLater(
				  () -> {
					  
			int index=chatTabs.indexOf(tabName);
			ChatBoxController controller=chatboxControllers.get(index);
			
			
			String chatMsg=recieved.getMsg();
			controller.setMsginBox(recieved.getUser().getUserName(), chatMsg,recieved.getFileTransfer());
			
			Tab tab=tabPane.getTabs().get(index+1);
			if(!tab.isSelected()) {
				
				int notificationNumber=1; // not sure r we gonna save notifications in data base //
				if( !notificationNumbers.containsKey(tabName))
				{
			        notificationNumbers.put( tabName, notificationNumber);
				}
				else
				{		
					notificationNumbers.put(tabName, notificationNumbers.get(tabName) + 1);
					notificationNumber=notificationNumbers.get(tabName);
				}
				String paddedTabName=getPaddedString(tabName) ; 
				HBox icon =   createNotification(notificationNumber,paddedTabName);
				tab.setText("");
				tab.setGraphic(icon);
				
			}
		    
				  });
	}
	
	private void addFriendRequest(Message recieved) {
		Platform.runLater(
				  () -> {
					  addToFriendRequests(recieved.getUser().getUserName());
		
		
		if(!requestsTab.isSelected()) {
			
			HBox icon =   createNotification(friendRequestNotificationsNumber,requestsTab.getText());
			requestsTab.setText("");
			requestsTab.setGraphic(icon);
			}
					
	 });
}	

	private void setContactsListStatusChanged(Message recieved) {
		Platform.runLater(
				  () -> {
					  setFriendListItem(recieved.getUser());
					  
				  });
	}
		    
	private class Reciever extends Thread{
		
		     public void run()
		     {
		    	 Message recieved;
		        	try {
		        		while(true)
				        {
		        			recieved = (Message)input.readObject();
		        			System.out.println("recieved msg type : "+recieved.getType());
						
							if(recieved.getType()==MessageType.FriendRequest)
							{
								    friendRequestNotificationsNumber++;
								    addFriendRequest(recieved);									
							}
							else if(recieved.getType()==MessageType.StatusChanged) 
							{	
								setContactsListStatusChanged(recieved);
								System.out.println("recieved status changed "+recieved.getUser().getUserName()+" : "+recieved.getUser().getStatus());
															
							}
							else if(recieved.getType()==MessageType.ChatMessage||recieved.getType()==MessageType.FileTransfer)
							{								
								String tabName=sortFriendName(recieved.getGroupMembers());
								System.out.println("tab name = "+tabName);
								if(chatTabs.contains(tabName)) 
								{			
									System.out.println("chat tabs contains tab name : "+tabName);
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

		
		
		