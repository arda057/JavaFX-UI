package JavaFXexample;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController 
{
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label resultLabel;
    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private void showUsername(){
        System.out.println(usernameField.getText());
    }

    @FXML
    private void login(){

        if((usernameField.getText().equals("admin")) && (passwordField.getText().equals("1234")))
        {
            resultLabel.setText("Login successful");
            resultLabel.getStyleClass().setAll("succsess");
            
            if(rememberCheckBox.isSelected())
            {
                System.out.println("Remember user enabled");
            }
        }
        else
        {
            resultLabel.setText("Wrong username or password");
            resultLabel.getStyleClass().setAll("error");
        }
    }
}
