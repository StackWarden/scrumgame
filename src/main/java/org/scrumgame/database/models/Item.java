package org.scrumgame.database.models;

import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Item {
    private int id;
    private String name;
    private int level_log_id;
    private boolean used = false;
    private Integer player_id = null;
    private Integer session_id = null;

    public Integer getSession_id() {
        return session_id;
    }

    public void setSession_id(Integer session_id) {
        this.session_id = session_id;
    }

    public Item(int id, String name, int level_log_id, boolean used, Integer player_id) {
        this.id = id;
        this.name = name;
        this.level_log_id = level_log_id;
        this.used = used;
        this.player_id = player_id;
    }

    public Item(int id, String name, int level_log_id, boolean used, Integer player_id, Integer session_id) {
        this.id = id;
        this.name = name;
        this.level_log_id = level_log_id;
        this.used = used;
        this.player_id = player_id;
        this.session_id = session_id;
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
        String insertQuery = """
        INSERT INTO item (id, name, level_log_id, used, player_id, session_id)
        VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            level_log_id = VALUES(level_log_id),
            used = VALUES(used),
            player_id = VALUES(player_id),
            session_id = VALUES(session_id)
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Set ID or NULL if not yet assigned
            if (this.id > 0) {
                stmt.setInt(1, this.id);
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }

            stmt.setString(2, name);
            stmt.setInt(3, level_log_id);
            stmt.setBoolean(4, used);

            if (player_id != null) {
                stmt.setInt(5, player_id);
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            if (session_id != null) {
                stmt.setInt(6, session_id);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            if (this.id <= 0) {
                var generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("[ItemLog] Failed to save item: " + e.getMessage());
        }
    }

    //    Static
    public static List<Item> getUnpickedItemsForRoom(int levelLogId) {
        List<Item> items = new ArrayList<>();

        String query = "SELECT id, name, level_log_id, used, player_id FROM item " +
                "WHERE level_log_id = ? AND player_id IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, levelLogId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("level_log_id"),
                        rs.getBoolean("used"),
                        null
                );
                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("[ItemLog] Failed to load unpicked items: " + e.getMessage());
        }

        return items;
    }

    public static Item loadById(int itemId) {
        String sql = "SELECT id, name, level_log_id, used, player_id, session_id FROM item WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("level_log_id"),
                        rs.getBoolean("used"),
                        rs.getObject("player_id") != null ? rs.getInt("player_id") : null,
                        rs.getObject("session_id") != null ? rs.getInt("session_id") : null
                );
            }
        } catch (SQLException e) {
            System.err.println("[Item] Failed to load item by id: " + e.getMessage());
        }
        return null;
    }

    public static List<Item> getItemsByPlayerAndSession(int playerId, int sessionId) {
        List<Item> items = new ArrayList<>();

        String sql = """
        SELECT id, name, level_log_id, used, player_id
        FROM item
        WHERE player_id = ? AND session_id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);
            stmt.setInt(2, sessionId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("level_log_id"),
                        rs.getBoolean("used"),
                        rs.getObject("player_id", Integer.class)
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("[DB] Error fetching items by player/session: " + e.getMessage());
        }

        return items;
    }
}
