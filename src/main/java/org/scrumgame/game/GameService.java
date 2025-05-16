package org.scrumgame.game;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.MonsterLogStrategy;
import org.scrumgame.strategies.RoomLogStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameContext context;
    private final LogService logService;

    @Autowired
    public GameService(GameContext context) {
        this.context = context;
        this.logService = new LogService();
    }

    private boolean inGame = false;
    private Session session;

    public boolean isInGame() {
        return inGame;
    }

    public Session startNewSession() {
        Session session = Session.createNew(1);
        Room room = Room.createRoom(session);

        LogService localLogService = this.logService;
        localLogService.setStrategy(new RoomLogStrategy());

        room = (Room) localLogService.executeLog(session, room);

        assert session != null;
        session.setCurrentRoomId(room.getLogId());

        this.session = session;
        inGame = true;
        return session;
    }

    public Session loadSession(int sessionId) {
        Session session = Session.loadById(sessionId);

        return session;
    }

    public String getCurrentPrompt() {
        LogService localLogService = this.logService;
        localLogService.setStrategy(new RoomLogStrategy());

        return session.getCurrentPrompt(localLogService);
    }

    public String submitAnswer(String answer) {
        List<Monster> activeMonsters = logService.getActiveMonsters(session);
        if (!activeMonsters.isEmpty()) {
            Monster currentMonster = activeMonsters.getFirst();
            currentMonster.setLogId(session.getCurrentMonsterLogId());

            boolean correct = currentMonster.checkAnswer(answer);
            if (correct) {
                logService.markCurrentLogCompleted(currentMonster);

                // Check for remaining monsters
                List<Monster> remaining = logService.getActiveMonsters(session);
                if (!remaining.isEmpty()) {
                    Monster next = remaining.getFirst();
                    session.setCurrentMonsterLogId(next.getLogId());
                    session.save();
                    return "Correct! Next monster: " + next.getPrompt();
                } else {
                    session.setCurrentMonsterLogId(null);
                    logService.markCurrentLogCompleted(getCurrentRoom(session.getCurrentRoomId()));
                    session.setCurrentRoomId(null);
                    session.save();
                    return "All monsters defeated! You're back in your room.";
                }
            } else {
                return "Wrong. Try again.";
            }
        }

        Room room = getCurrentRoom(session.getCurrentRoomId());
        room.setLogId(session.getCurrentRoomId());

        boolean correct = room.checkAnswer(answer);
        if (correct) {
            logService.markCurrentLogCompleted(room);
            session.setCurrentRoomId(null);
            session.save();
            return "Good Answer!\n U may proceed to the next room with 'next'.";
        } else {
            List<Monster> monsters = context.spawnMonstersForRoom(session, room);
            for (Monster monster : monsters) {
                logService.setStrategy(new MonsterLogStrategy());
                Monster logged = (Monster) logService.executeLog(session, monster);
                if (logged != null) {
                    monster.setLogId(logged.getLogId());
                }
            }

            List<Monster> active = logService.getActiveMonsters(session);
            if (!active.isEmpty()) {
                Monster first = active.getFirst();
                session.setCurrentMonsterLogId(first.getLogId());
                session.save();
                return "Wrong! You've awakened "+ active.size() + " monsters!\nMonster appears: " + first.getPrompt();
            } else {
                return "Failed to spawn monsters.";
            }
        }
    }

    public String goToNextRoom() {
        if (session == null || !session.isActive()) {
            return "No active session.";
        }

        if (session.getCurrentRoomId() != -1) {
            return "You must complete the current room first.";
        }

        if (session.getCurrentMonsterLogId() != -1) {
            return "You must defeat all monsters before proceeding.";
        }

        // Create a new room
        Room newRoom = Room.createRoom(session);

        // Log the room
        logService.setStrategy(new RoomLogStrategy());
        newRoom = (Room) logService.executeLog(session, newRoom);

        // Set new room in session
        session.setCurrentRoomId(newRoom.getLogId());
        session.save();

        return "New room entered.\nQuestion: " + newRoom.getPrompt();
    }

    public String getStatus() {
        // TODO: Display current room, score, monster info, etc.
        return "";
    }

    public void endGame() {
        // TODO: Clean up session and reset game state
        inGame = false;
    }

    public Room getCurrentRoom(int logId) {
        logService.setStrategy(new RoomLogStrategy());
        return (Room) logService.loadLevelByLogId(logId);
    }

    public Monster getCurrentMonster(int logId) {
        logService.setStrategy(new MonsterLogStrategy());
        return (Monster) logService.loadLevelByLogId(logId);
    }
}
