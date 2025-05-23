package org.scrumgame.database.models;

import org.scrumgame.classes.Question;
import org.scrumgame.interfaces.GameLog;
import java.util.ArrayList;
import java.util.List;

public class MonsterLog implements GameLog {
    private int id;
    private int sessionId;
    private List<Question> questions;
    private boolean defeated;

    public MonsterLog(int id, int sessionId, List<Question> questions, boolean defeated) {
        this.id = id;
        this.sessionId = sessionId;
        this.questions = questions != null ? questions : new ArrayList<>();
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
