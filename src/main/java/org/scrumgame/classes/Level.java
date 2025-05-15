package org.scrumgame.classes;

import java.util.AbstractMap;
import java.util.List;

public abstract class Level {
    public abstract List<Question> getQuestions();
    public abstract Room nextLevel();
    public abstract List<AbstractMap.SimpleEntry<Question, Boolean>> checkAnswers(List<AbstractMap.SimpleEntry<Question, String>> answers);
}
