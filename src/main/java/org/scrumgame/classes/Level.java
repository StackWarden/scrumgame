package org.scrumgame.classes;

public abstract class Level {
    private int logId = -1;
    private boolean completed = false;

    public abstract String getPrompt();
    public abstract boolean checkAnswer(String answer);
    public abstract String getAnswer();

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Question getQuestion() {
        return null;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}