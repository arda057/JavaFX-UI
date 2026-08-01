package JavaFXexample.controller;

import JavaFXexample.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML
    private Label welcomeLabel;

    public void setUsername(String username)
    {
        welcomeLabel.setText("Welcome " + username);
    }

    @FXML
    private void logout(ActionEvent event){
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/login.fxml", "/JavaFXexample/css/style.css", false);
    }

    @FXML
    private void studentTable(ActionEvent event){        
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/student.fxml", "/JavaFXexample/css/style.css",true);
    }
}
