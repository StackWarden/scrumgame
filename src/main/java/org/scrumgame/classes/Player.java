package org.scrumgame.classes;

import org.scrumgame.database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Player {
    int id;
    String name;

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