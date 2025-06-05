package org.scrumgame.game;

import org.scrumgame.classes.Monster;
import org.scrumgame.classes.Question;
import org.scrumgame.classes.Room;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.QuestionService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class GameContext {

    private final Random random = new Random();
    private final List<String> monsterNames = new ArrayList<>(Arrays.asList(
            "Goblin", "Orc", "Troll", "Demon", "Dragon"
    ));

    public GameContext() {
        // TODO: Optional init if needed
    }

    public Question generateRoomQuestion(int roomNumber) {
        // TODO: Generate or fetch a question for the room
        return null;
    }

    public List<Monster> spawnMonstersForRoom(Session session) {
        List<Monster> monsters = new ArrayList<>();

        List<Question> questions = QuestionService.generateQuestions(session, 3);
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String name = "Monster " + (i + 1);
            monsters.add(new Monster(q));
        }

        return monsters;
    }

    public String getRandomMonsterName() {
        return monsterNames.get(random.nextInt(monsterNames.size()));
    }
}
