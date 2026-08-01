package JavaFXexample.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty gpa = new SimpleDoubleProperty();
    private final StringProperty department = new SimpleStringProperty();

    public Student(int id, String name, double gpa, String department) {
        this.id.set(id);
        this.name.set(name);
        this.gpa.set(gpa);
        this.department.set(department);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty gpaProperty() {
        return gpa;
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public double getGpa() {
        return gpa.get();
    }

    public String getDepartment() {
        return department.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public void setName(String value) {
        name.set(value);
    }

    public void setGpa(double value) {
        gpa.set(value);
    }

    public void setDepartment(String value) {
        department.set(value);
    }

}
