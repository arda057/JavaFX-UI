package JavaFXexample.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import JavaFXexample.database.DatabaseManager;
import JavaFXexample.exception.DataAccessException;
import JavaFXexample.exception.DuplicateEntityException;
import JavaFXexample.model.User;

public class UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final int SQLITE_CONSTRAINT = 19;

    public void insertUser(String username, String passwordHash, String salt) {
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
        } catch (SQLException e) {
            if (e.getErrorCode() == SQLITE_CONSTRAINT) {
                throw new DuplicateEntityException("This user name is already taken.", e);
            }
            log.error("Failed to insert user {}", username, e);
            throw new DataAccessException("Could not register user.", e);
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("salt"),
                            rs.getBytes("photo"));
                }
            }
            return null;
        } catch (SQLException e) {
            log.error("Failed to find user {}", username, e);
            throw new DataAccessException("Could not look up user.", e);
        }
    }

    public void updatePasswordHash(int id, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setString(1, passwordHash);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to upgrade password hash for user {}", id, e);
            throw new DataAccessException("Could not update password hash.", e);
        }
    }

    public void updatePhoto(int id, byte[] photo) {
        String sql = "UPDATE users SET photo = ? WHERE id = ?";
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setBytes(1, photo);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update photo for user {}", id, e);
            throw new DataAccessException("Could not update profile photo.", e);
        }
    }

    public void deletePhoto(int id) {
        String sql = "UPDATE users SET photo = NULL WHERE id = ?";
        try (
                Connection conn = DatabaseManager.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete photo for user {}", id, e);
            throw new DataAccessException("Could not delete profile photo.", e);
        }
    }
}
