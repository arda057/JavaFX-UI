package JavaFXexample.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CurriculumCourse {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty department = new SimpleStringProperty();
    private final IntegerProperty academicYear = new SimpleIntegerProperty();
    private final IntegerProperty semester = new SimpleIntegerProperty();
    private final StringProperty courseCode = new SimpleStringProperty();
    private final StringProperty courseName = new SimpleStringProperty();
    private final IntegerProperty credits = new SimpleIntegerProperty();
    private final IntegerProperty ects = new SimpleIntegerProperty();
    private final StringProperty courseType = new SimpleStringProperty();

    public CurriculumCourse(int id, String department, int academicYear, int semester, 
                            String courseCode, String courseName, int credits, int ects, String courseType) {
        this.id.set(id);
        this.department.set(department);
        this.academicYear.set(academicYear);
        this.semester.set(semester);
        this.courseCode.set(courseCode);
        this.courseName.set(courseName);
        this.credits.set(credits);
        this.ects.set(ects);
        this.courseType.set(courseType != null ? courseType : "Compulsory");
    }

    public CurriculumCourse(String department, int academicYear, int semester, 
                            String courseCode, String courseName, int credits, int ects, String courseType) {
        this(0, department, academicYear, semester, courseCode, courseName, credits, ects, courseType);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public IntegerProperty academicYearProperty() {
        return academicYear;
    }

    public IntegerProperty semesterProperty() {
        return semester;
    }

    public StringProperty courseCodeProperty() {
        return courseCode;
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }

    public IntegerProperty creditsProperty() {
        return credits;
    }

    public IntegerProperty ectsProperty() {
        return ects;
    }

    public StringProperty courseTypeProperty() {
        return courseType;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String value) {
        department.set(value);
    }

    public int getAcademicYear() {
        return academicYear.get();
    }

    public void setAcademicYear(int value) {
        academicYear.set(value);
    }

    public int getSemester() {
        return semester.get();
    }

    public void setSemester(int value) {
        semester.set(value);
    }

    public String getCourseCode() {
        return courseCode.get();
    }

    public void setCourseCode(String value) {
        courseCode.set(value);
    }

    public String getCourseName() {
        return courseName.get();
    }

    public void setCourseName(String value) {
        courseName.set(value);
    }

    public int getCredits() {
        return credits.get();
    }

    public void setCredits(int value) {
        credits.set(value);
    }

    public int getEcts() {
        return ects.get();
    }

    public void setEcts(int value) {
        ects.set(value);
    }

    public String getCourseType() {
        return courseType.get();
    }

    public void setCourseType(String value) {
        courseType.set(value);
    }
}
