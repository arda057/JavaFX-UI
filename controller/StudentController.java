package JavaFXexample.controller;

import JavaFXexample.model.DialogMode;
import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.SceneManager;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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

                    SceneManager.dialogLoader(student, DialogMode.EDIT, "/JavaFXexample/fxml/student-dialog.fxml", StudentController.this::loadStudents);

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

        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit Student");
            MenuItem deleteItem = new MenuItem("Delete Student");
            MenuItem detailItem = new MenuItem("Student Details");

            menu.getItems().addAll(
                    editItem,
                    deleteItem,
                    detailItem);

            editItem.setOnAction(event -> {
                Student student = row.getItem();

                if (student == null) {
                    return;
                }

               SceneManager.dialogLoader(student, DialogMode.EDIT, "/JavaFXexample/fxml/student-dialog.fxml", this::loadStudents);

            });

            row.setContextMenu(menu);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu));

            return row;
        });

    }

    @FXML
    private void addStudent() {
        SceneManager.dialogLoader(null, DialogMode.ADD, "/JavaFXexample/fxml/student-dialog.fxml", this::loadStudents);

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

    }

    public void loadStudents(){
        students.setAll(
            studentService.getStudents()
        );
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.clear();
    }

    @FXML
    private void clearForm() {

        clearSelection();
        clearFields();

        idField.requestFocus();

    }

    private void clearSelection() {
        studentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void returnDashboard(ActionEvent event){
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/dashboard.fxml", "/JavaFXexample/css/style.css",true);
    }
}
