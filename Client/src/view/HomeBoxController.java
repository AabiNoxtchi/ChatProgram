package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;



public class HomeBoxController implements Initializable {

    @FXML
    private VBox homeBox;
    @FXML
    private Button btnNewFriend;
    @FXML
    private Image clips;
    @FXML
    private TextFlow txtFlowServerMsg;
    @FXML
    private Label labelUserName;
    @FXML
    private Text serverMessage;

   

    public HomeBoxController() {
       
    }

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	serverMessage.setText("Connected to chat service");
            updatePageInfo();
    }
    
    public void updatePageInfo() {
        
        labelUserName.setText(" ");
    }
     
    
}
