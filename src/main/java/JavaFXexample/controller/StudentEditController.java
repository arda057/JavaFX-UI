package JavaFXexample.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import JavaFXexample.exception.DuplicateEntityException;
import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.AsyncTask;
import JavaFXexample.util.Validator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StudentEditController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField gpaField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField advisorField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField emailField;

    @FXML
    private ImageView photoPreview;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Runnable onSaveCallback;
    private final StudentService studentService = new StudentService();
    private Student student;
    private byte[] selectedPhotoBytes;
    private boolean photoChangedByUser;
    private final BooleanProperty saving = new SimpleBooleanProperty(false);
    private final BooleanProperty photoReady = new SimpleBooleanProperty(false);

    public void setStudent(Student student) {
        this.student = student;

        idField.setText(String.valueOf(student.getId()));
        nameField.setText(student.getName());
        gpaField.setText(String.valueOf(student.getGpa()));
        departmentField.setText(student.getDepartment());
        advisorField.setText(student.getAdvisor() != null ? student.getAdvisor() : "");
        phoneField.setText(student.getPhone() != null ? student.getPhone() : "");
        addressField.setText(student.getAddress() != null ? student.getAddress() : "");
        emailField.setText(student.getEmail() != null ? student.getEmail() : "");

        selectedPhotoBytes = student.getPhoto();
        photoChangedByUser = false;
        photoReady.set(false);
        updatePhotoPreview();
        loadStoredPhoto();
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    @FXML
    private void initialize() {
        saveButton.disableProperty().bind(saving.or(photoReady.not()));
    }

    private void loadStoredPhoto() {
        if (student == null) {
            return;
        }
        AsyncTask.run(
                () -> studentService.getStudentPhoto(student.getId()),
                photoBytes -> {
                    if (!photoChangedByUser) {
                        selectedPhotoBytes = photoBytes;
                        student.setPhoto(photoBytes);
                        updatePhotoPreview();
                    }
                    photoReady.set(true);
                },
                error -> AlertHelper.showException("Photo Error", "Could not load student photo", error));
    }

    @FXML
    private void selectPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Student Photo");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                selectedPhotoBytes = Files.readAllBytes(file.toPath());
                photoChangedByUser = true;
                photoReady.set(true);
                updatePhotoPreview();
            } catch (IOException e) {
                AlertHelper.showError("Photo Error", "Could not load the selected photo.", "Please try again.");
            }
        }
    }

    @FXML
    private void removePhoto() {
        selectedPhotoBytes = null;
        photoChangedByUser = true;
        photoReady.set(true);
        updatePhotoPreview();
    }

    private void updatePhotoPreview() {
        if (photoPreview == null) return;
        if (selectedPhotoBytes != null && selectedPhotoBytes.length > 0) {
            Image img = new Image(new ByteArrayInputStream(selectedPhotoBytes));
            photoPreview.setImage(img);
        } else {
            Image defaultImg = new Image(getClass().getResource("/JavaFXexample/images/blankprofilepicture.jpg").toExternalForm());
            photoPreview.setImage(defaultImg);
        }
    }

    @FXML
    private void saveStudent() {
        if (Validator.isEmpty(idField.getText())
                || Validator.isEmpty(gpaField.getText())
                || Validator.isEmpty(nameField.getText())
                || Validator.isEmpty(departmentField.getText())) {

            AlertHelper.showWarning(
                "Warning", 
                "Missing Information", 
                "Please fill all required fields (ID, Name, GPA, Department)");
            return;
        }

        if (!Validator.isInteger(idField.getText())) {
            AlertHelper.showWarning("Input Warning", "Invalid ID", "ID must be an integer.");
            return;
        }

        if (!Validator.isDouble(gpaField.getText())) {
            AlertHelper.showWarning("Input Warning", "Invalid GPA", "GPA must be numeric.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        Double gpa = Double.parseDouble(gpaField.getText());

        if (!Validator.isValidGPA(gpa)) {
            AlertHelper.showWarning("Input Warning", "Invalid GPA", "GPA must be between 0 and 4.");
            return;
        }

        String name = nameField.getText();
        String department = departmentField.getText();
        String advisor = advisorField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String email = emailField.getText();

        Student updatedStudent = new Student(id, name, gpa, department, advisor, phone, address, email, selectedPhotoBytes);
        int originalId = student.getId();

        if (AlertHelper.showConfirmation("Update Student", "Update Confirmation", "Are you sure?")) {
            AsyncTask.run(
                    () -> studentService.updateStudent(originalId, updatedStudent),
                    () -> {
                        student.setId(id);
                        student.setName(name);
                        student.setDepartment(department);
                        student.setGpa(gpa);
                        student.setAdvisor(advisor);
                        student.setPhone(phone);
                        student.setAddress(address);
                        student.setEmail(email);
                        student.setPhoto(selectedPhotoBytes);

                        AlertHelper.showInfo(
                            "Success",
                            "Student Updated",
                            "The student has been updated successfully.");

                        if (onSaveCallback != null) {
                            onSaveCallback.run();
                        }

                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        stage.close();
                    },
                    error -> {
                        if (error instanceof DuplicateEntityException) {
                            AlertHelper.showException("Error", "This ID is in use.", error);
                        } else {
                            AlertHelper.showException("Error", "Student could not be updated", error);
                        }
                    },
                    saving);
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
