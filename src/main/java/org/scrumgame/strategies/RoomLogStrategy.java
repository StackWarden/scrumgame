package org.scrumgame.strategies;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.RoomLog;
import org.scrumgame.database.models.Session;

public class RoomLogStrategy  implements LogStrategy {

    @Override
    public void log(Session session, Level level) {
        Room room = (Room) level;
        RoomLog log = new RoomLog(session.getId(), room.getQuestions().getFirst(), true);
    }
}