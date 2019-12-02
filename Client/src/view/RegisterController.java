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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
	  @FXML
	  private TextField txtFirstName;
	  
	  @FXML
	  private TextField txtLastName;
	  
	  @FXML
	    private TextField txtUserName;
	  
	  @FXML
	  private TextField txtEmail;

	    @FXML
	    private PasswordField txtPassword;

	    @FXML
	    private Button btnRegister;
	    
	    @FXML
	    private void btnRegisterAction(ActionEvent event) {
	        try {

	            String errorMsg = "";
	            
	            String firstname = txtFirstName.getText();
	            String lastname = txtLastName.getText();
	            String email = txtEmail.getText();
	            String username = txtUserName.getText();
	            String password = txtPassword.getText();

	            User user = new User();
	            user.setFirstName(firstname);
	            user.setLastName(lastname);
	            user.setEmail(email);
	            user.setUserName(username);
	            user.setPassword(password);

	            Message message = new Message();
	            message.setType(MessageType.Register);
	            message.setUser(user);

	           //ClientHome clientHome=new ClientHome();
	           boolean done= ClientHome.accessServer(message);
	           if(done) {
	        	   continueToLoginScene(event);        	   
	           }

	        } catch (Exception ex) {
	            ex.printStackTrace();
	            System.out.println("Register Error");
	        }
	    }

		private void continueToLoginScene(ActionEvent event) {
			
			try {
			((Node) (event.getSource())).getScene().getWindow().hide();
            Parent parent = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));			
            Stage stage = new Stage();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
