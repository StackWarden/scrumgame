package org.scrumgame.database.models;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Room;
import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.RoomLogStrategy;

import java.sql.*;
import java.util.Optional;

public class Session {
    private int id;
    private int playerId;
    private Integer currentLevelLogId = -1;
    private Integer currentMonsterLogId = -1;
    private int score;
    private int monsterEncounters;
    private boolean gameOver;

    public Session(int id, int playerId, Integer currentLevelLogId, Integer currentMonsterLogId,
                   int score, int monsterEncounters, boolean gameOver) {
        this.id = id;
        this.playerId = playerId;
        this.currentLevelLogId = currentLevelLogId;
        this.currentMonsterLogId = currentMonsterLogId;
        this.score = score;
        this.monsterEncounters = monsterEncounters;
        this.gameOver = gameOver;
    }
    public Session(int id) {
        this.id = id;
    }

    public Session() {

    }

    public static Session createNew(int playerId) {
        String sql = """
            INSERT INTO session (player_id, score, monster_encounters, gameover)
            VALUES (?, 0, 0, false)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, playerId);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                return new Session(id, playerId, null, null, 0, 0, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Session loadById(int id) {
        String sql = """
            SELECT id, player_id, current_level_log_id, current_monster_log_id,
                   score, monster_encounters, gameover
            FROM session
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Session(
                        rs.getInt("id"),
                        rs.getInt("player_id"),
                        rs.getObject("current_level_log_id", Integer.class),
                        rs.getObject("current_monster_log_id", Integer.class),
                        rs.getInt("score"),
                        rs.getInt("monster_encounters"),
                        rs.getBoolean("gameover")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void save() {
        String sql = """
        UPDATE session
        SET current_level_log_id = ?, current_monster_log_id = ?,
            score = ?, monster_encounters = ?, gameover = ?
        WHERE id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (currentLevelLogId != null && currentLevelLogId != -1) {
                stmt.setInt(1, currentLevelLogId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (currentMonsterLogId != null && currentMonsterLogId != -1) {
                stmt.setInt(2, currentMonsterLogId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setInt(3, score);
            stmt.setInt(4, monsterEncounters);
            stmt.setBoolean(5, gameOver);
            stmt.setInt(6, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int amount) {
        this.score += amount;
    }

    public int getCurrentRoomId() {
        return currentLevelLogId != null ? currentLevelLogId : -1;
    }

    public void setCurrentRoomId(Integer currentRoomId) {
        this.currentLevelLogId = currentRoomId;
    }

    public boolean isActive() {
        return !gameOver;
    }

    public void setGameOver(boolean gameover) {
        this.gameOver = gameover;
    }

    public int getCurrentMonsterLogId() {
        return currentMonsterLogId != null ? currentMonsterLogId : -1;
    }

    public void setCurrentMonsterLogId(Integer currentMonsterLogId) {
        this.currentMonsterLogId = currentMonsterLogId;
    }

    public String getCurrentPrompt(LogService logService) {
        if (currentLevelLogId == null) return "No level active.";
        return logService.getPromptByLogId(currentLevelLogId);
    }
}
