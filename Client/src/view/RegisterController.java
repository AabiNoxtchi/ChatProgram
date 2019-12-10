package view;

import java.io.IOException;

import ClientSocket.ClientHome;
import Messages.Message;
import Messages.MessageType;
import Messages.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterController {
	  
	   @FXML
	    private TextField txtUserName;

	    @FXML
	    private PasswordField txtPassword;

	    @FXML
	    private Button btnRegister;
	    
	    @FXML 
	    Text txtRegisterError;
	    
	    @FXML
	    private Hyperlink linkLogIn;
	    
	    @FXML
	    private void btnRegisterAction(ActionEvent event) {
	        try {	           
	        	
	            String username = txtUserName.getText();
	            String password = txtPassword.getText();
	            
	            if(username.length()==0 || password.length()==0) {
	            	
	            	txtRegisterError.setText("Error in Register Fields ,All Fields are Requiered  ! ");
	            	txtRegisterError.setWrappingWidth(400);
	            }else{	            	
	            
	            User user = new User();	           
	            user.setUserName(username);
	            user.setPassword(password);

	            Message message = new Message();
	            message.setType(MessageType.Register);
	            message.setUser(user);

	           boolean done= ClientHome.accessServer(message);
	           if(done) {
	        	   txtRegisterError.setText("");
	        	   continueToLoginScene(event);        	   
	           }else {
	        	   txtRegisterError.setText("Error in Register Fields ,User name already exists ! ");
	        	   txtRegisterError.setWrappingWidth(600);
	           }
	        }
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            System.out.println("Register Error");
	        }
	    }
	    
	    @FXML
	    private void linkLogInAction(ActionEvent event) {
	    	continueToLoginScene(event);
	    }

		private void continueToLoginScene(ActionEvent event) {
			
			try {
			/*((Node) (event.getSource())).getScene().getWindow().hide();
            Parent parent = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));			
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();*/
				
				Stage stage = ((Stage) ((Node) (event.getSource())).getScene().getWindow());
		 		   Parent root = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));	
		 		   stage.setScene(new Scene(root));
		 		   stage.setResizable(false);
		 	       stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
