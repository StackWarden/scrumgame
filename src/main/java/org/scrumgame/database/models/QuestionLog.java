package org.scrumgame.database.models;

import org.scrumgame.interfaces.GameLog;
import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class QuestionLog implements GameLog {
    private int id;
    private int sessionId;
    private Question question;
    private boolean completed;

    public QuestionLog(int sessionId, Question question, boolean completed) {
        this.sessionId = sessionId;
        this.question = question;
        this.completed = completed;
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

    @Override
    public List<Question> getQuestions() {
        return Collections.singletonList(question);
    }

    public boolean isCompleted() {
        return completed;
    }
}