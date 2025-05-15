package org.scrumgame.classes;

import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.LogStrategy;
import org.scrumgame.strategies.MonsterLogStrategy;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Monster extends Level {
    private List<Question> questions;
    private boolean defeated;

    public boolean isDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    @Override
    public LogService getLogService() {
        LogService logService = new LogService();
        logService.setStrategy(new MonsterLogStrategy());
        return logService;
    }

    @Override
    public LogStrategy getLogStrategy() {
        return new MonsterLogStrategy();
    }

    @Override
    public Room nextLevel(Session session) {
        return new Room().nextLevel(session);
    }

    @Override
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @Override
    public List<Map.Entry<Question, Boolean>> checkAnswers(List<Map.Entry<Question, String>> answers) {
        return questions.stream()
                .map(q -> {
                    String provided = answers.stream()
                            .filter(e -> e.getKey().getId() == q.getId())
                            .map(Map.Entry::getValue)
                            .findFirst()
                            .orElse("");

                    System.out.println("Monster: checking QID " + q.getId());
                    System.out.println(" - Provided: '" + provided + "' | Expected: '" + q.getAnswer() + "'");

                    return Map.entry(q, q.checkAnswer(provided));
                })
                .toList();
    }
}
