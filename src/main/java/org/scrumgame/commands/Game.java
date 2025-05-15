package org.scrumgame.commands;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.AnswerResult;
import org.scrumgame.database.models.Session;
import org.scrumgame.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@ShellComponent
public class Game {

    private final GameService gameService;

    @Autowired
    public Game(GameService gameService) {
        this.gameService = gameService;
    }

    @ShellMethod(key = "start-game", value = "Start a game. Provide a session ID or leave blank to create one.")
    public String startGame(@ShellOption(defaultValue = "0", help = "Optional session ID") int sessionId) {
        boolean started;
        int usedSessionId = sessionId;

        if (sessionId == 0) {
            Session newSession = Session.createNewSession();
            if (newSession == null) return "Failed to create a new session.";
            usedSessionId = newSession.getId();
            started = gameService.startGame(usedSessionId);
        } else {
            started = gameService.startGame(sessionId);
        }

        if (!started) {
            return "Could not start game.";
        }

        Room room = gameService.getCurrentRoom();
        if (room != null && room.getQuestion() != null) {
            Question q = room.getQuestion();
            return "Game started for session " + usedSessionId + "\nQuestion: " + q.getQuestion();
        }

        return "Game started for session " + usedSessionId + ", but no question found.";
    }

    @ShellMethod(key = "next-room", value = "Move to the next room if the current one is completed")
    public String nextRoom() {
        try {
            if (!gameService.isReadyForNextRoom()) {
                return "Cannot move to next room: current room not completed or monster still active.";
            }

            Room nextRoom = gameService.advanceToNextRoom();
            Question nextQ = nextRoom.getQuestion();

            return "Moved to next room.\nNew Question: " + nextQ.getQuestion();

        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }


    @ShellMethod(key = "spawn-monster", value = "Spawn a monster encounter")
    public String spawnMonster() {
        gameService.spawnMonster();
        return "👾 Monster spawned!";
    }

    @ShellMethod(key = "finish-monster", value = "Finish monster fight")
    public String finishMonster(@ShellOption(help = "Was the monster defeated?") boolean defeated) {
        gameService.finishMonsterFight(defeated);
        return defeated ? "Monster defeated!" : "Monster escaped!";
    }

    @ShellMethod(key = "answer", value = "Answer the current question (Room or Monster)")
    public String answer(@ShellOption(help = "Answer to the current question") String answer) {
        Level currentLevel = gameService.getCurrentLevel();
        Question question = (currentLevel instanceof Room r) ? r.getQuestion()
                : (currentLevel instanceof Monster m) ? m.getQuestions().getFirst()
                : null;

        if (question == null) return "No active question found.";

        List<Map.Entry<Question, String>> answers = List.of(Map.entry(question, answer));
        AnswerResult result = gameService.checkAnswerAndHandle(currentLevel, answers);

        if (result.isCorrect()) {
            return "Correct! \nUse 'next-room' to go to the next level.";
        } else if (result.getSpawnedMonster() != null) {
            StringBuilder output = new StringBuilder("Wrong! A monster has appeared!\n");
            for (Question q : result.getSpawnedMonster().getQuestions()) {
                output.append(" - [").append(q.getId()).append("] ").append(q.getQuestion()).append("\n");
            }
            return output.toString();
        } else {
            return "You failed to defeat the monster. Game over.";
        }
    }
}
