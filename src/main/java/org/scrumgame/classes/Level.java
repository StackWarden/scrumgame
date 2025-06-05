package org.scrumgame.classes;

public abstract class Level {
    private int scoreRequirement = 0;
    private int logId = -1;

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

}