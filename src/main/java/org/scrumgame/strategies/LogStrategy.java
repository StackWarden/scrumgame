package org.scrumgame.strategies;

import org.scrumgame.classes.GameLog;
import org.scrumgame.classes.Level;
import org.scrumgame.database.models.Session;

import java.util.List;

public interface LogStrategy {
    void log(Session session, Level level);
    List<? extends GameLog> getLogs(Session session);
    void markCurrentLogCompleted(Session session);
}
