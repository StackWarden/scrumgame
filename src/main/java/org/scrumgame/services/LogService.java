package org.scrumgame.services;

import org.scrumgame.classes.*;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.GameLog;
import org.scrumgame.interfaces.LogStrategy;
import org.scrumgame.strategies.MonsterLogStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LogService {
    private LogStrategy strategy;

    public void setStrategy(LogStrategy strategy) {
        this.strategy = strategy;
    }

    public Level executeLog(Session session, Level level) {
        if (strategy == null) {
            throw new IllegalStateException("No strategy set.");
        }
        return strategy.log(session, level);
    }

    public List<? extends GameLog> getLogs(Session session) {
        if (strategy == null) {
            throw new IllegalStateException("No strategy set.");
        }
        return strategy.getLogs(session);
    }

    public void markCurrentLogCompleted(Session session) {
        if (strategy == null) throw new IllegalStateException("No strategy set.");
        strategy.markCurrentLogCompleted(session);
    }

    public List<Monster> getActiveMonsters(Session session) {
        MonsterLogStrategy strategy = new MonsterLogStrategy();
        List<MonsterLog> logs = strategy.getLogs(session);


        return logs.stream()
                .filter(log -> {
                    boolean alive = !log.isDefeated();
                    return alive;
                })
                .map(log -> {
                    Question q = log.getQuestions().getFirst();

                    Monster m = new Monster(q);
                    m.setLogId(log.getId());

                    return m;
                })
                .toList();
    }

    public String getPromptByLogId(int logId) {
        if (strategy == null) {
            return "No strategy set.";
        }
        return strategy.getPromptByLogId(logId);
    }

    public Level loadLevelByLogId(int logId) {
        if (strategy == null) {
            throw new IllegalStateException("LogStrategy not set.");
        }
        return strategy.loadByLogId(logId);
    }
}
