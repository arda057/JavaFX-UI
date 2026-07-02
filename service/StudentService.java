package JavaFXexample.service;

import java.util.List;

import JavaFXexample.model.Student;
import JavaFXexample.repistory.StudentRepository;

public class StudentService {
    private final StudentRepository repository = new StudentRepository();

    public void addStudent(Student student){
        repository.insertStudent(student);
    }

    public void updateStudent(Student student){
        repository.updateStudent(student);
    }

    public void deleteStudent(int id){
        repository.deleteStudent(id);
    }

    public List<Student> getStudents(){
        return repository.getStudents();
    }
}
