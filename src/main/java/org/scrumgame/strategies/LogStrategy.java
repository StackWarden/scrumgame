package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.database.models.Session;

public interface LogStrategy {
    void log(Session session, Level level);
}
