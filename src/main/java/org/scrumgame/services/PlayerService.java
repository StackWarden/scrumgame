package org.scrumgame.services;

import org.scrumgame.classes.Player;
import org.scrumgame.database.DatabaseConnection;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class PlayerService {
    public Player login(String name) {
        String sql = "SELECT id, name FROM player WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
                return player;
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public Player register(String name) {
        if (login(name) != null) {
            System.out.println("A player with that name already exists.");
            return null;
        }

        String sql = "INSERT INTO player(name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating player failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Player player = new Player();
                    player.setId(generatedKeys.getInt(1));
                    player.setName(name);
                    return player;
                } else {
                    throw new SQLException("Creating player failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
        return null;
    }


    public boolean deleteAccount(int id) {
        String sql = "DELETE FROM player WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int deleted = stmt.executeUpdate();
            return deleted > 0;

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
        return false;
    }

    public Player getPlayerById(int id) {
        String sql = "SELECT id, name FROM player WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
                return player;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean updatePlayerName(int id, String newName) {
        String sql = "UPDATE player SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setInt(2, id);
            int updated = stmt.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPlayerNameById(int id) {
        Player player = getPlayerById(id);
        return (player != null) ? player.getName() : "Unknown Player";
    }
}
