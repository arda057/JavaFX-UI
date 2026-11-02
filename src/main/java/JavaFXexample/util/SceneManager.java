package JavaFXexample.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JavaFXexample.controller.CourseScheduleController;
import JavaFXexample.controller.StudentAddController;
import JavaFXexample.controller.StudentDetailsController;
import JavaFXexample.controller.StudentEditController;
import JavaFXexample.model.Student;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    public static void FXMLloader(ActionEvent event, String fxmlPath, String cssPath, boolean resizable) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent currentRoot = stage.getScene().getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                try {
                    double oldWidth = stage.getWidth();
                    double oldHeight = stage.getHeight();
                    double oldX = stage.getX();
                    double oldY = stage.getY();

                    FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
                    Parent newRoot = loader.load();

                    Scene scene = new Scene(newRoot);
                    scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

                    newRoot.setOpacity(0.0);

                    stage.setScene(scene);
                    stage.setResizable(resizable);
                    stage.sizeToScene();

                    double newWidth = stage.getWidth();
                    double newHeight = stage.getHeight();

                    double newX = oldX - (newWidth - oldWidth) / 2;
                    double newY = oldY - (newHeight - oldHeight) / 2;

                    stage.setX(newX);
                    stage.setY(newY);
                    stage.show();

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(250), newRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();

                } catch (Exception ex) {
                    log.error("Failed to load scene {}", fxmlPath, ex);
                }
            });

            fadeOut.play();

        } catch (Exception e) {
            log.error("Failed to start scene transition to {}", fxmlPath, e);
        }
    }

    public static void addStudentLoader(String fxmlPath, String cssPath, Runnable onSave) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            StudentAddController controller = loader.getController();
            controller.setOnSaveCallback(onSave);

            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

            dialogStage.setScene(scene);
            dialogStage.setTitle("Add Student");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            log.error("Failed to open add-student dialog", e);
        }
    }

    public static void editStudentLoader(Student student, String fxmlPath, String cssPath, Runnable onSave) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            StudentEditController controller = loader.getController();
            if (student != null) {
                controller.setStudent(student);
            }
            controller.setOnSaveCallback(onSave);

            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

            dialogStage.setScene(scene);
            dialogStage.setTitle("Edit Student");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            log.error("Failed to open edit-student dialog", e);
        }
    }

    public static void detailsLoader(Student student, String fxmlPath, String cssPath, Runnable onUpdate) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            StudentDetailsController controller = loader.getController();

            if (student != null) {
                controller.setStudent(student);
            }

            controller.setOnUpdateCallback(onUpdate);

            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

            dialogStage.setScene(scene);
            dialogStage.setTitle("Student Details");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            log.error("Failed to open student-details dialog", e);
        }
    }

    public static void detailsLoader(Student student, String fxmlPath, String cssPath) {
        detailsLoader(student, fxmlPath, cssPath, null);
    }

    public static void courseScheduleLoader(Student student, String fxmlPath, String cssPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            CourseScheduleController controller = loader.getController();
            if (student != null) {
                controller.setStudent(student);
            }

            Stage dialogStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());

            dialogStage.setScene(scene);
            dialogStage.setTitle("Course Schedule" + (student != null && student.getName() != null ? " - " + student.getName() : ""));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(true);
            dialogStage.showAndWait();

        } catch (Exception e) {
            log.error("Failed to open course-schedule dialog", e);
        }
    }
}
