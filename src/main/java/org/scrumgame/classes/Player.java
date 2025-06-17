package org.scrumgame.classes;

import org.scrumgame.database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Player {
    int id;
    String name;
    private static final int MAX_NAME_LENGTH = 255;

    public Player() {
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }

        // Enforce maximum name length
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Name exceeds maximum length of %d characters", MAX_NAME_LENGTH)
            );
        }

        this.name = name;

        try {
            String sql = "UPDATE player SET name = ? WHERE id = ?";
            try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setInt(2, this.id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

    }

    public static void setCurrentPlayer(Player player) {
    }

    public void loadFromDatabase(int playerId) {
        try {
            String sql = "SELECT id, name FROM player WHERE id = ?";
            try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, playerId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.name = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

}