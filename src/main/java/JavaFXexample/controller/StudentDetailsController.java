package JavaFXexample.controller;

import java.io.ByteArrayInputStream;

import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.AsyncTask;
import JavaFXexample.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StudentDetailsController {

    private Student student;
    private Runnable onUpdateCallback;
    private final StudentService studentService = new StudentService();

    @FXML
    private ImageView photo;
    @FXML
    private Label studentnameField;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label gpaLabel;
    @FXML
    private Label deptDetailLabel;
    @FXML
    private Label advisorLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button editButton;

    public void setStudent(Student student) {
        this.student = student;
        refreshData();
    }

    public void setOnUpdateCallback(Runnable onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
    }

    public Student getStudent() {
        return student;
    }

    public void refreshData() {
        if (student == null) return;

        loadPhoto();

        studentnameField.setText(student.getName() != null ? student.getName() : "N/A");
        departmentLabel.setText(student.getDepartment() != null ? student.getDepartment() : "N/A");
        idLabel.setText(String.valueOf(student.getId()));
        gpaLabel.setText(String.format("%.2f", student.getGpa()));
        deptDetailLabel.setText(student.getDepartment() != null && !student.getDepartment().isBlank() ? student.getDepartment() : "N/A");
        advisorLabel.setText(student.getAdvisor() != null && !student.getAdvisor().isBlank() ? student.getAdvisor() : "N/A");
        phoneLabel.setText(student.getPhone() != null && !student.getPhone().isBlank() ? student.getPhone() : "N/A");
        addressLabel.setText(student.getAddress() != null && !student.getAddress().isBlank() ? student.getAddress() : "N/A");
        emailLabel.setText(student.getEmail() != null && !student.getEmail().isBlank() ? student.getEmail() : "N/A");
    }

    private void loadPhoto() {
        if (student == null) {
            return;
        }

        byte[] photoBytes = student.getPhoto();

        Image image;
        if (photoBytes != null && photoBytes.length > 0) {
            image = new Image(new ByteArrayInputStream(photoBytes));
        } else {
            image = new Image(getClass().getResource("/JavaFXexample/images/blankprofilepicture.jpg").toExternalForm());
        }

        photo.setImage(image);

        AsyncTask.run(
                () -> studentService.getStudentPhoto(student.getId()),
                loadedPhoto -> {
                    student.setPhoto(loadedPhoto);
                    Image resolved;
                    if (loadedPhoto != null && loadedPhoto.length > 0) {
                        resolved = new Image(new ByteArrayInputStream(loadedPhoto));
                    } else {
                        resolved = new Image(getClass()
                                .getResource("/JavaFXexample/images/blankprofilepicture.jpg")
                                .toExternalForm());
                    }
                    photo.setImage(resolved);
                },
                error -> AlertHelper.showException("Photo Error", "Could not load student photo", error));
    }

    @FXML
    private void editStudent() {
        if (student == null) return;

        SceneManager.editStudentLoader(
            student,
            "/JavaFXexample/fxml/student-edit.fxml",
            "/JavaFXexample/css/dialog-style.css",
            () -> {
                refreshData();
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
            }
        );
    }
}
