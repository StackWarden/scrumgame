package org.scrumgame.database.models;

import org.scrumgame.classes.GameLog;
import org.scrumgame.classes.Question;

public class RoomLog implements GameLog {
    private int id;
    private int sessionId;
    private Question question;
    private boolean completed;

    public RoomLog(int sessionId, Question question, boolean completed) {
        this.sessionId = sessionId;
        this.question = question;
        this.completed = completed;
    }

    public int getSessionId() {
        return sessionId;
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getId() {
        return id;
    }
}
