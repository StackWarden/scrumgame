package org.scrumgame.classes;

import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.LogStrategy;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

public abstract class Level {
    public abstract String getPrompt();
    public abstract boolean checkAnswer(String answer);
    public abstract String getAnswer();

    private int logId = -1;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Question getQuestion() {
        return null;
    }
}
