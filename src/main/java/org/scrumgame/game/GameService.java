package org.scrumgame.game;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.Session;
import org.scrumgame.observers.MonsterSpawnMessageObserver;
import org.scrumgame.services.LogService;
import org.scrumgame.services.MonsterSpawner;
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
    // the subject and the observer to react to monster spawning
    private final MonsterSpawner monsterSpawner;
    private final MonsterSpawnMessageObserver messageObserver;

    private boolean inGame = false;
    private Session session;


    @Autowired
    public GameService(GameContext context, MonsterSpawner monsterSpawner, MonsterSpawnMessageObserver messageObserver) {
        this.context = context;
        this.logService = new LogService();
        // inject both the subject and observer via spring
        this.monsterSpawner = monsterSpawner;
        this.messageObserver = messageObserver;
    }

    public boolean isInGame() {
        return inGame;
    }

    public Session startNewSession() {
        Session session = Session.createNew(1);
        Room room = Room.createRoom(session);

        logService.setStrategy(new RoomLogStrategy());
        room = (Room) logService.executeLog(session, room);

        session.setCurrentRoomId(room.getLogId());

        this.session = session;
        inGame = true;
        return session;
    }

    public Session loadSession(int sessionId) {
        Session session = Session.loadById(sessionId);
        this.session = session;
        return session;
    }

    public String getCurrentPrompt() {
        logService.setStrategy(new RoomLogStrategy());
        return session.getCurrentPrompt(logService);
    }

    public String submitAnswer(String answer) {
        List<Monster> activeMonsters = logService.getActiveMonsters(session);
        if (!activeMonsters.isEmpty()) {
            Monster currentMonster = activeMonsters.get(0);
            currentMonster.setLogId(session.getCurrentMonsterLogId());

            boolean correct = currentMonster.checkAnswer(answer);
            if (correct) {
                logService.markCurrentLogCompleted(currentMonster);

                // Check for remaining monsters
                List<Monster> remaining = logService.getActiveMonsters(session);
                if (!remaining.isEmpty()) {
                    Monster next = remaining.get(0);
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
            return "Good Answer!\nYou may proceed to the next room with 'next'.";
        } else {
            // Use the injected MonsterSpawner and Observer
            // clear previous message before any handlings
            messageObserver.clear();
            monsterSpawner.addObserver(messageObserver);

            List<Monster> monsters = monsterSpawner.spawnMonstersForRoom(session, room);
            // log each monster so they're persisted and can be referenced (for fighting) later on
            for (Monster monster : monsters) {
                logService.setStrategy(new MonsterLogStrategy());
                Monster logged = (Monster) logService.executeLog(session, monster);
                if (logged != null) {
                    monster.setLogId(logged.getLogId());
                }
            }
            // set the current monster to the first active
            List<Monster> active = logService.getActiveMonsters(session);
            if (!active.isEmpty()) {
                Monster first = active.get(0);
                session.setCurrentMonsterLogId(first.getLogId());
                session.save();
            }
            // detach observer
            monsterSpawner.removeObserver(messageObserver);
            // retrieve the message from the observer (decoupled, built by the observer)
            return messageObserver.getLastMessage();
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

    public String getHint() {
        if (session == null) {
            return "No session active.";
        }

        if (session.getCurrentMonsterLogId() != -1) {
            Monster monster = getCurrentMonster(session.getCurrentMonsterLogId());
            return monster.getHint();
        }

        return "You can only get hints for monsters!";
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
