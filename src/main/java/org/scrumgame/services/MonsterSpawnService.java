package org.scrumgame.services;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.database.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonsterSpawnService {
    private final QuestionGenerationService questionService;

    @Autowired
    public MonsterSpawnService(QuestionGenerationService questionService) {
        this.questionService = questionService;
    }

    public List<Monster> spawnMonstersForRoom(Session session) {
        int questionCount = 3;
        List<Monster> monsters = new ArrayList<>();

        List<Question> questions = questionService.generateQuestions(session, questionCount);
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String name = "Monster " + (i + 1);

            Monster m = new Monster(q);
            monsters.add(m);
        }

        return monsters;
    }
}