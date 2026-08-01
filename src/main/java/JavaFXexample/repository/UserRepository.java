package JavaFXexample.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import JavaFXexample.database.DatabaseManager;
import JavaFXexample.model.User;

public class UserRepository {
    public boolean insertUser(String username, String passwordHash, String salt) {
        String sql = """
                INSERT INTO users
                (username, password_hash, salt)
                VALUES (?,?,?)
                """;
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, salt);

            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                return false;
            }

            e.printStackTrace();
            return false;
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement ptsmt = conn.prepareStatement(sql);) {
                    
            ptsmt.setString(1, username);

            try (ResultSet rs = ptsmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("salt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
