package org.scrumgame.services;

import org.scrumgame.classes.*;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.strategies.LogStrategy;
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

    public void markCurrentLogCompleted(Level level) {
        int logId = level.getLogId();
        if (logId == -1) return;

        String sql;
        if (level instanceof Room) {
            sql = "UPDATE level_log SET completed = true WHERE id = ?";
        } else if (level instanceof Monster) {
            sql = "UPDATE monster_log SET defeated = true WHERE id = ?";
        } else {
            System.out.println("Unknown level type: " + level.getClass().getSimpleName());
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error marking log completed for log ID " + logId);
            e.printStackTrace();
        }
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
