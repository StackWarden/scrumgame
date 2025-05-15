package org.scrumgame.database.models;

import org.scrumgame.classes.Question;

public class RoomLog {
    private int id;
    private int sessionId;
    private Question question;
    private boolean completed;

    public RoomLog(int sessionId, Question question, boolean completed) {
        this.sessionId = sessionId;
        this.question = question;
        this.completed = completed;
    }
}
