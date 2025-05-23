package org.scrumgame.classes;

public abstract class Level {
    public int scoreRequirement = 0;
    public abstract String getPrompt();
    public abstract boolean checkAnswer(String answer);
    public abstract String getAnswer();

    private int logId = -1;

    public int getLogId() {
        return logId;
    }

    public void setScoreRequirement(int scoreRequirement) {
        this.scoreRequirement = scoreRequirement;
    }

    public int getScoreRequirement() {
        return scoreRequirement;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Question getQuestion() {
        return null;
    }
}
