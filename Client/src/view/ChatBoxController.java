
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

import Messages.Message;


/**
 * FXML Controller class
 *
 */
public class ChatBoxController implements Initializable {

    @FXML
    private VBox chatBox;
    @FXML
    private Label labelFriendName;
    @FXML
    private ImageView imgFriendStatus;
    @FXML
    private Label labelFriendStatus;
    @FXML
    private TextField txtFieldMsg;
    @FXML
    private Button btnSendAttach;
    @FXML
    private Image clips;
    @FXML
    private ListView<HBox> listviewChat;
    @FXML
    private Button saveBtn;
    @FXML
    private ToggleButton boldToggleBtn;

    @FXML
    private ToggleButton italicTogglebtn;

    @FXML
    private ToggleButton lineToggleBtn;

    @FXML
    private ComboBox<String> fontComboBox;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private ComboBox<String> fontSizeComboBox;

   
    String receiver;
    Message message;

    ArrayList<Message> History = new ArrayList<>();

    Boolean recMsgFlag = true;
    Boolean sendMsgFlag = true;
    Boolean conFlag = false;

   

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customizeEditorPane();
       // if ((message != null && message..contains("##")) || (receiver != null && receiver.contains("##"))) {
            btnSendAttach.setDisable(true);
            saveBtn.setDisable(true);

       // }
       // if (clientView.getHistory(receiver) != null) {
       //     loadHistory(clientView.getHistory(receiver));
       // }

        btnSendAttach.setTooltip(new Tooltip("Send Attachment"));
        saveBtn.setTooltip(new Tooltip("Save Message"));
    }

    @FXML
    void saveBtnAction(ActionEvent event) {
        Platform.runLater(() -> {

            Stage st = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            //Set extension filter
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml")
            );
            //Show save file dialog
            File file = fileChooser.showSaveDialog(st);

          /*  if (file != null) {
                ArrayList<Message> history = clientView.getHistory(receiver);
                clientView.saveXMLFile(file, history);
            }*/

        });
    }

    @FXML
    private void btnSendAttachAction(ActionEvent event) {

      
    }

    private void sendMessageAction() {
        if (!txtFieldMsg.getText().trim().equals("")) {
           

//        String color = "#" + Integer.toHexString(colorPicker.getValue().hashCode());
            String color = toRGBCode(colorPicker.getValue());
            String weight = (boldToggleBtn.isSelected()) ? "Bold" : "normal";
            String size = fontSizeComboBox.getSelectionModel().getSelectedItem();
            String style = (italicTogglebtn.isSelected()) ? "italic" : "normal";
            String font = fontComboBox.getSelectionModel().getSelectedItem();
            Boolean underline = lineToggleBtn.isSelected();

            Message msg = new Message();
           
                HBox cell = new HBox();
                VBox vbox = new VBox();

                Label sendLabel = new Label(txtFieldMsg.getText());
                sendLabel.setMaxWidth(300);
                sendLabel.setWrapText(true);
                sendLabel.setStyle("-fx-text-fill:" + color
                        + ";-fx-font-weight:" + weight
                        + ";-fx-font-size:" + size
                        + ";-fx-font-style:" + style
                        + ";-fx-font-family:\"" + font
                        + "\";-fx-underline:" + underline
                        + ";");

                if (recMsgFlag) {

                    sendLabel.getStyleClass().add("LabelSender");
                    if (!receiver.contains("##")) {
                        cell.getChildren().add( sendLabel);
                    } else {
                        cell.getChildren().add(sendLabel);
                    }
                    recMsgFlag = false;
                } else {
                    sendLabel.getStyleClass().add("LabelSenderSec");
                    cell.getChildren().add(sendLabel);
                    if (!receiver.contains("##")) {
                        cell.setMargin(sendLabel, new Insets(0, 0, 0, 32));
                    }
                }

                listviewChat.getItems().add(cell);
                listviewChat.scrollTo(cell);
                txtFieldMsg.setText(null);
                
                

           
        }
    }

    public void reciveMsg(Message message) throws IOException {

       
    }

    //handle Enter pressed action on txtFieldMessage and call the sendMessageAction ..
    @FXML
    private void txtFieldOnKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            sendMessageAction();
        }
    }

    void customizeEditorPane() {
        ObservableList<String> limitedFonts = FXCollections.observableArrayList("Arial", "Times", "Courier New", "Comic Sans MS");
        fontComboBox.setItems(limitedFonts);
        fontComboBox.getSelectionModel().selectFirst();

        ObservableList<String> fontSizes = FXCollections.observableArrayList("8", "10", "12", "14", "18", "24");
        fontSizeComboBox.setItems(fontSizes);
        fontSizeComboBox.getSelectionModel().select(2);

        colorPicker.setValue(Color.BLACK);
    }

   

   

   
    public static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
