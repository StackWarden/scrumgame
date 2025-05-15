package org.scrumgame.services;

import org.scrumgame.classes.Level;
import org.scrumgame.database.models.Session;
import org.scrumgame.strategies.LogStrategy;

public class LogService {
    private LogStrategy strategy;

    public void setStrategy(LogStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeLog(Session session, Level level) {
        if (strategy != null) {
            strategy.log(session, level);
        }
    }
}
