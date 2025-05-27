package org.scrumgame.game;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Room;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.database.RoomLogHelper;
import org.scrumgame.database.models.Item;
import org.scrumgame.database.models.Session;
import org.scrumgame.factories.ItemSpawner;
import org.scrumgame.jokers.SkipRoomJoker;
import org.scrumgame.observers.MonsterSpawnMessageObserver;
import org.scrumgame.services.Inventory;
import org.scrumgame.services.LogService;
import org.scrumgame.services.MonsterSpawner;
import org.scrumgame.strategies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Random;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameContext context;
    private final LogService logService;
    // the subject and the observer to react to monster spawning
    private final MonsterSpawner monsterSpawner;
    private final MonsterSpawnMessageObserver messageObserver;

    private final ItemSpawner itemSpawner;
    private final Inventory inventory;
    private final SkipRoomJoker room;

    private boolean inGame = false;
    private Session session;

        @Autowired
        public GameService(GameContext context, MonsterSpawner monsterSpawner, MonsterSpawnMessageObserver messageObserver, ItemSpawner itemSpawner, Inventory inventory, SkipRoomJoker room) {
            this.context = context;
            this.logService = new LogService();
            // inject both the subject and observer via spring
            this.monsterSpawner = monsterSpawner;
            this.messageObserver = messageObserver;
            this.itemSpawner = itemSpawner;
            this.inventory = inventory;
            this.room = room;
        }

    public boolean isInGame() {
        return inGame;
    }

    public void startNewSession() {
        Session session = Session.createNew(1);
        Room room = Room.createRoom(session);

        logService.setStrategy(new RoomLogStrategy());
        room = (Room) logService.executeLog(session, room);

        assert session != null;
        session.setCurrentRoomId(room.getLogId());

        this.session = session;
        inGame = true;
    }

    public void loadSession(int sessionId) {
        this.session = Session.loadById(sessionId);
    }

    public String getCurrentPrompt() {
        List<Monster> activeMonsters = logService.getActiveMonsters(session);
        if (!activeMonsters.isEmpty()) {
            Monster current = activeMonsters.getFirst();
            return "Monster appears: " + current.getPrompt();
        }

        logService.setStrategy(new RoomLogStrategy());
        return session.getCurrentPrompt(logService);
    }

    public void submitAnswer(String answer, boolean skip) {
        if (hasActiveMonsters()) {
            handleMonsterAnswer(answer);
            return;
        }
        handleRoomAnswer(answer, skip);
    }

    public String goToNextRoom(boolean check) {
        if (check) {
            if (session == null || !session.isActive()) {
                return "No active session.";
            }
            if (session.getCurrentRoomId() != -1) {
                return "You must complete the current room first.";
            }

            if (session.getCurrentMonsterLogId() != -1) {
                return "You must defeat all monsters before proceeding.";
            }
        }

        // Create a new room
        Room newRoom = Room.createRoom(session);

        // Log the room
        logService.setStrategy(new RoomLogStrategy());
        newRoom = (Room) logService.executeLog(session, newRoom);

        // Set new room in session
        session.setCurrentRoomId(newRoom.getLogId());
        session.save();

        itemSpawner.spawnItems(session.getCurrentRoomId(), 3);

        return "New room entered.\nQuestion: " + newRoom.getPrompt();
    }

    public String getStatus() {
        // TODO: Display current room, score, monster info, etc.
        return "";
    }

    public void endGame() {
        inGame = false;
    }

    public String getRoomItems() {
        int logId = session.getCurrentRoomId();
        if (logId == -1) return "No active room.";
        List<Item> items = inventory.getAvailableItemsInRoom(logId);
        if (items.isEmpty()) return "No items available in this room.";
        return items.stream()
                .map(item -> "Item ID: " + item.getId() + " - " + item.getName())
                .collect(Collectors.joining("\n"));
    }

    public void pickUpItem(int itemId) {
            inventory.pickUpItem(itemId, session);
    }

    public String viewPlayerInventory() {
        int playerId = session.getPlayerId();
        List<Item> items = inventory.getInventoryItems(playerId, session.getId());

        if (items.isEmpty()) {
            return "You are not carrying any items.";
        }

        return items.stream()
                .map(item -> "- " + item.getName() + " (ID: " + item.getId() + ")")
                .collect(Collectors.joining("\n"));
    }

    public Room getCurrentRoom(int logId) {
        logService.setStrategy(new RoomLogStrategy());
        return (Room) logService.loadLevelByLogId(logId);
    }

    public String getCurrentRoom() {
        int currentRoomNumber = RoomLogHelper.getCurrentRoomNumber(session);
        int totalRooms = 6;

        return String.format("%d out of %d", currentRoomNumber, totalRooms);
    }


    public Monster getCurrentMonster(int logId) {
        logService.setStrategy(new MonsterLogStrategy());
        return (Monster) logService.loadLevelByLogId(logId);
    }

    public void useItem(int itemId) {
        if (session == null || !session.isActive()) {
            System.out.println("No active session.");
            return;
        }

        if (inventory == null) {
            System.out.println("Inventory system not initialized.");
            return;
        }

        inventory.use(itemId, session.getPlayerId(), session.getId());
    }

    public void defeatCurrentMonster(String killMethod) {
        Monster monster = getCurrentActiveMonster();
        if (monster == null) {
            System.out.println("No monster to defeat.");
            return;
        }

        logService.markCurrentLogCompleted(monster);

        List<Monster> remaining = logService.getActiveMonsters(session);
        if (!remaining.isEmpty()) {
            Monster next = remaining.getFirst();
            session.setCurrentMonsterLogId(next.getLogId());
            session.save();
            System.out.println("Monster defeated using "+ killMethod + "! " + next.getPrompt());
            return;
        }

        session.setCurrentMonsterLogId(null);
        logService.markCurrentLogCompleted(getCurrentRoom(session.getCurrentRoomId()));
        session.setCurrentRoomId(null);
        session.save();
        System.out.println("All monsters defeated using item. You're back in your room.");
    }

    private boolean hasActiveMonsters() {
        return !logService.getActiveMonsters(session).isEmpty();
    }

    private Monster getCurrentActiveMonster() {
        return logService.getActiveMonsters(session).stream()
                .filter(m -> m.getLogId() == session.getCurrentMonsterLogId())
                .findFirst()
                .orElse(null);
    }

    private void handleMonsterAnswer(String answer) {
        Monster currentMonster = getCurrentActiveMonster();
        if (currentMonster == null) {
            System.out.println("Monster mismatch or already defeated.");
            return;
        }

        boolean correct = currentMonster.checkAnswer(answer);
        if (!correct) {
            System.out.println("Wrong. Try again.");
            return;
        }

        logService.markCurrentLogCompleted(currentMonster);

        List<Monster> remaining = logService.getActiveMonsters(session);
        if (!remaining.isEmpty()) {
            Monster next = remaining.getFirst();
            session.setCurrentMonsterLogId(next.getLogId());
            session.save();
            System.out.println("Monster defeated! Next monster: " + next.getPrompt());
            return;
        }

        session.setCurrentMonsterLogId(null);
        logService.markCurrentLogCompleted(getCurrentRoom(session.getCurrentRoomId()));
        session.setCurrentRoomId(null);
        session.save();
        System.out.println("All monsters defeated! You're back in your room.");
    }

    private void handleRoomAnswer(String answer, boolean skip) {
        Room room = getCurrentRoom(session.getCurrentRoomId());
        room.setLogId(session.getCurrentRoomId());

        boolean correct = room.checkAnswer(answer);
        if (correct || skip) {
            logService.markCurrentLogCompleted(room);
            session.setCurrentRoomId(null);
            session.save();
            System.out.println("Good Answer!\nYou may proceed to the next room with 'next'.");
            return;
        }

        messageObserver.clear();
        monsterSpawner.addObserver(messageObserver);

        List<Monster> monsters = monsterSpawner.spawnMonstersForRoom(session, room);
        for (Monster monster : monsters) {
            logService.setStrategy(new MonsterLogStrategy());
            Monster logged = (Monster) logService.executeLog(session, monster);
            if (logged != null) {
                monster.setLogId(logged.getLogId());
            }
        }

        List<Monster> active = logService.getActiveMonsters(session);
        if (!active.isEmpty()) {
            session.setCurrentMonsterLogId(active.getFirst().getLogId());
            session.save();
        }

        monsterSpawner.removeObserver(messageObserver);
        System.out.println(messageObserver.getLastMessage());
    }

    public String getHint() {
        if (session == null) {
            return "No session active.";
        }

        if (session.getCurrentMonsterLogId() != -1) {
            Monster monster = getCurrentMonster(session.getCurrentMonsterLogId());
            String answer = monster.getAnswer();
            String hint = monster.getHint();

            HintContext hintContext = new HintContext();

            if (hint != null && !hint.isBlank()) {
                hintContext.setStrategy(new PredefinedHintStrategy());
            } else {
                hintContext.setStrategy(new RandomObfuscatedHintStrategy(0.3));
            }

            return hintContext.getHint(answer, hint);
        }

        return "You can only get hints for monsters!";
    }

    public void dropItem(int itemId) {
        inventory.drop(itemId, session);
    }

    public void answerComplete(Room room) {
        logService.markCurrentLogCompleted(room);
        session.setCurrentRoomId(null);
        session.save();
    }

}
