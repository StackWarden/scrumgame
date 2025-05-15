package org.scrumgame.database.models;

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

    public int getId() {
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
}
