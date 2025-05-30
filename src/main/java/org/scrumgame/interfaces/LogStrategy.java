package org.scrumgame.interfaces;

import org.scrumgame.classes.Level;
import org.scrumgame.database.models.Session;

import java.util.List;

public interface LogStrategy {
    Level log(Session session, Level level);
    List<? extends GameLog> getLogs(Session session);
    void markCurrentLogCompleted(Session session);
    String getPromptByLogId(int logId);
    Level loadByLogId(int logId);
}
