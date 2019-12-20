package view;



import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
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
import javafx.scene.shape.Shape;
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
	
	static Map<String,Map<String,Status>> groupMembersStatus=new HashMap<String,Map<String,Status>>();
	
	    // fill unhandled friend requests from data base in SetRequestsList //
		// save new friend requests in AddToFriendRequests
		// remove from data base in RemoveFromFriendRequests
		// or on iconLogoutAction which sends to stop in Main view you can update all requests at once in data base. 
	ObservableList<String> friendrequestsList =FXCollections.observableArrayList();	
	ObservableList<HBox> contactsList = FXCollections.observableArrayList();
	
	    // fill contactsList from data base in SetFriendsList //  AddToFriends
	ArrayList<User> friends=new ArrayList<User>();
	
	int friendRequestNotificationsNumber=0;
	
	//serach users when adding friend
	private List<User> filteredEntries;   
    private ContextMenu entriesPopup;
    private TextField txtFieldUserName;
	
	
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
   	
   	    showDialog("","TextField","Add New Friend or a Group"," search User Name :",MessageType.FriendRequest);
		
	}		
		
	@FXML
	public void iconLogoutAction (MouseEvent event)  {
		   Platform.exit();
	}
	
	@FXML
	public void chatwithcontact(MouseEvent event) {
		HBox hbox= contactsListTab.getSelectionModel().getSelectedItem();
		if(hbox!=null) {
		ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
		Text text = (Text)listelements.get(0);
		String tabName=text.getText();
		
		int index=chatTabs.indexOf(tabName);
		if(index==-1)
		 openNewTab(tabName,null);
		else {
			tabPane.getSelectionModel().select( index+1); 
		}
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
		StackPane p=createNotificationCircle(number,Color.RED);
        tabName=getPaddedString(tabName);
        Label name=new Label(tabName);        
        hbox.getChildren().addAll(name,p);
        
        return hbox;
    }
	
    private StackPane createNotificationCircle(int number,Color color) {
    	StackPane p = new StackPane();
    	
        Label label = number>=1?new Label(""+number):new Label("");
        label.setStyle("-fx-text-fill:white");
        Circle circle = new Circle(8, color);
        circle.setStrokeWidth(2.0);
        circle.setStyle("-fx-background-insets: 0 0 -1 0, 0, 1, 2;");
        circle.setSmooth(true);
        p.getChildren().addAll(circle, label);
        p.setMinWidth(6);
        return p;
	}

	private void setRequestsList() {
    	
    	//friendrequestsList = if theres any requests in data base
		 requestsListview.setItems(friendrequestsList);		
	}

	private void setFriendsList() {
	
		// friends = fill contactsList from data base here // 
		
		for(User friend : friends) {
			 setFriendListItem(friend,MessageType.ApprovedFriendRequest);
		}
		
		contactsListTab.setItems(contactsList);
			
	}
	    
	private void setFriendListItem(User friend,MessageType type) {
		
		boolean done = false;
		HBox hbox=null;
		Text name=null;
		StackPane status=null;
		
		for (int i = 0; i < contactsList.size(); i++)
	    {
			hbox=contactsList.get(i);
			ObservableList<javafx.scene.Node> listelements=hbox.getChildren();
			name = (Text)listelements.get(0);
			
			String nameText=name.getText();
			if(nameText.contains(","))
			{
				if(nameText==friend.getUserName()) {done=true;}
				String[] names=nameText.split(",");
				for(String r:names)
				{
					if (r.equals(friend.getUserName())) 
			        {
			        	status = (StackPane)listelements.get(1);
			        	boolean isChecked = groupMembersStatus.get(nameText).get(r) == friend.getStatus();
			        	
			        	if(type==MessageType.StatusChanged && !isChecked) setColors(status,friend.getStatus(),name);
			        	if(!isChecked && friend.getStatus()!=null) {groupMembersStatus.get(nameText).put(r, friend.getStatus());}
			        }
				}
			}
			else if (nameText.equals(friend.getUserName())) 
	        {
	        	status = (StackPane)listelements.get(1);
	        	done=true;
	        	if(type==MessageType.StatusChanged) setColors(status,friend.getStatus(),name);
	        	
	        }
	    }
		
		if(!done && type==MessageType.ApprovedFriendRequest) {
			 hbox=new HBox();
			 name=new Text(friend.getUserName());
			 name.setWrappingWidth(140);
			 status=createNotificationCircle(0,Color.LIGHTGRAY);
			 
			 if(friend.getUserName().contains(",")) {
				 String[] members=friend.getUserName().split(",");
				 Map<String,Status> membersStatus=new HashMap<String,Status>();
				 for(String member:members) {
					 
					 membersStatus.put(member, Status.OffLine);}
				     groupMembersStatus.put(friend.getUserName(), membersStatus);
			 }
			 
	         hbox.getChildren().addAll(name,status);				
			 contactsList.add(hbox);
		}
	}

	private void setColors(StackPane status, Status friendStatus, Text name) {
		        if(name.getText().contains(",")) { 
					
					int number= ((Label)status.getChildren().get(1)).getText().isEmpty()?0:Integer.parseInt(((Label)status.getChildren().get(1)).getText());
					if(friendStatus==Status.Online) { 
						number+=1;
						((Shape) status.getChildren().get(0)).setFill(Color.GREENYELLOW);
						String label = number >= 1 ? ""+number : "";
						((Label) status.getChildren().get(1)).setText(label);
						
						}	       
			         else {
			        	 
			        	 number-=1;
							if(number >0)((Shape) status.getChildren().get(0)).setFill(Color.GREENYELLOW);
							else ((Shape) status.getChildren().get(0)).setFill(Color.LIGHTGRAY);
							String label = number >= 1 ? ""+number : "";
							((Label) status.getChildren().get(1)).setText(label);
			        	 
			         }
				} 					
				else {
					
			         if(friendStatus==Status.Online) { ((Shape) status.getChildren().get(0)).setFill(Color.GREENYELLOW);	 }          
			         else ((Shape) status.getChildren().get(0)).setFill(Color.LIGHTGRAY);
		         
				}
		
	}

	@FXML
	private void approveFriendRequests(MouseEvent event) {
		 String friendName=requestsListview.getSelectionModel().getSelectedItem().toString();
    	 showDialog(friendName,"Text","Accept Friend Request","Friend request from : ",MessageType.ApprovedFriendRequest);
    	
	}

	private boolean showDialog(String name,String TextFieldType,String title,String text,MessageType msgType) {
		
		 Dialog<String> dialog = new Dialog<>();
		 dialog.setTitle(title);
		
		
		
		 GridPane grid = new GridPane();
		 grid.setHgap(10);
		 grid.setVgap(10);
		 grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
		
		 
		 if(TextFieldType=="TextField")
		 {
			 //adding friend name or group members
			 txtFieldUserName = new TextField();
			 grid.add(new Text(text), 0, 0);			
			 grid.add(txtFieldUserName, 1, 0);
			 Button btn=new Button("add");
			 grid.add(btn, 2, 0);
			 
			 Text friendsToAdd = new Text("friend/Group Name ");
			 grid.add(friendsToAdd, 0, 1);
			 Text friends = new Text("");
			 grid.add(friends, 1, 1);
			 
			 
			 ButtonType addButtonType = new ButtonType("Send friend request", ButtonBar.ButtonData.OK_DONE);		 
			 dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
			   
			 dialog.getDialogPane().setContent(grid);
			 Platform.runLater(() ->  txtFieldUserName.requestFocus());
			
			 btn.setOnAction(new EventHandler<ActionEvent>() {
				    @Override public void handle(ActionEvent e) {
				        if(!txtFieldUserName.getText().isEmpty()) {
				        	 boolean valid=false;
					    	 for(User user: filteredEntries)
					    	 {
					    		 if(user.getUserName().equals(txtFieldUserName.getText())) valid=true;
					    				 
					    	 }
					    	 
					    	 if(valid) {
					    		 if(!friends.getText().isEmpty()) {
					    			 if(!friends.getText().contains(","))
					    			 {
					    				 if(!friends.getText().equals(txtFieldUserName.getText()))
					    					 friends.setText(friends.getText()+","+txtFieldUserName.getText());
					    			 }else {
					    				 String[] friendsArr=friends.getText().split(",");
					    				 boolean exists=false;
					    				 for(String r:friendsArr) {
					    					 if(r.equals(txtFieldUserName.getText()))exists=true;
					    				 }
					    				 if(!exists)friends.setText(friends.getText()+","+txtFieldUserName.getText());
					    			 }
					    		 }else {
					    			 friends.setText(txtFieldUserName.getText());
					    		 }
					    	 } 
				        }
				        
				        txtFieldUserName.setText("");
				    }
				});
			 
			 txtFieldUserName.textProperty().addListener((observable, oldValue, newValue) -> {
			     
			     if (newValue == null || newValue.isEmpty() || newValue.trim().isEmpty()) {
		                entriesPopup.hide();
		            } else {
		            	
		            	Message msg=new Message();
		            	msg.setType(MessageType.SearchUsers);
		            	msg.setUsersSearchString(newValue);
		            	ClientHome.sendMsgs(msg);
		                
		            }
			 });
			 dialog.setResultConverter(dialogButton -> {
			     if (dialogButton == addButtonType) {
			    	 if(friends.getText().isEmpty()) {}
			    	 else return new String( friends.getText()); 
			    		 
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
			 
			 ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
			 ButtonType declineButtonType = new ButtonType("Decline", ButtonBar.ButtonData.NO);
			 dialog.getDialogPane().getButtonTypes().addAll(addButtonType,declineButtonType, ButtonType.CANCEL);
			   
			 dialog.getDialogPane().setContent(grid);
			 Platform.runLater(() ->  txtFieldUserName.requestFocus());
			
			 dialog.setResultConverter(dialogButton -> {
			     if (dialogButton == addButtonType) {
			    	
						 return new String (txtFieldUserName.getText());
			        
			     }else if(dialogButton == declineButtonType) {
			    	 
			    	     return new String ("declined"+name);
			     }
			     return null;
			 });
		 }
		
		
		 Optional<String> result = dialog.showAndWait();
			 
		 result.ifPresent(userName->{
			 
			 if(userName.startsWith("declined")) {
				 User friend=new User();
				 friend.setUserName(userName.replace("declined", ""));
				 Message msg=new Message();
				 msg.setType(MessageType.DeclinedFriendRequest);		 
				 msg.setUser(friend);
				 ClientHome.sendMsgs(msg);
			 }
			 else {
			 userName=sortFriendName(userName);
			 User friend=new User();
			 friend.setUserName(userName);
			 
			 for(User user:friends) {System.out.print(user.getUserName()+" , ");}
			 if(!friends.contains(friend))///??????
			 {
						 Message msg=new Message();
						 msg.setType(msgType);		 
						 msg.setUser(friend);
						 ClientHome.sendMsgs(msg);
					
				 if(msgType==MessageType.ApprovedFriendRequest)removeFromFriendRequests(userName);
				 addToFriends(friend);	
				 setFriendListItem(friend,MessageType.ApprovedFriendRequest);
			 }
			}
		 });
		 
		 return result.isPresent();
	}
		 
    private void populatePopup(List<User> searchResult) {
			 
			 if(entriesPopup !=null && entriesPopup.isShowing())entriesPopup.hide();
			 this.entriesPopup = new ContextMenu();
		        List<CustomMenuItem> menuItems = new LinkedList<>();
		        int count=searchResult.size();
		        for (int i = 0; i < count; i++) {
		          final String result = searchResult.get(i).getUserName();
		          Label entryLabel = new Label();
		          entryLabel.setText(result);
		          entryLabel.setPrefWidth(100);
		          CustomMenuItem item = new CustomMenuItem(entryLabel, true);
		          menuItems.add(item);

		          item.setOnAction(actionEvent -> {
		        	  txtFieldUserName.setText(result);
		              entriesPopup.hide();
		          });
		        }
		        
		        entriesPopup.getItems().clear();
		        entriesPopup.getItems().addAll(menuItems);
		   }
	
	private String sortFriendName(String msgRecepient) {
		
		int index=msgRecepient.indexOf(currentuser);
				
		if(index>-1) {
			if(index==0 && msgRecepient.indexOf(",")==currentuser.length()) {
				
				msgRecepient=msgRecepient.substring(currentuser.length()+1);
			}
			else if(index>0)
				{
				
				if(index+currentuser.length()==msgRecepient.length())msgRecepient=msgRecepient.substring(0, index);
				else {
					msgRecepient=msgRecepient.substring(0,index)+msgRecepient.substring(index+currentuser.length()+1);
				}
			}
			
		}
		
	    String[] recepients=msgRecepient.split(",");
		List<String> names= (List<String>) Arrays.asList(recepients);
		java.util.Collections.sort((java.util.List<String>) names );
		String recepient=String.join(",", names);
		return recepient;
}
    
	private void openNewTab(String tabName,Message msg) {
		Platform.runLater(
				  () -> {
					  
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
		 
		 int index= tabPane.getTabs().size();
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
					 
					  String friendName=sortFriendName(recieved.getUser().getUserName());
					  addToFriendRequests(friendName);
		
		
		if(!requestsTab.isSelected()) {
		    friendRequestNotificationsNumber++;
			HBox icon =   createNotification(friendRequestNotificationsNumber,requestsTab.getText());
			requestsTab.setText("");
			requestsTab.setGraphic(icon);
			}
					
	 });
}	

	private void setContactsListStatusChanged(Message recieved) {
		Platform.runLater(
				  () -> {
					  setFriendListItem(recieved.getUser(),MessageType.StatusChanged);
					  
				  });
	}
	
	private void populateUserSearchOptions(Message recieved) {
		
		Platform.runLater(
				  () -> {
					  
        filteredEntries = recieved.getSearchUsers();
        if (!filteredEntries.isEmpty()) {
            populatePopup(filteredEntries);
            if (!entriesPopup.isShowing()) { 
                entriesPopup.show(txtFieldUserName, Side.BOTTOM, 0, 0); 
            }
        } else {
            entriesPopup.hide();
        }
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
						
							if(recieved.getType()==MessageType.FriendRequest)
							{
								    addFriendRequest(recieved);									
							}
							else if(recieved.getType()==MessageType.StatusChanged) 
							{	
								setContactsListStatusChanged(recieved);
															
							}
							else if(recieved.getType()==MessageType.ChatMessage||recieved.getType()==MessageType.FileTransfer)
							{								
								String tabName=sortFriendName(recieved.getGroupMembers());
								if(chatTabs.contains(tabName)) 
								{			
									sendChatMsgToTab(tabName,recieved);					
								}
								else 
								    openNewTab(tabName,recieved);
							}
							else if(recieved.getType()==MessageType.SearchUsers)
							{
								populateUserSearchOptions(recieved);
							}
							
					  }
		        	}catch (ClassNotFoundException|IOException e) {
						e.printStackTrace();
		        		System.out.println("connection got exception ..");	
					}
		   }
   }
}

		
		
		