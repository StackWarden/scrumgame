package org.scrumgame.repositories;

import org.scrumgame.classes.Question;
import org.scrumgame.database.models.Session;

import java.util.List;
import java.util.Set;

public interface QuestionRepository {
    Set<Integer> findUsedQuestionIds(Session session);
    List<Question> findNewQuestions(Set<Integer> excludeIds, int limit);
    List<Question> findHistoricQuestions(Session session, Set<Integer> excludeIds, int limit);
}
