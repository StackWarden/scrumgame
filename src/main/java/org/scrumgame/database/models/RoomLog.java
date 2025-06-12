package org.scrumgame.database.models;

import org.scrumgame.interfaces.GameLog;
import org.scrumgame.classes.Question;

import java.util.Collections;
import java.util.List;

public class RoomLog implements GameLog {
    private int id;
    private int levelLogId;
    private int sessionId;
    private int roomNumber;
    private Question question;
    private boolean completed;

    public RoomLog(int id, int levelLogId, int sessionId, int roomNumber, Question question, boolean completed) {
        this.id = id;
        this.levelLogId = levelLogId;
        this.sessionId = sessionId;
        this.roomNumber = roomNumber;
        this.question = question;
        this.completed = completed;
    }

    public int getLevelLogId() {
        return levelLogId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getId() {
        return id;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    @Override
    public List<Question> questions() {
        return Collections.singletonList(question);
    }

    public boolean isCompleted() {
        return completed;
    }
}