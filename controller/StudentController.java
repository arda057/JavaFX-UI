package JavaFXexample.controller;

import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class StudentController {
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student,Integer> idColumn;
    @FXML
    private TableColumn<Student,String> nameColumn;
    @FXML
    private TableColumn<Student,Double> gpaColumn;
    @FXML
    private TableColumn<Student,String> departmentColumn;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField gpaField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;

    private final ObservableList<Student> students = FXCollections.observableArrayList();

    private final FilteredList<Student> filteredStudents = new FilteredList<>(students, p -> true);

    private final SortedList<Student> sortedStudents = new SortedList<>(filteredStudents);
    
    private StudentService studentService = new StudentService();

    @FXML
    public void initialize()
    {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        students.addAll(
            studentService.getStudents()
        );

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    filteredStudents.setPredicate(student -> {
                        if (newValue == null || newValue.isBlank()) {
                            return true;
                        }

                        String keyword = newValue.toLowerCase();

                        return student.getName().toLowerCase().contains(keyword)
                                ||

                                student.getDepartment()
                                        .toLowerCase()
                                        .contains(keyword)

                                ||

                                String.valueOf(student.getId())
                                        .contains(keyword)

                                ||

                                String.valueOf(student.getGpa())
                                        .contains(keyword);
                    });
                });

        sortedStudents.comparatorProperty().bind(
            studentTable.comparatorProperty()
        );
        studentTable.setItems(sortedStudents);

        studentTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Student selected = studentTable.getSelectionModel().getSelectedItem();

                if (selected == null)
                    return;

                System.out.println("Student: " + selected.getName());
                System.out.println("Department: " + selected.getDepartment());

                idField.setText(String.valueOf(selected.getId()));
                nameField.setText(selected.getName());
                gpaField.setText(String.valueOf(selected.getGpa()));
                departmentField.setText(selected.getDepartment());

            }
        });

        addButton.disableProperty().bind(
            idField.textProperty().isEmpty().or(
                nameField.textProperty().isEmpty().or(
                    gpaField.textProperty().isEmpty().or(
                        departmentField.textProperty().isEmpty()
                    )
                )
            )
        );
    }

    @FXML
    private void addStudent() {

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

        Student student = new Student(id, name, gpa, department);

        studentService.addStudent(student);

        students.add(student);

        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.clear();

        AlertHelper.showInfo(
            "Success", 
            "Student Added", 
            "The student has been added successfully.");

    }

    @FXML
    private void deleteStudent(){
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if(selected == null)
        {
            return;
        }
        
        if (AlertHelper.showConfirmation(
                "Delete Student",
                "Delete Confirmation",
                "Are you sure?")) {
            studentService.deleteStudent(selected.getId());
        }
        
        students.remove(selected);

        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.clear();

    }

    @FXML 
    private void updateStudent(){
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            return;
        }

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

        if (AlertHelper.showConfirmation("Update Student", "Update Confirmation", "Are you sure?")) {
            studentService.updateStudent(selected.getId(),updatedStudent);

            selected.setId(id);
            selected.setName(name);
            selected.setDepartment(department);
            selected.setGpa(gpa);

            idField.clear();
            nameField.clear();
            gpaField.clear();
            departmentField.clear();
        }

    }

    @FXML
    private void returnDashboard(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFXexample/fxml/dashboard.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/JavaFXexample/css/style.css").toExternalForm());

            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
