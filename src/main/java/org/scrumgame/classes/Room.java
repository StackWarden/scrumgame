package org.scrumgame.classes;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class Room extends Level {
    public Question question;

    public Monster spawnMonster() {
        return null;
    }

    @Override
    public List<Question> getQuestions() {
        return List.of();
    }

    @Override
    public Room nextLevel() {
        return null;
    }

    @Override
    public List<SimpleEntry<Question, Boolean>> checkAnswers(List<SimpleEntry<Question, String>> answers) {
        return List.of();
    }
}
