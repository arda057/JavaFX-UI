package JavaFXexample.controller;

import JavaFXexample.service.UserService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.SceneManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label resultLabel;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button togglePasswordButton;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private TextField confirmVisiblePasswordField;

    UserService userService = new UserService();

    private FadeTransition fadeOut;

    @FXML
    private void initialize() {
        createAccountButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(
                        passwordField.textProperty().isEmpty().or(
                                confirmPasswordField.textProperty().isEmpty())));

        visiblePasswordField.textProperty().bindBidirectional(
                passwordField.textProperty());

        confirmVisiblePasswordField.textProperty().bindBidirectional(
                confirmPasswordField.textProperty());
                
        resultLabel.managedProperty().bind(
            resultLabel.visibleProperty()
        );
    }

    @FXML
    private void togglePasswordVisibility() {

        if (passwordField.getText().isEmpty()) {
            return;
        }

        if (passwordField.isVisible()) {
            passwordField.setVisible(false);

            visiblePasswordField.setVisible(true);

        } else {
            passwordField.setVisible(true);

            visiblePasswordField.setVisible(false);

        }
    }

    @FXML
    private void confirmTogglePasswordVisibility() {

        if (confirmPasswordField.getText().isEmpty()) {
            return;
        }

        if (confirmPasswordField.isVisible()) {
            confirmPasswordField.setVisible(false);

            confirmVisiblePasswordField.setVisible(true);

        } else {
            confirmPasswordField.setVisible(true);

            confirmVisiblePasswordField.setVisible(false);

        }
    }

    @FXML
    private void toLoginPage(ActionEvent event) {
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/login.fxml", "/JavaFXexample/css/style.css", false);
    }

    @FXML
    private void register() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            resultLabel.setText("Passwords do not match");
            resultLabel.getStyleClass().setAll("label", "error");
            resultLabel.setOpacity(1.0);
            resultLabel.setVisible(true);

            if (fadeOut != null) {
                fadeOut.stop();
            }

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), resultLabel);

            fadeOut.setFromValue(0.8);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.seconds(2));

            fadeOut.setOnFinished(delayEvent -> {
                resultLabel.setVisible(false);
                resultLabel.setOpacity(1.0);
            });

            fadeOut.play();
            return;
        }
        boolean success = userService.register(usernameField.getText(), passwordField.getText());

        if(success){
            AlertHelper.showInfo("Success",
                    "User Added",
                    "The user has been added successfully.");
        } else {
            AlertHelper.showError("Error", "This user name already taken.", "User could not be added");
        }
    }

}
