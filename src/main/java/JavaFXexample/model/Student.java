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
    private final StringProperty advisor = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private byte[] photo;

    public Student(int id, String name, double gpa, String department, String advisor, String phone, String address, String email, byte[] photo) {
        this.id.set(id);
        this.name.set(name);
        this.gpa.set(gpa);
        this.department.set(department);
        this.advisor.set(advisor);
        this.phone.set(phone);
        this.address.set(address);
        this.email.set(email);
        this.photo = photo;
    }

    public Student(int id, String name, double gpa, String department, byte[] photo) {
        this(id, name, gpa, department, "", "", "", "", photo);
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

    public StringProperty advisorProperty() {
        return advisor;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty emailProperty() {
        return email;
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

    public String getAdvisor() {
        return advisor.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getAddress() {
        return address.get();
    }

    public String getEmail() {
        return email.get();
    }

    public byte[] getPhoto() {
        return photo;
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

    public void setAdvisor(String value) {
        advisor.set(value);
    }

    public void setPhone(String value) {
        phone.set(value);
    }

    public void setAddress(String value) {
        address.set(value);
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public void setPhoto(byte[] photo){
        this.photo = photo;
    }
}
