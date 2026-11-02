package JavaFXexample.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JavaFXexample.database.DatabaseManager;
import JavaFXexample.exception.DataAccessException;
import JavaFXexample.exception.DuplicateEntityException;
import JavaFXexample.model.Student;

public class StudentRepository {
    private static final Logger log = LoggerFactory.getLogger(StudentRepository.class);
    private static final int SQLITE_CONSTRAINT = 19;

    public void insertStudent(Student student) {
        String sql = """
                INSERT INTO students
                (id, name, gpa, department, advisor, phone, address, email, photo)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setDouble(3, student.getGpa());
            pstmt.setString(4, student.getDepartment());
            pstmt.setString(5, student.getAdvisor());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getEmail());
            pstmt.setBytes(9, student.getPhoto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw wrapWriteError("Could not add student.", e);
        }
    }

    public void updateStudent(int id, Student student) {
        String sql = """
                UPDATE students
                SET id = ?,
                    name = ?,
                    gpa = ?,
                    department = ?,
                    advisor = ?,
                    phone = ?,
                    address = ?,
                    email = ?,
                    photo = ?
                WHERE id = ?
                """;

        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setDouble(3, student.getGpa());
            pstmt.setString(4, student.getDepartment());
            pstmt.setString(5, student.getAdvisor());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getEmail());
            pstmt.setBytes(9, student.getPhoto());
            pstmt.setInt(10, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw wrapWriteError("Could not update student.", e);
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete student {}", id, e);
            throw new DataAccessException("Could not delete student.", e);
        }
    }

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        String sql = """
                SELECT id, name, gpa, department, advisor, phone, address, email
                FROM students
                """;

        try (
                Connection conn = DatabaseManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {
            while (rs.next()) {
                students.add(mapStudent(rs, false));
            }
            return students;
        } catch (SQLException e) {
            log.error("Failed to load students", e);
            throw new DataAccessException("Could not load students.", e);
        }
    }

    public byte[] getPhoto(int id) {
        String sql = "SELECT photo FROM students WHERE id = ?";
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("photo");
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("Failed to load photo for student {}", id, e);
            throw new DataAccessException("Could not load student photo.", e);
        }
    }

    public void updatePhoto(int studentId, byte[] photo) {
        String sql = "UPDATE students SET photo = ? WHERE id = ?";
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setBytes(1, photo);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update photo for student {}", studentId, e);
            throw new DataAccessException("Could not update student photo.", e);
        }
    }

    private Student mapStudent(ResultSet rs, boolean includePhoto) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("gpa"),
                rs.getString("department"),
                rs.getString("advisor"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("email"),
                includePhoto ? rs.getBytes("photo") : null);
    }

    private DataAccessException wrapWriteError(String message, SQLException e) {
        if (e.getErrorCode() == SQLITE_CONSTRAINT) {
            throw new DuplicateEntityException("A student with this ID already exists.", e);
        }
        log.error(message, e);
        return new DataAccessException(message, e);
    }
}
