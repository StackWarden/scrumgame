package org.scrumgame.database.models;

import org.scrumgame.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Session {
    private int id;
    private int playerId;
    private int currentLevel;
    private int score;
    private int monstersEncountered;
    private boolean gameOver;

    // Constructor with ID (used when loading from DB)
    public Session(int id, int playerId, int currentLevel, int score, int monstersEncountered, boolean gameOver) {
        this.id = id;
        this.playerId = playerId;
        this.currentLevel = currentLevel;
        this.score = score;
        this.monstersEncountered = monstersEncountered;
        this.gameOver = gameOver;
    }

    public Session(boolean gameOver, int monstersEncountered, int score, int currentLevel, int playerId) {
        this(0, playerId, currentLevel, score, monstersEncountered, gameOver);
    }

    public static Session getSessionById(int sessionId) {
        String query = """
            SELECT id, player_id, current_level_log_id, score, monster_encounters, gameover
            FROM session
            WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                int playerId = rs.getInt("player_id");
                int currentLevel = rs.getInt("current_level_log_id");
                int score = rs.getInt("score");
                int monstersEncountered = rs.getInt("monster_encounters");
                boolean gameOver = rs.getBoolean("gameover");

                return new Session(id, playerId, currentLevel, score, monstersEncountered, gameOver);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getScore() {
        return score;
    }

    public int getMonstersEncountered() {
        return monstersEncountered;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}