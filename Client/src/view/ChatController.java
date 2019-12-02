package view;


import java.awt.Label;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Pair;


public class ChatController implements Initializable{

	@FXML
	private ListView<String> requestsListview=new ListView<String>();
	
	@FXML
	private ListView<String> contactsListTab=new ListView<String>();
		
	
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab homeBox;
    @FXML
    private SplitPane splitPane;
    @FXML
    private VBox leftPane;
	
	static ObjectInputStream input;
	static ArrayList<String> friendrequestsArrayList =new ArrayList<String>();
	ObservableList<String> contactsArrayList = FXCollections.observableArrayList();
	static ArrayList<String> chatTabs=new ArrayList<String>();

	
	//take contacts from data base
	//static ArrayList<String> contactsArrayList =new ArrayList<String>();
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		input=ClientHome.getInput();
		SetFriendsList();
		recieveNotifications();
		
		splitPane.setDividerPositions(0.3246);
        leftPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.3246));
        tabPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.6546));
       

        try {
            homeBox.setContent(FXMLLoader.load(getClass().getResource("HomeBox.fxml")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

	}
	
	
    private void SetFriendsList() {
		//for(String name:contactsArrayList)
			//contactsListTab.getItems().add(name);
		contactsListTab.setItems(contactsArrayList);
		
	}
    
    @FXML
    public void iconAddNewFriendAction(MouseEvent event) {
		
		

		 Dialog<String> dialog = new Dialog<>();
		 dialog.setTitle("Add New Friend");
		
		 ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		 dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
		
		 GridPane grid = new GridPane();
		 grid.setHgap(10);
		 grid.setVgap(10);
		 grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
		
		 TextField txtFieldUserName = new TextField();
		 txtFieldUserName.setPromptText("username");
		
		 
		
		 grid.add(new Text("User Name :"), 0, 0);
		 grid.add(txtFieldUserName, 1, 0);
		
		 
		
		   
		 dialog.getDialogPane().setContent(grid);
		
		 // Request focus on the txtFieldEmail field by default.
		 Platform.runLater(() -> txtFieldUserName.requestFocus());
		
		 dialog.setResultConverter(dialogButton -> {
		     if (dialogButton == addButtonType) {
		         return new String(txtFieldUserName.getText());
		     }
		     return null;
		 });
		
		
		 Optional<String> result = dialog.showAndWait();
		 result.ifPresent(userName->{
		 Message msg=new Message();
		 User user=new User();
		 msg.setType(MessageType.FriendRequest); 
		 user.setUserName(result.get());
		 msg.setUser(user);
		 boolean done=ClientHome.accessServer(msg);
		 //add to friend list
		// contactsListTab.getItems().add(userName);
		 contactsArrayList.add(result.get());
		 });
	}		
	
	@FXML
	public void iconCreateGroupAction (MouseEvent event) {	}
	
	@FXML
	public void iconLogoutAction (MouseEvent event) {	}
	
	@FXML
	public void chatwithconatct(MouseEvent event) {
		String name=contactsListTab.getSelectionModel().getSelectedItem().toString();
		if(chatTabs.contains(name))
		 System.out.println("clicked on " +name );
		 Tab newtab=new Tab(name);
		 try {
			newtab.setContent(FXMLLoader.load(getClass().getResource("ChatBox.fxml")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 tabPane.getTabs().add( 
                 tabPane.getTabs().size() - 1, newtab); 

         // select the last tab 
         tabPane.getSelectionModel().select( 
                 tabPane.getTabs().size() - 2); 
		 tabPane.getTabs().add(newtab); 
		 chatTabs.add(name);
		
		 
		 
	}
	
	public void recieveNotifications (){
		
		 Runnable thread = new Runnable()
		   {
		     public void run()
		     {
		    	 requestsListview.setOnMouseClicked(event->
			     {
			    	 String selectedRequest=requestsListview.getSelectionModel().getSelectedItem().toString();
			    	 showApproveFriendRequestDialog(selectedRequest);
			    	 friendrequestsArrayList.remove(selectedRequest);
			    	 requestsListview.getItems().remove(selectedRequest);
			     });
		    	 
		    	 
		        
		        	try {
		        		while(true)
				        {
		        	Message recieved = (Message)input.readObject();
		        	System.out.println("recieved msg"+recieved.getType());
					
						
							//ChatController.getInstance().recieveNotifications(recieved);
							if(recieved.getType()==MessageType.FriendRequest)
								{
								
								//friendrequestsArrayList.add(recieved.getUser().getUserName());
								//for(String name:friendrequestsArrayList)
									requestsListview.getItems().add(recieved.getUser().getUserName());
								
								}
							else if(recieved.getType()==MessageType.StatusChanged) {
								if(recieved.getUser().getStatus()==Status.Online) {
									
									System.out.println(recieved.getUser().getUserName()+" is "+recieved.getUser().getStatus());
								}
								
							}
					  }
		        	}catch (ClassNotFoundException|IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		          
		   }
		   
	 };
	 
	   System.out.println("Starting thread recieve from server..");
	   new Thread(thread).start();    //
	  // System.out.println("Returning");
	   //return;
  
}

		 
		 
	private void showApproveFriendRequestDialog(String name) {
			
				 Dialog<String> dialog = new Dialog<>();
				 dialog.setTitle("Accept Friend Request");
				
				 ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
				 dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
				
				 GridPane grid = new GridPane();
				 grid.setHgap(10);
				 grid.setVgap(10);
				 grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
				
				 Text txtFieldUserName = new Text();
				 txtFieldUserName.setText(name);
				
				 grid.add(new Text("Friend request from : "), 0, 0);
				 grid.add(txtFieldUserName, 1, 0);
				   
				 dialog.getDialogPane().setContent(grid);
				
				 // Request focus on the txtFieldEmail field by default.
				 Platform.runLater(() -> txtFieldUserName.requestFocus());
				
				 dialog.setResultConverter(dialogButton -> {
				     if (dialogButton == addButtonType) {
				         return new String(txtFieldUserName.getText());
				     }
				     return null;
				 });
				
				
				 Optional<String> result = dialog.showAndWait();
				 result.ifPresent(userName->{
				 Message msg=new Message();
				 User user=new User();
				 msg.setType(MessageType.ApprovedFriendRequest); 
				 user.setUserName(result.get());
				 msg.setUser(user);
				 boolean done=ClientHome.accessServer(msg);
				 contactsArrayList.add(result.get());
				 //contactsListTab.getItems().add(result.get());
				 System.out.println("adding "+result.get());
				 
				 
				 });
				
			}
		     
		    
		  
	

	    
	   

}

		
		
		