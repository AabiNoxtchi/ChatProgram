package view;

import ClientSocket.ClientHome;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private Stage primaryStageObj;

    @Override
    public void start(Stage primaryStage) throws Exception{
    	primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));
        primaryStage.setTitle("Our Skype !!!");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    
    public Stage getPrimaryStage() {
        return this.primaryStageObj;
    }
    
    @Override
    public void stop(){
    	
    	
        System.out.println("Stage is closing");
        ClientHome.logOut();
        
       
    }
}
