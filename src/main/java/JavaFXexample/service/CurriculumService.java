package JavaFXexample.service;

import java.util.List;

import JavaFXexample.model.CurriculumCourse;
import JavaFXexample.repository.CurriculumRepository;

public class CurriculumService {
    private final CurriculumRepository repository = new CurriculumRepository();

    public List<CurriculumCourse> getCurriculumByDepartment(String department) {
        return repository.getCurriculumByDepartment(department);
    }

    public void addCourse(CurriculumCourse course) {
        repository.insertCourse(course);
    }

    public void updateCourse(CurriculumCourse course) {
        repository.updateCourse(course);
    }

    public void deleteCourse(int id) {
        repository.deleteCourse(id);
    }
}
