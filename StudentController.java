package JavaFXexample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    @FXML
    public void initialize()
    {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        students.addAll(
            DatabaseManager.getStudents()
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
    private void addStudent(){
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            Double gpa = Double.parseDouble(gpaField.getText());
            String department = departmentField.getText();
            Student student = new Student(id, name, gpa, department);

            DatabaseManager.insertStudent(student);

            loadStudents();

            idField.clear();
            nameField.clear();
            gpaField.clear();
            departmentField.clear();

        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    @FXML
    private void deleteStudent(){
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if(selected == null)
        {
            return;
        }
        DatabaseManager.deleteStudent(selected.getId());

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

        try {
            Student updatedStudent = new Student(
                Integer.parseInt(idField.getText()),
                nameField.getText(),
                Double.parseDouble(gpaField.getText()),
                departmentField.getText()
            );

            DatabaseManager.updateStudent(updatedStudent);

            loadStudents();

            idField.clear();
            nameField.clear();
            gpaField.clear();
            departmentField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadStudents(){
        students.clear();
        students.addAll(DatabaseManager.getStudents());
    }

    @FXML
    private void returnDashboard(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            Stage stage = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
