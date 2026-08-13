package JavaFXexample.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:students.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS students(
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    gpa REAL,
                    department TEXT,
                    photo BLOB
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

    public static void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    photo BLOB
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

}
