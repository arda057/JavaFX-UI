package JavaFXexample.controller;

import java.io.ByteArrayInputStream;

import JavaFXexample.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CourseScheduleController {

    private Student student;

    @FXML
    private ImageView photo;

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label studentIdLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label advisorLabel;

    @FXML
    private Label termLabel;

    @FXML
    private Button closeButton;

    public void setStudent(Student student) {
        this.student = student;
        renderStudentInfo();
    }

    public Student getStudent() {
        return student;
    }

    private void renderStudentInfo() {
        if (student == null) {
            return;
        }

        if (studentNameLabel != null) {
            studentNameLabel.setText(student.getName() != null ? student.getName() : "Unknown Student");
        }

        if (studentIdLabel != null) {
            studentIdLabel.setText("ID: " + student.getId());
        }

        if (departmentLabel != null) {
            String dept = student.getDepartment();
            departmentLabel.setText(dept != null && !dept.isBlank() ? dept : "No Department");
        }

        if (advisorLabel != null) {
            String advisor = student.getAdvisor();
            advisorLabel.setText("Advisor: " + (advisor != null && !advisor.isBlank() ? advisor : "Not Assigned"));
        }

        if (termLabel != null) {
            termLabel.setText("Fall 2026-2027 Academic Term");
        }

        loadPhoto();
    }

    private void loadPhoto() {
        if (photo == null || student == null) {
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
    }

    @FXML
    private void closeDialog() {
        if (closeButton != null && closeButton.getScene() != null) {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        }
    }
}
