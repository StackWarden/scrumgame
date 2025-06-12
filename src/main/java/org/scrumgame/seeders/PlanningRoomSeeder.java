package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.MultipleChoiceQuestion;
import org.scrumgame.questions.RiddleQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlanningRoomSeeder extends BaseSeeder {
    private static final int ROOM_NUMBER = 3;
    private static final String LEVEL_TYPE = "planning";

    private final List<Question> QUESTIONS = generateRandomQuestions();

    public PlanningRoomSeeder(int sessionId) {
        super(sessionId);
    }

    private List<Question> generateRandomQuestions() {
        List<BaseQuestion> baseQuestions = List.of(
                new BaseQuestion(-1, "What is the purpose of the Sprint Planning meeting?", "To plan the upcoming Sprint", "Think about setting goals."),
                new BaseQuestion(-1, "Who defines the Sprint Goal?", "The Scrum Team", "It's a team effort."),
                new BaseQuestion(-1, "What is the input to the Sprint Planning meeting?", "The Product Backlog", "Where do tasks come from?"),
                new BaseQuestion(-1, "How long should Sprint Planning last for a one-month Sprint?", "8 hours", "Scrum Guide gives this number."),
                new BaseQuestion(-1, "What is the outcome of Sprint Planning?", "Sprint Backlog", "It's the set of planned work.")
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