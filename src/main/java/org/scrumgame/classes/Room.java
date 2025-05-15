package org.scrumgame.classes;

import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.services.QuestionService;
import org.scrumgame.strategies.LogStrategy;
import org.scrumgame.strategies.RoomLogStrategy;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Room extends Level {

    public Question question;
    public boolean completed;

    public Room(Question question, boolean completed) {
        super();
        this.question = question;
        this.completed = completed;
    }

    public Room() {
        super();
    }

    @Override
    public LogStrategy getLogStrategy() {
        return new RoomLogStrategy();
    }

    @Override
    public LogService getLogService() {
        RoomLogStrategy roomLogStrategy = new RoomLogStrategy();
        LogService logService = new LogService();
        logService.setStrategy(roomLogStrategy);
        return logService;
    }

    public Question getQuestion() {
        return question;
    }

    @Override
    public void setQuestions(List<Question> questions) {
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Room must have at least one question");
        }
        this.question = questions.getFirst();
    }

    @Override
    public Room nextLevel(Session session) {
        Room nextRoom = new Room();
        List<Question> newQuestions = QuestionService.generateQuestions(session, 1);
        nextRoom.setQuestions(newQuestions);
        return nextRoom;
    }

    @Override
    public List<Map.Entry<Question, Boolean>> checkAnswers(List<Map.Entry<Question, String>> answers) {
        if (question == null || answers.isEmpty()) return List.of();

        String userAnswer = answers.stream()
                .filter(entry -> entry.getKey().getId() == question.getId())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("");

        System.out.println("Room: matching question ID = " + question.getId());
        System.out.println("User answer = '" + userAnswer + "'");

        boolean isCorrect = question.checkAnswer(userAnswer);
        return List.of(new AbstractMap.SimpleEntry<>(question, isCorrect));
    }

    @Override
    public String toString() {
        if (question != null) {
            return "Room with question: [" + question.getId() + "] " + question.getQuestion();
        } else {
            return "Empty Room";
        }
    }
}
