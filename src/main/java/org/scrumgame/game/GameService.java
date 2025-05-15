package org.scrumgame.game;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.AnswerResult;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.services.QuestionService;
import org.scrumgame.strategies.MonsterLogStrategy;
import org.scrumgame.strategies.RoomLogStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GameService {

    private final GameContext context;

    public GameService(GameContext context) {
        this.context = context;
    }

    public boolean startGame(int sessionId) {
        Session session = Session.getSessionById(sessionId);
        if (session == null || session.isGameOver()) return false;

        if (session.getCurrentLevel() == 0) {
            Room firstRoom = createRoom(session);
            int lastAdd = logLevelAndReturnId(session, firstRoom);
            session.setRoom(lastAdd);
            System.out.println(" - [" + firstRoom.getQuestion().getId() + "] " + firstRoom.getQuestion().getQuestion());
        }

        context.setSession(session);
        return true;
    }

    public void finishMonsterFight(boolean defeated) {
        Session session = context.getSession();

        Monster currentMonster = session.getCurrentMonster();
        if (currentMonster != null) {
            currentMonster.getLogService().markCurrentLogCompleted(session);
        }

        session.finishMonsterFight(defeated);
    }

    public void setCurrentMonsterLogId(int logId) {
        context.getSession().setCurrentMonsterLogId(logId);
    }

    public void markGameOver() {
        context.getSession().markGameOver();
    }

    public AnswerResult checkAnswerAndHandle(Level level, List<Map.Entry<Question, String>> answers) {
        Session session = context.getSession();

        if (level instanceof Room room) {
            Question question = room.getQuestion();

            String providedAnswer = answers.stream()
                    .filter(entry -> entry.getKey().getId() == question.getId())
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse("");

            boolean correct = question.checkAnswer(providedAnswer);

            if (correct) {
                room.getLogService().markCurrentLogCompleted(session);
                session.completeCurrentRoom();
                return new AnswerResult(true, null);
            } else {
                Monster monster = spawnMonster();
                return new AnswerResult(false, monster);
            }

        } else if (level instanceof Monster monster) {
            List<Map.Entry<Question, Boolean>> results = monster.checkAnswers(answers);
            boolean allCorrect = results.stream().allMatch(Map.Entry::getValue);

            if (allCorrect) {
                monster.getLogService().markCurrentLogCompleted(session);
                session.finishMonsterFight(true);
                return new AnswerResult(true, null);
            } else {
                session.finishMonsterFight(false);
                session.markGameOver();
                return new AnswerResult(false, null);
            }
        }

        return new AnswerResult(false, null);
    }


    public Room advanceToNextRoom() {
        Session session = context.getSession();
        Room nextRoom = createRoom(session);
        int lastAdd = logLevelAndReturnId(session, nextRoom);
        session.setRoom(lastAdd);

        System.out.println("Generated question(s) for the new room:");
        System.out.println(" - [" + nextRoom.getQuestion().getId() + "] " + nextRoom.getQuestion().getQuestion());

        return nextRoom;
    }


    public Monster spawnMonster() {
        Session session = context.getSession();

        Monster monster = new Monster();
        List<Question> questions = QuestionService.generateQuestions(session, 3);
        monster.setQuestions(questions);

        int lastAdd = logLevelAndReturnId(session, monster);
        session.setCurrentMonsterLogId(lastAdd);

        return monster;
    }

    public Monster createMonster(Session session) {
        Monster monster = new Monster();
        monster.setQuestions(QuestionService.generateQuestions(session, 3));
        return monster;
    }

    public Room createRoom(Session session) {
        List<Question> generated = QuestionService.generateQuestions(session, 1);
        if (generated.isEmpty()) {
            throw new IllegalStateException("No questions available to create a room.");
        }

        Room room = new Room();
        room.setQuestions(generated);
        return room;
    }

    public void logLevel(Session session, Level level) {
        LogService logService = new LogService();
        logService.setStrategy(level.getLogStrategy());
        logService.executeLog(session, level);
    }

    public int logLevelAndReturnId(Session session, Level level) {
        LogService logService = new LogService();

        if (level instanceof Monster monster) {
            MonsterLogStrategy strategy = new MonsterLogStrategy();
            logService.setStrategy(strategy);
            logService.executeLog(session, monster);
            return strategy.getLastInsertedLogId();

        } else if (level instanceof Room room) {
            RoomLogStrategy strategy = new RoomLogStrategy();
            logService.setStrategy(strategy);
            logService.executeLog(session, room);
            return strategy.getLastInsertedLogId();
        }

        return -1;
    }

    public Level getCurrentLevel() {
        Session session = context.getSession();
        Monster monster = session.getCurrentMonster();
        return (monster != null) ? monster : session.getCurrentRoom();
    }

    public boolean isReadyForNextRoom() {
        Session session = context.getSession();
        if (session.isGameOver()) return false;

        Monster activeMonster = session.getCurrentMonster();
        if (activeMonster != null) return false;

        Room currentRoom = session.getCurrentRoom();
        return currentRoom == null;
    }

    public Room getCurrentRoom() {
        Session session = context.getSession();
        return session.getCurrentRoom();
    }
}
