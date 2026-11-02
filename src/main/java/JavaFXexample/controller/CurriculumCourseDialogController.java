package JavaFXexample.controller;

import JavaFXexample.model.CurriculumCourse;
import JavaFXexample.service.CurriculumService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.DepartmentConstants;
import JavaFXexample.util.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CurriculumCourseDialogController {

    @FXML
    private Label dialogTitleLabel;

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private ComboBox<Integer> semesterComboBox;

    @FXML
    private TextField courseCodeField;

    @FXML
    private TextField courseNameField;

    @FXML
    private TextField creditsField;

    @FXML
    private TextField ectsField;

    @FXML
    private ComboBox<String> courseTypeComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final CurriculumService curriculumService = new CurriculumService();
    private CurriculumCourse course;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @FXML
    private void initialize() {
        departmentComboBox.getItems().addAll(DepartmentConstants.DEPARTMENTS);
        yearComboBox.getItems().addAll(1, 2, 3, 4);
        semesterComboBox.getItems().addAll(1, 2);
        courseTypeComboBox.getItems().addAll("Compulsory", "Elective");
        yearComboBox.setValue(1);
        semesterComboBox.setValue(1);
        courseTypeComboBox.setValue("Compulsory");
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    public void setPreselectedDepartment(String department) {
        if (department != null && !department.isBlank()) {
            departmentComboBox.setValue(department);
        }
    }

    public void setCourseForEdit(CurriculumCourse course) {
        this.course = course;
        this.isEditMode = true;
        dialogTitleLabel.setText("Edit Curriculum Course");

        departmentComboBox.setValue(course.getDepartment());
        yearComboBox.setValue(course.getAcademicYear());
        semesterComboBox.setValue(course.getSemester());
        courseCodeField.setText(course.getCourseCode());
        courseNameField.setText(course.getCourseName());
        creditsField.setText(String.valueOf(course.getCredits()));
        ectsField.setText(String.valueOf(course.getEcts()));
        courseTypeComboBox.setValue(course.getCourseType());
    }

    @FXML
    private void saveCourse() {
        String dept = departmentComboBox.getValue();
        Integer year = yearComboBox.getValue();
        Integer sem = semesterComboBox.getValue();
        String code = courseCodeField.getText();
        String name = courseNameField.getText();
        String creditsText = creditsField.getText();
        String ectsText = ectsField.getText();
        String type = courseTypeComboBox.getValue();

        if (dept == null || dept.isBlank() ||
            year == null || sem == null ||
            Validator.isEmpty(code) || Validator.isEmpty(name) ||
            Validator.isEmpty(creditsText) || Validator.isEmpty(ectsText)) {

            AlertHelper.showWarning(
                "Missing Information",
                "Incomplete Course Details",
                "Please fill in all course information fields."
            );
            return;
        }

        if (!Validator.isInteger(creditsText.trim())) {
            AlertHelper.showWarning("Input Warning", "Invalid Credits", "Credits must be a valid integer.");
            return;
        }

        if (!Validator.isInteger(ectsText.trim())) {
            AlertHelper.showWarning("Input Warning", "Invalid ECTS", "ECTS must be a valid integer.");
            return;
        }

        int credits = Integer.parseInt(creditsText.trim());
        int ects = Integer.parseInt(ectsText.trim());
        if (type == null || type.isBlank()) {
            type = "Compulsory";
        }

        try {
            if (isEditMode && course != null) {
                course.setDepartment(dept);
                course.setAcademicYear(year);
                course.setSemester(sem);
                course.setCourseCode(code.trim().toUpperCase());
                course.setCourseName(name.trim());
                course.setCredits(credits);
                course.setEcts(ects);
                course.setCourseType(type);

                curriculumService.updateCourse(course);
                AlertHelper.showInfo("Success", "Course Updated", "Curriculum course updated successfully.");
            } else {
                CurriculumCourse newCourse = new CurriculumCourse(
                    dept, year, sem, code.trim().toUpperCase(), name.trim(), credits, ects, type
                );
                curriculumService.addCourse(newCourse);
                AlertHelper.showInfo("Success", "Course Added", "New course added to curriculum successfully.");
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            closeDialog();

        } catch (Exception ex) {
            AlertHelper.showException("Database Error", "Failed to save curriculum course", ex);
        }
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
