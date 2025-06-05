package org.scrumgame.database.models;

import org.scrumgame.interfaces.GameLog;
import org.scrumgame.classes.Question;

import java.util.Collections;
import java.util.List;

public class QuestionLog implements GameLog {
    private int id;
    private int sessionId;
    private int levelLogId;
    private Question question;
    private boolean completed;

    public QuestionLog(int sessionId, int levelLogId, Question question, boolean completed) {
        this.sessionId = sessionId;
        this.levelLogId = levelLogId;
        this.question = question;
        this.completed = completed;
    }

    public QuestionLog(int sessionId, Question question, boolean completed) {
        this(sessionId, -1, question, completed);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getLevelLogId() {
        return levelLogId;
    }

    public void setLevelLogId(int levelLogId) {
        this.levelLogId = levelLogId;
    }

    @Override
    public List<Question> getQuestions() {
        return Collections.singletonList(question);
    }

    public boolean isCompleted() {
        return completed;
    }
}
