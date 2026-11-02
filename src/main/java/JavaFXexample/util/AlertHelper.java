package JavaFXexample.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertHelper {
    public static void showInfo(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static void showError(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static void showException(String title, String header, Throwable error) {
        String message = error.getMessage() != null ? error.getMessage() : "An unexpected error occurred.";
        showError(title, header, message);
    }
    
    public static void showWarning(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

}
