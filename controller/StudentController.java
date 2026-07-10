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
import javafx.scene.control.TableCell;
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
    private TableColumn<Student,Void> actionColumn;
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

        gpaColumn.setCellFactory(column -> new TableCell<Student, Double>() {
            @Override
            protected void updateItem(Double gpa, boolean empty) {

                super.updateItem(gpa, empty);

                if (empty || gpa == null) {

                    setText(null);
                    setStyle("");

                    return;
                }

                setText(String.format("%.2f", gpa));

                if (gpa >= 3.5) {

                    setStyle("-fx-text-fill: green;");

                } else if (gpa >= 2.5) {

                    setStyle("-fx-text-fill: orange;");

                } else {

                    setStyle("-fx-text-fill: red;");
                }
            }
        });

        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        actionColumn.setCellFactory(column -> new TableCell<>() {

            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {

                    Student student = getTableView()
                            .getItems()
                            .get(getIndex());

                    System.out.println(student.getName());

                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {

                    setGraphic(null);

                } else {

                    setGraphic(editButton);
                }
            }

        });

        students.setAll(
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

        studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldStudent, newStudent) -> {
            if(newStudent == null){
                clearFields();
                return;
            }

            idField.setText(String.valueOf(newStudent.getId()));
            nameField.setText(newStudent.getName());
            gpaField.setText(String.valueOf(newStudent.getGpa()));
            departmentField.setText(newStudent.getDepartment());
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
    private void clearForm() {

        clearSelection();
        clearFields();

        idField.requestFocus();

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

        clearFields();

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

        clearFields();

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

            clearFields();
        }

    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.clear();
    }

    private void clearSelection() {
        studentTable.getSelectionModel().clearSelection();
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
