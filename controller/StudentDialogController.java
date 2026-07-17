package JavaFXexample.controller;

import JavaFXexample.model.DialogMode;
import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StudentDialogController {
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
    @FXML
    private Label dialogLabel;

    private Runnable onSaveCallback;

    private final StudentService studentService = new StudentService();

    private Student student;

    private DialogMode mode;

    public void setStudent(Student student){
        this.student = student;

        idField.setText(String.valueOf(student.getId()));
        nameField.setText(student.getName());
        gpaField.setText(String.valueOf(student.getGpa()));
        departmentField.setText(student.getDepartment());
    }

    public Student getStudent(){
        return student;
    }

    @FXML
    private void saveStudent(){
        if (Validator.isEmpty(idField.getText())
                || Validator.isEmpty(gpaField.getText())
                || Validator.isEmpty(nameField.getText())
                || Validator.isEmpty(departmentField.getText())) {

            AlertHelper.showWarning(
                "Warning", 
                "Missing Information", 
                "Please fill all fields");

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

        Student updatedStudent = new Student(id, name, gpa, department);

        if (mode == DialogMode.ADD) {
            boolean inserted = studentService.addStudent(updatedStudent);

            if (!inserted) {
                AlertHelper.showError("Error", "This ID is in use.", "Student could not be added");
                return;
            }

            AlertHelper.showInfo(
                    "Success",
                    "Student Added",
                    "The student has been added successfully.");
        } else {

            if (AlertHelper.showConfirmation("Update Student", "Update Confirmation", "Are you sure?")) {
                boolean updated = studentService.updateStudent(student.getId(), updatedStudent);

                if (!updated) {
                    AlertHelper.showError("Error", "This ID is in use.", "Student could not be added");
                    return;
                }

                student.setId(id);
                student.setName(name);
                student.setDepartment(department);
                student.setGpa(gpa);

                AlertHelper.showInfo(
                    "Success",
                    "Student Updated",
                    "The student has been updated successfully.");

            }
        }

        if (onSaveCallback != null){
            onSaveCallback.run();
        }

        Stage stage = (Stage) saveButton.getScene().getWindow();

        stage.close();
    }

    @FXML
    private void closeDialog(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();

        stage.close();
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    public void setMode(DialogMode mode){
        this.mode = mode;

        if (mode == DialogMode.ADD){
            dialogLabel.setText("Add Student");
        } else{
            dialogLabel.setText("Edit Student");
        }
    }
}
