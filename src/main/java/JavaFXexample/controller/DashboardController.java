package JavaFXexample.controller;

import java.io.ByteArrayInputStream;

import JavaFXexample.model.User;
import JavaFXexample.util.SceneManager;
import JavaFXexample.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DashboardController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private ImageView profilePhoto;

    @FXML
    private void initialize(){
        User user = Session.getCurrentUser();
        byte[] photoBytes = null;

        if(user != null){
            welcomeLabel.setText("Welcome " + user.getUsername());
            photoBytes = user.getPhoto();
        }

        Image image;
        if (photoBytes != null && photoBytes.length > 0) {
            image = new Image(new ByteArrayInputStream(photoBytes));
        } else {
            image = new Image(getClass().getResource("/JavaFXexample/images/blankprofilepicture.jpg").toExternalForm());
        }

        profilePhoto.setImage(image);
    }

    @FXML
    private void logout(ActionEvent event){
        Session.clear();
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/login.fxml", "/JavaFXexample/css/style.css", false);
    }

    @FXML
    private void studentTable(ActionEvent event){        
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/student.fxml", "/JavaFXexample/css/style.css",true);
    }

    @FXML
    private void profileSettings(ActionEvent event){
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/profile-settings.fxml", "/JavaFXexample/css/style.css", false);
    }
}
