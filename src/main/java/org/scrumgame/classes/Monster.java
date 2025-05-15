package org.scrumgame.classes;

import org.scrumgame.services.LogService;
import org.scrumgame.strategies.MonsterLogStrategy;

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
    public LogService getLogService() {
        MonsterLogStrategy monsterLogStrategy = new MonsterLogStrategy();
        LogService logService = new LogService();
        logService.setStrategy(monsterLogStrategy);
        return logService;
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
