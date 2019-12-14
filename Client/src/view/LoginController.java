package view;

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

import java.io.IOException;

import ClientSocket.ClientHome;

public class LoginController {
    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Hyperlink linkCreateAccount;
    
    @FXML
    private Text txtLogInError;

   // private ClientHome clientHome;

    @FXML
    private void loginActionbtn(ActionEvent event) {
        try {

            String username = txtUserName.getText();
            String password = txtPassword.getText();

            User user = new User();
            user.setUserName(username);
            user.setPassword(password);

            Message message = new Message();
            message.setType(MessageType.LogIn);
            message.setUser(user);

           //ClientHome clientHome=new ClientHome();
           boolean done=ClientHome.accessServer(message);
           if(done) {
        	   
        	   txtLogInError.setText("");
        	   try {
       			/*((Node) (event.getSource())).getScene().getWindow().hide();
       			
                   Parent parent = FXMLLoader.load(getClass().getResource("ChatScene.fxml"));			
                   Stage stage = new Stage();                   
                   Scene scene = new Scene(parent);
                   stage.setScene(scene);
                   stage.setResizable(false);
                   stage.show();*/
        		   
        		   ((Node) (event.getSource())).getScene().getWindow().hide();
        		   Stage stage = new Stage();  
        		  // Stage stage = ((Stage) ((Node) (event.getSource())).getScene().getWindow());
        		   Parent root = FXMLLoader.load(getClass().getResource("ChatScene.fxml"));	
        		   stage.setScene(new Scene(root));
        		   stage.setResizable(false);
        	       stage.show();
        		   
       			} catch (IOException e) {
       				
       				e.printStackTrace();
       			}
        	   
           }else {
        	   txtLogInError.setText("Wrong User name or Password !!!");
        	   txtLogInError.setWrappingWidth(500);
           }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Login Error");
        }
    }

	@FXML
    private void creatAccountActionLink(ActionEvent event) {
        try {
        	
           Stage stage = ((Stage) ((Node) (event.getSource())).getScene().getWindow());
 		   Parent root = FXMLLoader.load(getClass().getResource("RegisterScene.fxml"));	
 		   stage.setScene(new Scene(root));
 		   stage.setResizable(false);
 	       stage.show();
        	
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}