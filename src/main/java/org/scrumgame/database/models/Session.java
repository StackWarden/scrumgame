package org.scrumgame.database.models;

import org.scrumgame.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Session {
    private int id;
    private int playerId;
    private int currentLevel;
    private int score;
    private int monstersEncountered;
    private boolean gameOver;

    public Session(boolean gameOver, int monstersEncountered, int score, int currentLevel, int playerId) {
        this.gameOver = gameOver;
        this.monstersEncountered = monstersEncountered;
        this.score = score;
        this.currentLevel = currentLevel;
        this.playerId = playerId;
    }

    public Session(boolean gameOver, int monstersEncountered, int score, int currentLevel, int playerId, int id) {
        this.gameOver = gameOver;
        this.monstersEncountered = monstersEncountered;
        this.score = score;
        this.currentLevel = currentLevel;
        this.playerId = playerId;
        this.id = id;
    }

    public Session() {
        this.id = id;
    }

    public int getId() {
        String sql = "SELECT id FROM sessions WHERE player_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)){

             stmt.setInt(1, getCurrentId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getMonstersEncountered() {
        return monstersEncountered;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getPlayerId() {
        return playerId;
    }
    public int getCurrentId() {
        return id;
    }
}
