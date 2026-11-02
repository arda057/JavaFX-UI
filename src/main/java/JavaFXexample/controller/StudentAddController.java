package JavaFXexample.controller;

import JavaFXexample.exception.DataAccessException;
import JavaFXexample.exception.DuplicateEntityException;
import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.AsyncTask;
import JavaFXexample.util.Validator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StudentAddController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField gpaField;
    @FXML
    private TextField departmentField;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Runnable onSaveCallback;
    private final StudentService studentService = new StudentService();
    private final BooleanProperty saving = new SimpleBooleanProperty(false);

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    @FXML
    private void initialize() {
        saveButton.disableProperty().bind(saving);
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
                "Please fill all fields (ID, Name, GPA, Department)");
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

        Student newStudent = new Student(id, nameField.getText(), gpa, departmentField.getText(), null);

        AsyncTask.run(
                () -> studentService.addStudent(newStudent),
                () -> {
                    AlertHelper.showInfo(
                            "Success",
                            "Student Added",
                            "The student has been added successfully.");

                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }

                    Stage stage = (Stage) saveButton.getScene().getWindow();
                    stage.close();
                },
                this::handleSaveError,
                saving);
    }

    private void handleSaveError(Throwable error) {
        if (error instanceof DuplicateEntityException) {
            AlertHelper.showException("Error", "This ID is in use.", error);
        } else if (error instanceof DataAccessException) {
            AlertHelper.showException("Error", "Student could not be added", error);
        } else {
            AlertHelper.showException("Error", "Student could not be added", error);
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
