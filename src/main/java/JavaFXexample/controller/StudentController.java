package JavaFXexample.controller;

import JavaFXexample.model.Student;
import JavaFXexample.service.StudentService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.AsyncTask;
import JavaFXexample.util.SceneManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
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
    private TableColumn<Student, Integer> idColumn;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, Double> gpaColumn;
    @FXML
    private TableColumn<Student, String> departmentColumn;
    @FXML
    private TableColumn<Student, Void> actionColumn;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField gpaField;
    @FXML
    private ComboBox<String> departmentField;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ProgressIndicator loadingIndicator;

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredStudents = new FilteredList<>(students, p -> true);
    private final SortedList<Student> sortedStudents = new SortedList<>(filteredStudents);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
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
                    Student student = getTableView().getItems().get(getIndex());
                    SceneManager.editStudentLoader(
                        student, 
                        "/JavaFXexample/fxml/student-edit.fxml",
                        "/JavaFXexample/css/dialog-style.css", 
                        StudentController.this::loadStudents
                    );
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

        students.clear();
        studentTable.getColumns().forEach(column -> column.setReorderable(false));

        if (loadingIndicator != null) {
            loadingIndicator.visibleProperty().bind(loading);
            loadingIndicator.managedProperty().bind(loading);
        }

        idField.textProperty().addListener((obs, oldValue, newValue) -> updateFilter());
        gpaField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        nameField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        departmentField.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        departmentField.getEditor().textProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        sortedStudents.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedStudents);

        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();

            ContextMenu menu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit Student");
            MenuItem deleteItem = new MenuItem("Delete Student");
            MenuItem detailItem = new MenuItem("Student Details");
            MenuItem scheduleItem = new MenuItem("Course Schedule");

            menu.getItems().addAll(editItem, deleteItem, detailItem, scheduleItem);

            editItem.setOnAction(event -> {
                Student student = row.getItem();
                if (student == null) return;

                SceneManager.editStudentLoader(
                    student, 
                    "/JavaFXexample/fxml/student-edit.fxml",
                    "/JavaFXexample/css/dialog-style.css", 
                    this::loadStudents
                );
            });

            deleteItem.setOnAction(event -> {
                Student student = row.getItem();
                if (student == null) return;

                if (AlertHelper.showConfirmation(
                        "Delete Student",
                        "Delete Confirmation",
                        "Are you sure you want to delete " + student.getName() + "?\nThis action cannot be undone.")) {
                    deleteStudentById(student.getId(), true);
                }
            });

            detailItem.setOnAction(event -> {
                Student student = row.getItem();
                if (student == null) return;

                SceneManager.detailsLoader(
                    student, 
                    "/JavaFXexample/fxml/student-details.fxml", 
                    "/JavaFXexample/css/dialog-style.css", 
                    this::loadStudents
                );
            });

            scheduleItem.setOnAction(event -> {
                Student student = row.getItem();
                if (student == null) return;

                SceneManager.courseScheduleLoader(
                    student,
                    "/JavaFXexample/fxml/course-schedule.fxml",
                    "/JavaFXexample/css/dialog-style.css"
                );
            });

            row.setContextMenu(menu);
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu)
            );

            return row;
        });

        deleteButton.disableProperty().bind(
            studentTable.getSelectionModel().selectedItemProperty().isNull()
        );

        departmentField.getItems().addAll(JavaFXexample.util.DepartmentConstants.DEPARTMENTS);

        loadStudents();
    }

    @FXML
    private void addStudent() {
        SceneManager.addStudentLoader(
            "/JavaFXexample/fxml/student-add.fxml",
            "/JavaFXexample/css/dialog-style.css", 
            this::loadStudents
        );
    }

    @FXML
    private void deleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (AlertHelper.showConfirmation(
                "Delete Student",
                "Delete Confirmation",
                "Are you sure you want to delete " + selected.getName() + "?\nThis action cannot be undone.")) {
            deleteStudentById(selected.getId(), false);
        }
        clearSelection();
    }

    public void loadStudents() {
        AsyncTask.run(
            (java.util.function.Supplier<java.util.List<Student>>) studentService::getStudents,
                students::setAll,
                error -> AlertHelper.showException("Error", "Could not load students", error),
                loading);
    }

    private void deleteStudentById(int id, boolean reloadList) {
        AsyncTask.run(
                () -> studentService.deleteStudent(id),
                () -> {
                    if (reloadList) {
                        loadStudents();
                    } else {
                        students.removeIf(student -> student.getId() == id);
                    }
                },
                error -> AlertHelper.showException("Error", "Could not delete student", error),
                loading);
    }

    private void updateFilter() {
        String gpaText = gpaField.getText();
        String normalizedGpaText = gpaText == null ? "" : gpaText.trim();
        boolean hasGpaRange = normalizedGpaText.contains("-");
        double[] gpaRange = hasGpaRange ? parseGpaRange(normalizedGpaText) : null;

        filteredStudents.setPredicate(student -> {
            String idText = idField.getText();
            if (idText != null && !idText.isBlank()) {
                if (!String.valueOf(student.getId()).toLowerCase().contains(idText.toLowerCase().trim())) {
                    return false;
                }
            }

            if (!normalizedGpaText.isBlank()) {
                if (hasGpaRange) {
                    if (gpaRange == null) {
                        return false;
                    }
                    double gpa = student.getGpa();
                    if (gpa < gpaRange[0] || gpa > gpaRange[1]) {
                        return false;
                    }
                } else if (!String.valueOf(student.getGpa()).contains(normalizedGpaText)) {
                    return false;
                }
            }

            String nameText = nameField.getText();
            if (nameText != null && !nameText.isBlank()) {
                if (student.getName() == null
                        || !student.getName().toLowerCase().contains(nameText.toLowerCase().trim())) {
                    return false;
                }
            }

            String deptText = departmentField.getEditor().getText();
            if (deptText != null && !deptText.isBlank()) {
                if (student.getDepartment() == null
                        || !student.getDepartment().toLowerCase().contains(deptText.toLowerCase().trim())) {
                    return false;
                }
            }

            return true;
        });
    }

    private double[] parseGpaRange(String text) {
        String[] bounds = text.split("\\s*-\\s*", -1);
        if (bounds.length != 2) {
            return null;
        }

        try {
            double minimum = Double.parseDouble(bounds[0].trim());
            double maximum = Double.parseDouble(bounds[1].trim());
            if (!Double.isFinite(minimum) || !Double.isFinite(maximum) || minimum > maximum) {
                return null;
            }
            return new double[] { minimum, maximum };
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        gpaField.clear();
        departmentField.setValue(null);
        departmentField.getEditor().clear();
    }

    @FXML
    private void clearForm() {
        clearSelection();
        clearFields();
    }

    private void clearSelection() {
        studentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void returnDashboard(ActionEvent event) {
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/dashboard.fxml", "/JavaFXexample/css/style.css", true);
    }
}
