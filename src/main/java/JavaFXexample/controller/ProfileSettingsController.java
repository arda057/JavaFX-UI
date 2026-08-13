package JavaFXexample.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import JavaFXexample.service.UserService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.SceneManager;
import JavaFXexample.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

public class ProfileSettingsController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField idField;
    @FXML
    private Circle profileCircle;

    UserService userService = new UserService();

    @FXML
    private void initialize(){
        usernameField.setText(Session.getCurrentUser().getUsername());
        passwordField.setText("0000000000");
        idField.setText(String.valueOf(Session.getCurrentUser().getId()));

        loadPhoto();

    }

    @FXML
    private void returnDashboard(ActionEvent event){
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/dashboard.fxml", "/JavaFXexample/css/style.css",true);
    }

    @FXML
    private void changePhoto(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                byte[] photoBytes = Files.readAllBytes(file.toPath());

                userService.updatePhoto(Session.getCurrentUser().getId(), photoBytes);
                Session.getCurrentUser().setPhoto(photoBytes);

                loadPhoto();

            } catch(IOException e){
                AlertHelper.showError("Photo Error", "Could not load the selected photo.", "Please try again.");
            }
        }
    }

    @FXML
    private void deletePhoto() {
        if(Session.getCurrentUser().getPhoto() == null){
            return;
        }
        
        try {
            AlertHelper.showConfirmation("Delete Photo", "Are you sure?", "This action is irreversible.");
            userService.deletePhoto(Session.getCurrentUser().getId());
            Session.getCurrentUser().setPhoto(null);
            loadPhoto();
        } catch (Exception e) {
            AlertHelper.showError("Photo Error", "Could not delete the photo.", "Please try again.");
        }
    }

    private void loadPhoto(){
        byte[] photoBytes = Session.getCurrentUser().getPhoto();

        Image image;
        if (photoBytes != null) {
            image = new Image(new ByteArrayInputStream(photoBytes));
        } else {
            image = new Image(getClass().getResource("/JavaFXexample/images/blankprofilepicture.jpg").toExternalForm());
        }

        profileCircle.setFill(new ImagePattern(image));
    }
}
