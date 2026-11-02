package JavaFXexample.controller;

import JavaFXexample.exception.DataAccessException;
import JavaFXexample.model.User;
import JavaFXexample.service.UserService;
import JavaFXexample.util.AsyncTask;
import JavaFXexample.util.SceneManager;
import JavaFXexample.util.Session;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label resultLabel;
    @FXML
    private CheckBox rememberCheckBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button togglePasswordButton;
    @FXML
    private TextField visiblePasswordField;

    private FadeTransition fadeOut;

    UserService userService = new UserService();
    private final BooleanProperty busy = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(
                        passwordField.textProperty().isEmpty()).or(busy));

        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> login(event));

        visiblePasswordField.textProperty().bindBidirectional(
                passwordField.textProperty());

        rememberCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Node mark = rememberCheckBox.lookup(".mark");

            if (mark != null) {
                if (newValue) {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(180), mark);
                    scale.setFromX(0);
                    scale.setFromY(0);
                    scale.setToX(1.0);
                    scale.setToY(1.0);

                    fadeOut = new FadeTransition(Duration.millis(180), mark);
                    fadeOut.setFromValue(0.0);
                    fadeOut.setToValue(1.0);

                    ParallelTransition anim = new ParallelTransition(scale, fadeOut);
                    anim.play();

                } else {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(120), mark);
                    scale.setToX(0);
                    scale.setToY(0);
                    scale.play();
                }
            }
        });

    }

    @FXML
    private void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        AsyncTask.run(
                () -> userService.login(username, password),
                user -> handleLoginResult(event, user),
                error -> {
                    if (error instanceof DataAccessException) {
                        resultLabel.setText(error.getMessage());
                    } else {
                        resultLabel.setText("Login failed");
                    }
                    showLoginError();
                },
                busy);
    }

    private void handleLoginResult(ActionEvent event, User user) {
        if (user != null) {
            Session.setCurrentUser(user);

            resultLabel.setText("Login successful");
            resultLabel.getStyleClass().setAll("label", "succsess");

            SceneManager.FXMLloader(event, "/JavaFXexample/fxml/dashboard.fxml", "/JavaFXexample/css/style.css", true);

            if (rememberCheckBox.isSelected()) {
                System.out.println("Remember user enabled");
            }
        } else {
            resultLabel.setText("Wrong username or password");
            showLoginError();
        }
    }

    private void showLoginError() {
            resultLabel.getStyleClass().setAll("label", "error");
            resultLabel.setOpacity(1.0);
            resultLabel.setVisible(true);

            if (fadeOut != null) {
                fadeOut.stop();
            }

            fadeOut = new FadeTransition(Duration.seconds(2), resultLabel);

            fadeOut.setFromValue(0.8);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.seconds(2));

            fadeOut.setOnFinished(delayEvent -> {
                resultLabel.setVisible(false);
                resultLabel.setOpacity(1.0);
            });

            fadeOut.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(50), loginButton);

            tt.setFromX(0);
            tt.setByX(10);

            tt.setCycleCount(8);

            tt.setAutoReverse(true);

            tt.setOnFinished(e -> loginButton.setTranslateX(0));

            tt.play();
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
    private void toRegister(ActionEvent event) {
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/register.fxml", "/JavaFXexample/css/style.css", false);
    }
}
