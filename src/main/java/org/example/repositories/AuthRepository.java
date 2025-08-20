package org.example.repositories;

import org.example.database.Database;

import java.sql.SQLException;

public class AuthRepository {
    public Boolean saveUser(String username, String hashedPassword, String publicKey) {
        String sql = "INSERT INTO users (username, password_hash, public_key) VALUES (?, ?, ?)";
        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, publicKey);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String findPasswordHashedByUser(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("password_hash");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
