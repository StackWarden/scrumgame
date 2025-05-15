package org.scrumgame.classes;

import java.util.AbstractMap;
import java.util.List;

public class Monster extends Level {
    public List<Question> questions;

    public void gameOver() {

    }
    public boolean isDefeated() {
        return false;
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
    public List<AbstractMap.SimpleEntry<Question, Boolean>> checkAnswers(List<AbstractMap.SimpleEntry<Question, String>> answers) {
        return List.of();
    }
}
