package org.scrumgame.database.models;

import org.scrumgame.classes.Game;
import org.scrumgame.classes.GameLog;
import org.scrumgame.classes.Question;

import java.util.List;

public class MonsterLog implements GameLog {
    private int id;
    private int sessionId;
    private List<Question> questions;
    private boolean defeated;

    public MonsterLog(int sessionId, List<Question> questions, boolean defeated) {
        this.sessionId = sessionId;
        this.questions = questions;
        this.defeated = defeated;
    }

    public int getId() {
        return id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
