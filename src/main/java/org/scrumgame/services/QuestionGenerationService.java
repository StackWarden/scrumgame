package org.scrumgame.services;

import org.scrumgame.repositories.QuestionRepository;
import org.scrumgame.classes.Question;
import org.scrumgame.database.models.Session;
import org.scrumgame.provider.HistoricQuestionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionGenerationService {
    private final QuestionRepository questionRepo;
    private final HistoricQuestionProvider fallbackQuestions;

    @Autowired
    public QuestionGenerationService(QuestionRepository questionRepo,
                                     HistoricQuestionProvider fallbackQuestions) {
        this.questionRepo = questionRepo;
        this.fallbackQuestions = fallbackQuestions;
    }

    public List<Question> generateQuestions(Session session, int amount) {
        Set<Integer> usedIds = questionRepo.findUsedQuestionIds(session);
        List<Question> newQuestions = questionRepo.findNewQuestions(usedIds, amount);
        if (newQuestions.size() < amount) {
            newQuestions.addAll(
                    fallbackQuestions.getFallbackQuestions(session, usedIds, amount - newQuestions.size())
            );
        }
        return newQuestions;
    }
}
