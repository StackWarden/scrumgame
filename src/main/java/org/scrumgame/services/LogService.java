package org.scrumgame.services;

import org.scrumgame.classes.GameLog;
import org.scrumgame.classes.Level;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.models.Session;
import org.scrumgame.strategies.LogStrategy;
import org.scrumgame.strategies.RoomLogStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LogService {
    private LogStrategy strategy;

    public void setStrategy(LogStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeLog(Session session, Level level) {
        if (strategy == null) {
            throw new IllegalStateException("No strategy set.");
        }
        strategy.log(session, level);
    }

    public List<? extends GameLog> getLogs(Session session) {
        if (strategy == null) {
            throw new IllegalStateException("No strategy set.");
        }
        return strategy.getLogs(session);
    }

    public void markCurrentLogCompleted(Session session) {
        if (strategy instanceof RoomLogStrategy) {
            String sql = "UPDATE level_log SET completed = true WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, session.getCurrentLevel());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}