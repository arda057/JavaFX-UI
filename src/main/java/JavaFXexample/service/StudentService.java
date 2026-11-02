package JavaFXexample.service;

import java.util.List;

import JavaFXexample.model.Student;
import JavaFXexample.repository.StudentRepository;

public class StudentService {
    private final StudentRepository repository = new StudentRepository();

    public void addStudent(Student student) {
        repository.insertStudent(student);
    }

    public void updateStudent(int id, Student student) {
        repository.updateStudent(id, student);
    }

    public void deleteStudent(int id) {
        repository.deleteStudent(id);
    }

    public List<Student> getStudents() {
        return repository.getStudents();
    }

    public byte[] getStudentPhoto(int id) {
        return repository.getPhoto(id);
    }

    public void updateStudentPhoto(int id, byte[] photo) {
        repository.updatePhoto(id, photo);
    }
}
