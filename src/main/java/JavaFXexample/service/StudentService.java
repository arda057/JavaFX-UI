package JavaFXexample.service;

import java.util.List;

import JavaFXexample.model.Student;
import JavaFXexample.repository.StudentRepository;

public class StudentService {
    private final StudentRepository repository = new StudentRepository();

    public boolean addStudent(Student student){
        return repository.insertStudent(student);
    }

    public boolean updateStudent(int id, Student student){
        return repository.updateStudent(id, student);
    }

    public void deleteStudent(int id){
        repository.deleteStudent(id);
    }

    public List<Student> getStudents(){
        return repository.getStudents();
    }

    public boolean updateStudentPhoto(int id, byte[] photo){
        return repository.updatePhoto(id, photo);
    }
}
