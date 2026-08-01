package JavaFXexample.util;

import JavaFXexample.controller.StudentDialogController;
import JavaFXexample.model.DialogMode;
import JavaFXexample.model.Student;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    public static void FXMLloader(ActionEvent event, String fxmlPath, String cssPath, boolean resizable) {
    try {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent currentRoot = stage.getScene().getRoot();

        // 1. Mevcut ekranı yavaşça saydamlaştır (Fade Out)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            try {
                double oldWidth = stage.getWidth();
                double oldHeight = stage.getHeight();
                double oldX = stage.getX();
                double oldY = stage.getY();

                // 2. Yeni FXML ve CSS'i yükle
                FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                Parent newRoot = loader.load();

                Scene scene = new Scene(newRoot);
                scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

                // 3. Yeni sayfayı başlangıçta tamamen saydam yap
                newRoot.setOpacity(0.0);

                // 4. Sahneyi değiştir, boyutlandır ve ortala
                stage.setScene(scene);
                stage.setResizable(resizable);
                stage.sizeToScene();     // Yeni FXML boyutuna uyarla

                double newWidth = stage.getWidth();
                double newHeight = stage.getHeight();

                double newX = oldX - (newWidth - oldWidth) / 2;
                double newY = oldY - (newHeight - oldHeight) / 2;

                stage.setX(newX);
                stage.setY(newY);
                stage.show();

                // 5. Yeni ekranı yavaşça görünür yap (Fade In)
                FadeTransition fadeIn = new FadeTransition(Duration.millis(250), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fadeOut.play();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void dialogLoader(Student student, DialogMode mode, String fxmlPath, String cssPath, Runnable onSave) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));

            Parent root = loader.load();

            StudentDialogController controller = loader.getController();

            controller.setMode(mode);
            
            if(student != null){
                controller.setStudent(student);
            }

            controller.setOnSaveCallback(onSave);

            Stage dialogStage = new Stage();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

            dialogStage.setScene(scene);
            if(mode == DialogMode.EDIT){
                dialogStage.setTitle("Edit Student");
            }else{
                dialogStage.setTitle("Add Student");
            }

            dialogStage.setResizable(false);

            dialogStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}