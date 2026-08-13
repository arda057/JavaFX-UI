package JavaFXexample.controller;

import java.io.ByteArrayInputStream;

import JavaFXexample.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StudentDetailsController {

    private Student student;

    @FXML
    private ImageView photo;
    @FXML
    private Label studentnameField;

    public void setStudent(Student student) {
        this.student = student;
        loadPhoto();

        studentnameField.setText(student.getName());
    }

    public Student getStudent() {
        return student;
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
    }

}
