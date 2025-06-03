package org.scrumgame.classes;

public abstract class Level {
    private int scoreRequirement = 0;
    private int logId = -1;

    private Integer roomNumber = null;

    public abstract String getPrompt();
    public abstract boolean checkAnswer(String answer);
    public abstract String getAnswer();

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getScoreRequirement() {
        return scoreRequirement;
    }

    public void setScoreRequirement(int scoreRequirement) {
        this.scoreRequirement = scoreRequirement;
    }

    public Question getQuestion() {
        return null;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean isMonster() {
        return roomNumber == null;
    }

    public boolean isRoom() {
        return roomNumber != null;
    }
}