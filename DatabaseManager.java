package JavaFXexample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:students.db";

    public static Connection connect() throws SQLException 
    {
        return DriverManager.getConnection(URL);
    }

    public static void createTable(){
        String sql = 
                """
                CREATE TABLE IF NOT EXISTS students(
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    gpa REAL,
                    department TEXT
                ) 
                """;

        try (
            Connection conn = connect();
            Statement stmt = conn.createStatement();
        )
        {
            stmt.execute(sql);
        }    
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertStudent(Student student){
        String sql = 
                """
                INSERT INTO students 
                (id, name, gpa, department)
                VALUES (?,?,?,?)
                """;
        try (
            Connection conn = connect();
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

    public static List<Student> getStudents(){
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (
            Connection conn = connect();
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

    public static void updateStudent(Student student){
        String sql = 
                """
                UPDATE students
                SET name = ?,
                    gpa = ?,
                    department = ?
                WHERE id = ?
                """;

        try (
            Connection conn = connect();
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

    public static void deleteStudent(int id){
        String sql = 
                """
                DELETE FROM students 
                WHERE id = ?;
                """;
        try (
            Connection conn = connect();
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
}
