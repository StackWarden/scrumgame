package org.scrumgame.game;

import org.scrumgame.classes.*;
import org.scrumgame.database.RoomLogHelper;
import org.scrumgame.database.models.Item;
import org.scrumgame.database.models.Session;
import org.scrumgame.factories.ItemSpawner;
import org.scrumgame.interfaces.RoomLevel;
import org.scrumgame.jokers.SkipQuestionJoker;
import org.scrumgame.observers.MonsterSpawnMessageObserver;
import org.scrumgame.rooms.Benefits;
import org.scrumgame.seeders.*;
import org.scrumgame.services.Inventory;
import org.scrumgame.services.LogService;
import org.scrumgame.services.MonsterSpawner;
import org.scrumgame.strategies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final Scanner scanner = new Scanner(System.in);
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameContext context;
    private final LogService logService;
    private final MonsterSpawner monsterSpawner;
    private final MonsterSpawnMessageObserver messageObserver;
    private final ItemSpawner itemSpawner;
    private final Inventory inventory;

    private boolean inGame = false;
    private Session session;
    private Player player;

    @Autowired
    public GameService(GameContext context, MonsterSpawner monsterSpawner, MonsterSpawnMessageObserver messageObserver, ItemSpawner itemSpawner, Inventory inventory) {
        this.context = context;
        this.logService = new LogService();
        this.monsterSpawner = monsterSpawner;
        this.messageObserver = messageObserver;
        this.itemSpawner = itemSpawner;
        this.inventory = inventory;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void startNewSession() {
        Session session = Session.createNew(player.getId());

        assert session != null;
        BenefitsRoomSeeder.seedBenefitsRoomForSession(session.getId());
        DailyScrumRoomSeeder.seedDailyScrumRoomForSession(session.getId());
        PlanningRoomSeeder.seedPlanningRoomForSession(session.getId());
        RetrospectiveRoomSeeder.seedRetrospectiveRoomForSession(session.getId());
        ScrumBoardRoomSeeder.seedScrumBoardRoomForSession(session.getId());
        SprintReviewRoomSeeder.seedSprintReviewRoomForSession(session.getId());

        int benefitsLogId = RoomLogHelper.getLevelLogIdByRoomNumber(session.getId(), 1);
        session.setCurrentRoomId(benefitsLogId);

        this.session = session;
        itemSpawner.spawnItems(benefitsLogId, 3);
        inGame = true;
    }

    public void loadSession(int playerId) {
        List<Session> sessions = Session.getAllForPlayer(playerId);
        if (sessions.isEmpty()) {
            System.out.println("There are no saved sessions");
            return;
        }

        System.out.println("Available sessions:");
        for (Session s : sessions) {
            System.out.println(s.toString(true));
        }

        System.out.println("Which session do you want to load? Type in the #ID:");

        boolean chosen = false;

        while (!chosen) {
            int choice = scanner.nextInt();
            Optional<Session> match = sessions.stream()
                    .filter(s -> s.getId() == choice)
                    .findFirst();

            if (match.isPresent()) {
                Session selected = match.get();
                this.session = selected;
                this.inGame = true;
                chosen = true;
                System.out.println("Session loaded: " + selected.toString(false));
            } else {
                System.out.println("Invalid choice try again:");
            }
        }
    }

    public boolean isLoggedIn() {
        return player != null;
    }

    public String getCurrentPrompt() {
        List<Monster> activeMonsters = logService.getActiveMonsters(session);
        if (!activeMonsters.isEmpty()) {
            Monster current = activeMonsters.getFirst();
            return "Monster appears: " + current.getPrompt();
        }

        return getCurrentRoom().getPrompt();
    }

    public void submitAnswer(boolean skip) {
        System.out.print("Your answer: ");
        String answer = scanner.nextLine();

        if (hasActiveMonsters()) {
            handleMonsterAnswer(answer);
            return;
        }
        handleRoomAnswer(answer, skip);
    }

    public String goToNextRoom() {
        if (!canAdvanceToNextRoom()) {
            return "You must complete the current room and defeat all monsters before proceeding.";
        }

        int nextRoomNumber = getNextRoomNumber();
        if (nextRoomNumber == -1) {
            return "No further rooms available.";
        }

        int logId = RoomLogHelper.getLevelLogIdByRoomNumber(session.getId(), nextRoomNumber);
        if (logId == -1) {
            return "Room #" + nextRoomNumber + " not found in session.";
        }

        logService.setStrategy(new RoomLogStrategy());
        RoomLevel nextRoom = logService.extractRoomLevel(logService.loadLevelByLogId(logId));

        session.setCurrentRoomId(logId);
        session.setCurrentQuestionLogId(null);
        session.setCurrentMonsterLogId(null);
        session.save();

        itemSpawner.spawnItems(logId, 3);
        return "Entered Room #" + nextRoomNumber + ":\n" + nextRoom.getPrompt();
    }

    private int getNextRoomNumber() {
        int currentLogId = session.getCurrentRoomId();
        if (currentLogId == -1) return 1;

        logService.setStrategy(new RoomLogStrategy());
        Level level = logService.loadLevelByLogId(currentLogId);
        if (!(level instanceof RoomLevel room)) return -1;

        return room.getRoomNumber() + 1;
    }

    private boolean canAdvanceToNextRoom() {
        logService.setStrategy(new RoomLogStrategy());
        Level room = logService.loadLevelByLogId(session.getCurrentRoomId());
        return session != null
                && session.isActive()
                && room.isCompleted()
                && session.getCurrentMonsterLogId() == -1;
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

    public RoomLevel getCurrentRoom() {
        logService.setStrategy(new RoomLogStrategy());

        int logId = session.getCurrentRoomId();
        if (logId == -1) {
            throw new IllegalStateException("No current room is active for this session.");
        }

        Level level = logService.loadLevelByLogId(logId);
        if (!(level instanceof RoomLevel roomLevel)) {
            throw new IllegalStateException("Current level is not a RoomLevel. Log ID: " + logId);
        }

        return roomLevel;
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

        logService.setStrategy(new MonsterLogStrategy());
        logService.markCurrentLogCompleted(session);

        List<Monster> remaining = logService.getActiveMonsters(session);
        if (!remaining.isEmpty()) {
            Monster next = remaining.getFirst();
            session.setCurrentMonsterLogId(next.getLogId());
            session.save();
            System.out.println("Monster defeated using "+ killMethod + "! " + next.getPrompt());
            return;
        }

        session.setCurrentMonsterLogId(null);
        logService.setStrategy(new QuestionLogStrategy());
        logService.markCurrentLogCompleted(session);
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

        logService.setStrategy(new MonsterLogStrategy());
        logService.markCurrentLogCompleted(session);

        List<Monster> remaining = logService.getActiveMonsters(session);
        if (!remaining.isEmpty()) {
            Monster next = remaining.getFirst();
            session.setCurrentMonsterLogId(next.getLogId());
            session.save();
            System.out.println("Monster defeated! Next monster: " + next.getPrompt());
            return;
        }

        session.setCurrentMonsterLogId(null);
        session.save();
        System.out.println("All monsters defeated! You're back in your room.");
    }

    private void handleRoomAnswer(String answer, boolean skip) {
        logService.setStrategy(new RoomLogStrategy());
        Level level = logService.loadLevelByLogId(session.getCurrentRoomId());

        if (!(level instanceof RoomLevel roomLevel)) {
            System.out.println("This level does not support room-style interaction.");
            return;
        }

        roomLevel.setLogId(session.getCurrentRoomId());
        session.setCurrentQuestionLogId(roomLevel.getQuestionLogId());

        boolean correct = roomLevel.checkAnswer(answer);
        if (correct || skip) {
            logService.setStrategy(new QuestionLogStrategy());
            logService.markCurrentLogCompleted(session);

            RoomLevel reloadedRoom = getCurrentRoom();
            session.save();

            roomLevel = reloadedRoom;

            System.out.println("Correct! You've answered the question successfully.");
            if (roomLevel.isCompleted()) {
                System.out.println("All questions in this room are answered.");
            } else {
                System.out.println("Next question: " + roomLevel.getPrompt());
            }

            session.save();
            return;
        }

        messageObserver.clear();
        monsterSpawner.addObserver(messageObserver);

        List<Monster> monsters = monsterSpawner.spawnMonstersForRoom(session);
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
            return generateHint(monster.getAnswer(), monster.getHint());
        }

        if (session.getCurrentRoomId() != -1) {
            try {
                RoomLevel currentRoom = getCurrentRoom();
                List<Question> remainingQuestions = currentRoom.getRemainingQuestions();

                if (!remainingQuestions.isEmpty()) {
                    Question currentQuestion = remainingQuestions.get(0);
                    return generateHint(currentQuestion.getAnswer(), currentQuestion.getHint());
                } else {
                    return "All questions in this room have been answered.";
                }
            } catch (IllegalStateException e) {
                return "Cannot get hint for current room: " + e.getMessage();
            }
        }

        return "No active question or monster to get hint for.";
    }

    private String generateHint(String answer, String hint) {
        HintContext hintContext = new HintContext();

        if (hint != null && !hint.isBlank()) {
            hintContext.setStrategy(new PredefinedHintStrategy());
        } else {
            hintContext.setStrategy(new RandomObfuscatedHintStrategy(0.3));
        }

        return hintContext.getHint(answer, hint);
    }

    public void printRoomOverview() {
        int logId = session.getCurrentRoomId();
        if (logId == -1) {
            System.out.println("Er is geen actieve kamer.");
            return;
        }

        logService.setStrategy(new RoomLogStrategy());
        RoomLevel roomLevel;
        try {
            Level level = logService.loadLevelByLogId(logId);
            roomLevel = logService.extractRoomLevel(level);
        } catch (IllegalStateException e) {
            System.out.println("De huidige kamer is geen room-level.");
            return;
        }

        List<Question> remaining = roomLevel.getRemainingQuestions();
        if (remaining.isEmpty()) {
            System.out.println("Alle vragen in kamer " + roomLevel.getRoomNumber() + " zijn beantwoord.");
            return;
        }

        System.out.println("\nOverzicht van openstaande vragen in kamer " + roomLevel.getRoomNumber() + ":");
        int index = 1;
        for (Question q : remaining) {
            System.out.printf("  %d. %s%n", index++, q.getQuestion());
        }
    }

    public void dropItem(int itemId) {
        inventory.drop(itemId, session);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void skipCurrentQuestion() {
        if (hasActiveMonsters()) {
            Monster currentMonster = getCurrentActiveMonster();
            if (currentMonster != null) {
                defeatCurrentMonster("skip joker");
            }
            return;
        }

        handleRoomAnswer("", true);
    }

    public String skipQuestion() {
        if (session == null || !session.isActive()) {
            return "No active session.";
        }

        skipCurrentQuestion();

        try {
            RoomLevel currentRoom = getCurrentRoom();
            if (currentRoom.isCompleted()) {
                return goToNextRoom();
            } else {
                return "Question skipped. Next question: " + currentRoom.getPrompt();
            }
        } catch (IllegalStateException e) {
            return goToNextRoom();
        }
    }
}
