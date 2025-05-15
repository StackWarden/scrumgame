package org.scrumgame.classes;

import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.LogStrategy;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

public abstract class Level {
    public abstract LogService getLogService();

    public abstract LogStrategy getLogStrategy();
    public abstract Room nextLevel(Session session);
    public abstract void setQuestions(List<Question> questions);
    public abstract List<Map.Entry<Question, Boolean>> checkAnswers(
            List<Map.Entry<Question, String>> answers
    );
}
