package JavaFXexample.controller;

import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.Validator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private ObservableList<Student> students = FXCollections.observableArrayList();
    
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

        studentTable.setItems(students);

        loadStudents();

        studentTable.setOnMouseClicked(event ->
        {
            if(event.getClickCount() == 1)
            {
                Student selected = studentTable.getSelectionModel().getSelectedItem();

                if(selected != null)
                {
                    System.out.println("Student: " + selected.getName());
                    System.out.println("Department: " + selected.getDepartment());

                    idField.setText(String.valueOf(selected.getId()));
                    nameField.setText(selected.getName());
                    gpaField.setText(String.valueOf(selected.getGpa()));
                    departmentField.setText(selected.getDepartment());
                }
            }
        });
    }

    @FXML
    private void addStudent() {

        if (Validator.isEmpty(idField.getText())
                || Validator.isEmpty(gpaField.getText())
                || Validator.isEmpty(nameField.getText())
                || Validator.isEmpty(departmentField.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setTitle("Warning");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill all fields");

            alert.showAndWait();
            return;
        }

        if (!Validator.isInteger(idField.getText())) {
            System.out.println("ID must be an integer.");
            return;
        }

        if (!Validator.isDouble(gpaField.getText())) {
            System.out.println("GPA must be numeric.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        Double gpa = Double.parseDouble(gpaField.getText());

        if (!Validator.isValidGPA(gpa)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid GPA");
            alert.setContentText("GPA must be between 0 and 4.");

            alert.showAndWait();
            return;
        }

        String name = nameField.getText();
        String department = departmentField.getText();

        Student student = new Student(id, name, gpa, department);

        studentService.addStudent(student);

        loadStudents();

        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.clear();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Success");
        alert.setHeaderText("Student Added");
        alert.setContentText("The student has been added successfully.");

        alert.showAndWait();
    }

    @FXML
    private void deleteStudent(){
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if(selected == null)
        {
            return;
        }

        if (Validator.isEmpty(idField.getText())){
            System.out.println("Please fill in the id field.");
            return;
        }

        if (!Validator.isInteger(idField.getText())) {
            System.out.println("ID must be an integer.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        
        studentService.deleteStudent(id);
        
        loadStudents();

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
            System.out.println("Please fill all fields.");
            return;
        }

        if (!Validator.isInteger(idField.getText())) {
            System.out.println("ID must be an integer.");
            return;
        }

        if (!Validator.isDouble(gpaField.getText())) {
            System.out.println("GPA must be numeric.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        Double gpa = Double.parseDouble(gpaField.getText());

        if (!Validator.isValidGPA(gpa)) {
            System.out.println("GPA must be between 0 and 4.");
            return;
        }

        String name = nameField.getText();
        String department = departmentField.getText();

        Student updatedStudent = new Student(id, name, gpa, department);

        studentService.updateStudent(updatedStudent);

        loadStudents();

        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.clear();

    }

    private void loadStudents(){
        students.clear();
        students.addAll(studentService.getStudents());
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
