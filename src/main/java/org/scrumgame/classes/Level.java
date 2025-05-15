package org.scrumgame.classes;

import org.scrumgame.services.LogService;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public abstract class Level {
    public abstract LogService getLogService();

    public List<Question> getQuestions(int amount) {
        return new ArrayList<Question>();
    }

    public abstract Room nextLevel();
    public abstract List<AbstractMap.SimpleEntry<Question, Boolean>> checkAnswers(List<AbstractMap.SimpleEntry<Question, String>> answers);
}
