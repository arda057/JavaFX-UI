package JavaFXexample.controller;

import java.util.ArrayList;
import java.util.List;

import JavaFXexample.model.CurriculumCourse;
import JavaFXexample.service.CurriculumService;
import JavaFXexample.util.AlertHelper;
import JavaFXexample.util.AsyncTask;
import JavaFXexample.util.DepartmentConstants;
import JavaFXexample.util.SceneManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class CurriculumController {

    @FXML
    private ComboBox<String> departmentComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label departmentTitleLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label totalEctsLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    // Year 1 Tables
    @FXML private TableView<CurriculumCourse> tableY1S1;
    @FXML private TableView<CurriculumCourse> tableY1S2;
    @FXML private Label labelY1S1Stats;
    @FXML private Label labelY1S2Stats;

    // Year 2 Tables
    @FXML private TableView<CurriculumCourse> tableY2S1;
    @FXML private TableView<CurriculumCourse> tableY2S2;
    @FXML private Label labelY2S1Stats;
    @FXML private Label labelY2S2Stats;

    // Year 3 Tables
    @FXML private TableView<CurriculumCourse> tableY3S1;
    @FXML private TableView<CurriculumCourse> tableY3S2;
    @FXML private Label labelY3S1Stats;
    @FXML private Label labelY3S2Stats;

    // Year 4 Tables
    @FXML private TableView<CurriculumCourse> tableY4S1;
    @FXML private TableView<CurriculumCourse> tableY4S2;
    @FXML private Label labelY4S1Stats;
    @FXML private Label labelY4S2Stats;

    // All Courses Table
    @FXML private TableView<CurriculumCourse> tableAll;

    private final ObservableList<CurriculumCourse> allCourses = FXCollections.observableArrayList();
    private final FilteredList<CurriculumCourse> filteredAllCourses = new FilteredList<>(allCourses, p -> true);

    private final ObservableList<CurriculumCourse> listY1S1 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY1S2 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY2S1 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY2S2 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY3S1 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY3S2 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY4S1 = FXCollections.observableArrayList();
    private final ObservableList<CurriculumCourse> listY4S2 = FXCollections.observableArrayList();

    private final FilteredList<CurriculumCourse> filteredY1S1 = new FilteredList<>(listY1S1, p -> true);
    private final FilteredList<CurriculumCourse> filteredY1S2 = new FilteredList<>(listY1S2, p -> true);
    private final FilteredList<CurriculumCourse> filteredY2S1 = new FilteredList<>(listY2S1, p -> true);
    private final FilteredList<CurriculumCourse> filteredY2S2 = new FilteredList<>(listY2S2, p -> true);
    private final FilteredList<CurriculumCourse> filteredY3S1 = new FilteredList<>(listY3S1, p -> true);
    private final FilteredList<CurriculumCourse> filteredY3S2 = new FilteredList<>(listY3S2, p -> true);
    private final FilteredList<CurriculumCourse> filteredY4S1 = new FilteredList<>(listY4S1, p -> true);
    private final FilteredList<CurriculumCourse> filteredY4S2 = new FilteredList<>(listY4S2, p -> true);

    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final CurriculumService curriculumService = new CurriculumService();

    @FXML
    private void initialize() {
        if (loadingIndicator != null) {
            loadingIndicator.visibleProperty().bind(loading);
            loadingIndicator.managedProperty().bind(loading);
        }

        departmentComboBox.getItems().addAll(DepartmentConstants.DEPARTMENTS);

        setupTable(tableY1S1, filteredY1S1, false);
        setupTable(tableY1S2, filteredY1S2, false);
        setupTable(tableY2S1, filteredY2S1, false);
        setupTable(tableY2S2, filteredY2S2, false);
        setupTable(tableY3S1, filteredY3S1, false);
        setupTable(tableY3S2, filteredY3S2, false);
        setupTable(tableY4S1, filteredY4S1, false);
        setupTable(tableY4S2, filteredY4S2, false);
        setupTable(tableAll, filteredAllCourses, true);

        departmentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBlank()) {
                loadCurriculumForDepartment();
            }
        });

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter(newVal));
        }

        // Default selection
        departmentComboBox.setValue("Computer Science");
    }

    private void setupTable(TableView<CurriculumCourse> table, ObservableList<CurriculumCourse> items, boolean showYearSemColumns) {
        if (table == null) return;

        table.setItems(items);

        if (showYearSemColumns) {
            TableColumn<CurriculumCourse, Integer> yearCol = new TableColumn<>("Year");
            yearCol.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
            yearCol.setPrefWidth(60);

            TableColumn<CurriculumCourse, Integer> semCol = new TableColumn<>("Sem");
            semCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
            semCol.setPrefWidth(60);

            table.getColumns().add(yearCol);
            table.getColumns().add(semCol);
        }

        TableColumn<CurriculumCourse, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        codeCol.setPrefWidth(110);

        TableColumn<CurriculumCourse, String> nameCol = new TableColumn<>("Course Title");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        nameCol.setPrefWidth(260);

        TableColumn<CurriculumCourse, Integer> creditCol = new TableColumn<>("Credits");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditCol.setPrefWidth(70);

        TableColumn<CurriculumCourse, Integer> ectsCol = new TableColumn<>("ECTS");
        ectsCol.setCellValueFactory(new PropertyValueFactory<>("ects"));
        ectsCol.setPrefWidth(70);

        TableColumn<CurriculumCourse, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        typeCol.setPrefWidth(110);

        TableColumn<CurriculumCourse, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(140);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox box = new HBox(6, editBtn, delBtn);

            {
                editBtn.getStyleClass().add("btn-secondary");
                delBtn.getStyleClass().add("btn-danger");
                editBtn.setOnAction(e -> {
                    CurriculumCourse course = getTableView().getItems().get(getIndex());
                    editCourse(course);
                });
                delBtn.setOnAction(e -> {
                    CurriculumCourse course = getTableView().getItems().get(getIndex());
                    deleteCourse(course);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(codeCol, nameCol, creditCol, ectsCol, typeCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<CurriculumCourse> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit Course");
            MenuItem deleteItem = new MenuItem("Delete Course");

            menu.getItems().addAll(editItem, deleteItem);

            editItem.setOnAction(e -> {
                CurriculumCourse course = row.getItem();
                if (course != null) editCourse(course);
            });

            deleteItem.setOnAction(e -> {
                CurriculumCourse course = row.getItem();
                if (course != null) deleteCourse(course);
            });

            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menu)
            );
            return row;
        });
    }

    public void loadCurriculumForDepartment() {
        String dept = departmentComboBox.getValue();
        if (dept == null || dept.isBlank()) return;

        if (departmentTitleLabel != null) {
            departmentTitleLabel.setText(dept + " Curriculum");
        }

        AsyncTask.run(
            () -> curriculumService.getCurriculumByDepartment(dept),
            courses -> {
                allCourses.setAll(courses);
                distributeCourses(courses);
                updateStats(courses);
                applySearchFilter(searchField != null ? searchField.getText() : null);
            },
            error -> AlertHelper.showException("Error", "Could not load curriculum data", error),
            loading
        );
    }

    private void distributeCourses(List<CurriculumCourse> courses) {
        listY1S1.clear(); listY1S2.clear();
        listY2S1.clear(); listY2S2.clear();
        listY3S1.clear(); listY3S2.clear();
        listY4S1.clear(); listY4S2.clear();

        for (CurriculumCourse c : courses) {
            int y = c.getAcademicYear();
            int s = c.getSemester();
            if (y == 1 && s == 1) listY1S1.add(c);
            else if (y == 1 && s == 2) listY1S2.add(c);
            else if (y == 2 && s == 1) listY2S1.add(c);
            else if (y == 2 && s == 2) listY2S2.add(c);
            else if (y == 3 && s == 1) listY3S1.add(c);
            else if (y == 3 && s == 2) listY3S2.add(c);
            else if (y == 4 && s == 1) listY4S1.add(c);
            else if (y == 4 && s == 2) listY4S2.add(c);
        }

        updateSemesterStats(listY1S1, labelY1S1Stats);
        updateSemesterStats(listY1S2, labelY1S2Stats);
        updateSemesterStats(listY2S1, labelY2S1Stats);
        updateSemesterStats(listY2S2, labelY2S2Stats);
        updateSemesterStats(listY3S1, labelY3S1Stats);
        updateSemesterStats(listY3S2, labelY3S2Stats);
        updateSemesterStats(listY4S1, labelY4S1Stats);
        updateSemesterStats(listY4S2, labelY4S2Stats);
    }

    private void updateSemesterStats(List<CurriculumCourse> list, Label label) {
        if (label == null) return;
        int count = list.size();
        int totalCredits = list.stream().mapToInt(CurriculumCourse::getCredits).sum();
        int totalEcts = list.stream().mapToInt(CurriculumCourse::getEcts).sum();
        label.setText(String.format("%d Courses  •  %d Credits  •  %d ECTS", count, totalCredits, totalEcts));
    }

    private void updateStats(List<CurriculumCourse> courses) {
        int count = courses.size();
        int credits = courses.stream().mapToInt(CurriculumCourse::getCredits).sum();
        int ects = courses.stream().mapToInt(CurriculumCourse::getEcts).sum();

        if (totalCoursesLabel != null) totalCoursesLabel.setText("Total Courses: " + count);
        if (totalCreditsLabel != null) totalCreditsLabel.setText("Total Credits: " + credits);
        if (totalEctsLabel != null) totalEctsLabel.setText("Total ECTS: " + ects);
    }

    private void applySearchFilter(String query) {
        String filter = query == null ? "" : query.trim().toLowerCase();

        java.util.function.Predicate<CurriculumCourse> predicate = course -> {
            if (filter.isEmpty()) return true;
            return (course.getCourseCode() != null && course.getCourseCode().toLowerCase().contains(filter))
                || (course.getCourseName() != null && course.getCourseName().toLowerCase().contains(filter))
                || (course.getCourseType() != null && course.getCourseType().toLowerCase().contains(filter));
        };

        filteredAllCourses.setPredicate(predicate);
        filteredY1S1.setPredicate(predicate);
        filteredY1S2.setPredicate(predicate);
        filteredY2S1.setPredicate(predicate);
        filteredY2S2.setPredicate(predicate);
        filteredY3S1.setPredicate(predicate);
        filteredY3S2.setPredicate(predicate);
        filteredY4S1.setPredicate(predicate);
        filteredY4S2.setPredicate(predicate);
    }

    @FXML
    private void addCourse() {
        String currentDept = departmentComboBox.getValue();
        SceneManager.curriculumDialogLoader(null, currentDept, this::loadCurriculumForDepartment);
    }

    private void editCourse(CurriculumCourse course) {
        if (course == null) return;
        SceneManager.curriculumDialogLoader(course, departmentComboBox.getValue(), this::loadCurriculumForDepartment);
    }

    private void deleteCourse(CurriculumCourse course) {
        if (course == null) return;
        if (AlertHelper.showConfirmation(
                "Delete Course",
                "Delete Confirmation",
                "Are you sure you want to delete " + course.getCourseCode() + " (" + course.getCourseName() + ") from the curriculum?")) {

            AsyncTask.run(
                () -> curriculumService.deleteCourse(course.getId()),
                () -> {
                    AlertHelper.showInfo("Deleted", "Course Deleted", "Course was removed from the curriculum.");
                    loadCurriculumForDepartment();
                },
                error -> AlertHelper.showException("Error", "Could not delete course", error),
                loading
            );
        }
    }

    @FXML
    private void returnDashboard(ActionEvent event) {
        SceneManager.FXMLloader(event, "/JavaFXexample/fxml/dashboard.fxml", "/JavaFXexample/css/style.css", true);
    }
}
