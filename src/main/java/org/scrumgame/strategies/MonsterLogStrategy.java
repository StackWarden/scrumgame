package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Monster;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;

public class MonsterLogStrategy implements LogStrategy {

    @Override
    public void log(Session session, Level level) {
        Monster monster = (Monster) level;
        MonsterLog log = new MonsterLog(session.getId(), monster.getQuestions(), monster.isDefeated());
    }
}
