package JavaFXexample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private void login(ActionEvent event){

        if((usernameField.getText().equals("admin")) && (passwordField.getText().equals("1234")))
        {
            resultLabel.setText("Login successful");
            resultLabel.getStyleClass().setAll("succsess");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));

                Parent root = loader.load();

                DashboardController controller = loader.getController();

                controller.setUsername(usernameField.getText());

                Stage stage =(Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
                
                Scene scene = new Scene(root);

                scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

                stage.setScene(scene);

                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
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
