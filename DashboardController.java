package JavaFXexample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController {
    @FXML
    private Label welcomeLabel;

    public void setUsername(String username)
    {
        welcomeLabel.setText("Welcome " + username);
    }

    @FXML
    private void logout(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void studentTable(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
