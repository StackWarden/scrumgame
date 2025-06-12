package org.scrumgame.database.models;

import org.scrumgame.classes.Level;
import org.scrumgame.interfaces.GameLog;
import org.scrumgame.classes.Question;

import java.util.Collections;
import java.util.List;

public class QuestionLog extends Level implements GameLog {
    private int id;
    private final int sessionId;
    private int levelLogId;
    private final Question question;
    private final boolean completed;

    public QuestionLog(int sessionId, int levelLogId, Question question, boolean completed) {
        this.sessionId = sessionId;
        this.levelLogId = levelLogId;
        this.question = question;
        this.completed = completed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int sessionId() {
        return sessionId;
    }

    public int getLevelLogId() {
        return levelLogId;
    }

    @Override
    public List<Question> getQuestions() {
        return Collections.singletonList(question);
    }

    @Override
    public String getPrompt() {
        return question.getQuestion();
    }

    @Override
    public boolean checkAnswer(String answer) {
        return question.checkAnswer(answer);
    }

    @Override
    public String getAnswer() {
        return question.getAnswer();
    }

    public boolean isCompleted() {
        return completed;
    }
}
