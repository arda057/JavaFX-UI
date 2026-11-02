package JavaFXexample.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JavaFXexample.database.DatabaseManager;
import JavaFXexample.exception.DataAccessException;
import JavaFXexample.model.CurriculumCourse;

public class CurriculumRepository {
    private static final Logger log = LoggerFactory.getLogger(CurriculumRepository.class);

    public List<CurriculumCourse> getCurriculumByDepartment(String department) {
        String sql = """
                SELECT id, department, academic_year, semester, course_code, course_name, credits, ects, course_type
                FROM curriculum
                WHERE department = ?
                ORDER BY academic_year ASC, semester ASC, course_code ASC
                """;
        List<CurriculumCourse> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new CurriculumCourse(
                            rs.getInt("id"),
                            rs.getString("department"),
                            rs.getInt("academic_year"),
                            rs.getInt("semester"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("ects"),
                            rs.getString("course_type")
                    ));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to load curriculum for department {}", department, e);
            throw new DataAccessException("Could not load curriculum data.", e);
        }
        return list;
    }

    public void insertCourse(CurriculumCourse course) {
        String sql = """
                INSERT INTO curriculum (department, academic_year, semester, course_code, course_name, credits, ects, course_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getDepartment());
            pstmt.setInt(2, course.getAcademicYear());
            pstmt.setInt(3, course.getSemester());
            pstmt.setString(4, course.getCourseCode());
            pstmt.setString(5, course.getCourseName());
            pstmt.setInt(6, course.getCredits());
            pstmt.setInt(7, course.getEcts());
            pstmt.setString(8, course.getCourseType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to insert curriculum course", e);
            throw new DataAccessException("Could not add curriculum course.", e);
        }
    }

    public void updateCourse(CurriculumCourse course) {
        String sql = """
                UPDATE curriculum
                SET department = ?, academic_year = ?, semester = ?, course_code = ?, course_name = ?,
                    credits = ?, ects = ?, course_type = ?
                WHERE id = ?
                """;
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getDepartment());
            pstmt.setInt(2, course.getAcademicYear());
            pstmt.setInt(3, course.getSemester());
            pstmt.setString(4, course.getCourseCode());
            pstmt.setString(5, course.getCourseName());
            pstmt.setInt(6, course.getCredits());
            pstmt.setInt(7, course.getEcts());
            pstmt.setString(8, course.getCourseType());
            pstmt.setInt(9, course.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update curriculum course id={}", course.getId(), e);
            throw new DataAccessException("Could not update curriculum course.", e);
        }
    }

    public void deleteCourse(int id) {
        String sql = "DELETE FROM curriculum WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete curriculum course id={}", id, e);
            throw new DataAccessException("Could not delete curriculum course.", e);
        }
    }
}
