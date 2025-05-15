package org.scrumgame.classes;

import org.scrumgame.services.LogService;
import org.scrumgame.strategies.RoomLogStrategy;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class Room extends Level {
    public Question question;

    public Monster spawnMonster() {
        return null;
    }

    @Override
    public LogService getLogService() {
        RoomLogStrategy roomLogStrategy = new RoomLogStrategy();
        LogService logService = new LogService();
        logService.setStrategy(roomLogStrategy);
        return logService;
    }

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
