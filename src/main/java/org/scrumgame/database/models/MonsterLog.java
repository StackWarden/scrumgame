package org.scrumgame.database.models;

import org.scrumgame.classes.Question;
import org.scrumgame.interfaces.GameLog;
import java.util.ArrayList;
import java.util.List;

public record MonsterLog(int id, int sessionId, List<Question> questions, boolean defeated) implements GameLog {
    public MonsterLog(int id, int sessionId, List<Question> questions, boolean defeated) {
        this.id = id;
        this.sessionId = sessionId;
        this.questions = questions != null ? questions : new ArrayList<>();
        this.defeated = defeated;
    }

    @Override
    public List<Question> getQuestions() {
        return this.questions;
    }
}
