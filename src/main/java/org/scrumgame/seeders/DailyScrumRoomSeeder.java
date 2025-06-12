package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.MultipleChoiceQuestion;
import org.scrumgame.questions.RiddleQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DailyScrumRoomSeeder extends BaseSeeder {
    private static final int ROOM_NUMBER = 2;
    private static final String LEVEL_TYPE = "daily_scrum";

    private final List<Question> QUESTIONS = generateRandomQuestions();

    public DailyScrumRoomSeeder(int sessionId) {
        super(sessionId);
    }

    private List<Question> generateRandomQuestions() {
        List<BaseQuestion> baseQuestions = List.of(
                new BaseQuestion(-1, "Wat is het doel van de Daily Scrum?", "Synchronisatie en planning", "Denk aan samenwerking."),
                new BaseQuestion(-1, "Wie neemt deel aan de Daily Scrum?", "Het ontwikkelteam", "Wie werkt dagelijks samen?"),
                new BaseQuestion(-1, "Wat is de maximale duur van een Daily Scrum?", "15 minuten", "Het is kort."),
                new BaseQuestion(-1, "Wanneer vindt de Daily Scrum plaats?", "Elke werkdag", "Denk aan frequentie."),
                new BaseQuestion(-1, "Wat bespreekt men tijdens de Daily Scrum?", "Voortgang en obstakels", "Wat helpt het team vooruit?")
        );

        List<Question> randomQuestions = new ArrayList<>();
        for (BaseQuestion baseQuestion : baseQuestions) {
            int randomType = ThreadLocalRandom.current().nextInt(2);
            if (randomType == 0) {
                randomQuestions.add(new MultipleChoiceQuestion(
                        baseQuestion.getId(),
                        baseQuestion.getQuestion(),
                        baseQuestion.getAnswer(),
                        baseQuestion.getHint()
                ));
            } else {
                randomQuestions.add(new RiddleQuestion(
                        baseQuestion.getId(),
                        baseQuestion.getQuestion(),
                        baseQuestion.getAnswer(),
                        baseQuestion.getHint()
                ));
            }
        }
        return randomQuestions;
    }

    public void seed(int sessionId) {
        insertDataToDatabase(sessionId, ROOM_NUMBER, LEVEL_TYPE, QUESTIONS);
    }
}