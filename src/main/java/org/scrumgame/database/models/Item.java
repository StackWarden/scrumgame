package org.scrumgame.database.models;

import org.scrumgame.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Item {
    private int id;
    private String name;
    private int level_log_id;
    private boolean used = false;
    private Integer player_id = null;

    public Item(int id, String name, int level_log_id, boolean used, Integer player_id) {
        this.id = id;
        this.name = name;
        this.level_log_id = level_log_id;
        this.used = used;
        this.player_id = player_id;
    }

    public Item(boolean used, int level_log_id, String name, int id) {
        this.used = used;
        this.level_log_id = level_log_id;
        this.name = name;
        this.id = id;
    }

    public Item(String name, int level_log_id) {
        this.name = name;
        this.level_log_id = level_log_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel_log_id() {
        return level_log_id;
    }

    public void setLevel_log_id(int level_log_id) {
        this.level_log_id = level_log_id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public Integer getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(Integer player_id) {
        this.player_id = player_id;
    }

    public void save() {
        String insertQuery = "INSERT INTO item (name, level_log_id, used, player_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setInt(2, level_log_id);
            stmt.setBoolean(3, used);

            if (player_id != null) {
                stmt.setInt(4, player_id);
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            var generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[ItemLog] Failed to save item: " + e.getMessage());
        }
    }
}
