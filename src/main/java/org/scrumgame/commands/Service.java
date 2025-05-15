package org.scrumgame.commands;

import org.scrumgame.classes.GameLog;
import org.scrumgame.classes.Level;
import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.services.QuestionService;
import org.scrumgame.strategies.MonsterLogStrategy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class Service {

    @ShellMethod(key = "test-monster-logs", value = "Display all MonsterLogs for a given session ID")
    public String testMonsterLogs(@ShellOption(help = "Session ID to fetch logs for") int sessionId) {
        // Fetch session from DB
        Session session = Session.getSessionById(sessionId);
        if (session == null) {
            return "No session found with ID " + sessionId + ".";
        }

        // Prepare LogService with the MonsterLogStrategy
        LogService logService = new LogService();
        logService.setStrategy(new MonsterLogStrategy());

        // Fetch logs (as GameLog, then filter for MonsterLog)
        List<? extends GameLog> gameLogs = logService.getLogs(session);
        List<MonsterLog> monsterLogs = new ArrayList<>();

        for (GameLog log : gameLogs) {
            if (log instanceof MonsterLog monsterLog) {
                monsterLogs.add(monsterLog);
            }
        }

        if (monsterLogs.isEmpty()) {
            return "No MonsterLogs found for session ID " + sessionId + ".";
        }

        StringBuilder output = new StringBuilder("MonsterLogs for session ID " + sessionId + ":\n");
        for (MonsterLog log : monsterLogs) {
            output.append(" - Defeated: ").append(log.isDefeated())
                    .append(" | Questions: ").append(log.getQuestions().size())
                    .append("\n");
        }

        return output.toString();
    }


    @ShellMethod(key = "test-level-questions", value = "Create unique questions for a Level based on session")
    public String testLevelQuestions(
            @ShellOption(help = "Session ID") int sessionId,
            @ShellOption(help = "Amount of questions") int amount
    ) {
        Session session = Session.getSessionById(sessionId);
        if (session == null) {
            return "Session with ID " + sessionId + " not found.";
        }

        Level level = new Monster();
        List<Question> questions = QuestionService.generateQuestions(session, amount);
        level.setQuestions(questions);

        if (questions.isEmpty()) {
            return "No questions available (all previously used or none in DB).";
        }

        StringBuilder output = new StringBuilder("Made questions for session " + sessionId + ":\n");
        for (Question q : questions) {
            output.append("- [").append(q.getId()).append("] ").append(q.getQuestion()).append("\n");
        }

        return output.toString();
    }
}