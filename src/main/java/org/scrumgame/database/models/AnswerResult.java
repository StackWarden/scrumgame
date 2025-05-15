package org.scrumgame.database.models;

import org.scrumgame.classes.Monster;

public class AnswerResult {
    private final boolean correct;
    private final Monster spawnedMonster;

    public AnswerResult(boolean correct, Monster spawnedMonster) {
        this.correct = correct;
        this.spawnedMonster = spawnedMonster;
    }

    public boolean isCorrect() {
        return correct;
    }

    public Monster getSpawnedMonster() {
        return spawnedMonster;
    }
}
