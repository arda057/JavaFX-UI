package JavaFXexample.repistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import JavaFXexample.database.DatabaseManager;
import JavaFXexample.model.Student;

public class StudentRepository {
    public void insertStudent(Student student){
        String sql = 
                """
                INSERT INTO students 
                (id, name, gpa, department)
                VALUES (?,?,?,?)
                """;
        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        )
        {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setDouble(3, student.getGpa());
            pstmt.setString(4, student.getDepartment());

            pstmt.executeUpdate();

            System.out.println("Student inserted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStudent(Student student){
        String sql = 
                """
                UPDATE students
                SET name = ?,
                    gpa = ?,
                    department = ?
                WHERE id = ?
                """;

        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) 
        {
            pstmt.setString(1, student.getName());
            pstmt.setDouble(2, student.getGpa());
            pstmt.setString(3, student.getDepartment());
            pstmt.setInt(4, student.getId());

            pstmt.executeUpdate();

            System.out.println("Student uploaded!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id){
        String sql = 
                """
                DELETE FROM students 
                WHERE id = ?;
                """;
        try (
            Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ) 
        {
            pstmt.setInt(1, id);

            pstmt.executeUpdate();

            System.out.println("Student deleted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Student> getStudents(){
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (
            Connection conn = DatabaseManager.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        )
        {
            while(rs.next()){
                Student student = new Student(
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getDouble("gpa"), 
                    rs.getString("department")
                );
                students.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

}
