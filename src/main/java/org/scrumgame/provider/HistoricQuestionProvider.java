package org.scrumgame.provider;

import org.scrumgame.repositories.DatabaseQuestionRepository;
import org.scrumgame.classes.Question;
import org.scrumgame.database.models.MonsterLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.GameLog;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.MonsterLogStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class HistoricQuestionProvider {
    private final LogService logService;

    public HistoricQuestionProvider(LogService logService) {
        this.logService = logService;
    }

    public List<Question> getFallbackQuestions(Session session, Set<Integer> usedIds, int needed) {
        List<Question> fallback = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        logService.setStrategy(new MonsterLogStrategy());
        for (GameLog log : logService.getLogs(session)) {
            if (log instanceof MonsterLog monsterLog) {
                for (Question q : monsterLog.questions()) {
                    if (seen.add(q.getId()) && !usedIds.contains(q.getId()) && fallback.size() < needed) {
                        fallback.add(q);
                    }
                }
            }
        }
        if (fallback.size() < needed) {
            fallback.addAll(
                    new DatabaseQuestionRepository()
                            .findHistoricQuestions(session, usedIds, needed - fallback.size())
            );
        }
        return fallback;
    }
}