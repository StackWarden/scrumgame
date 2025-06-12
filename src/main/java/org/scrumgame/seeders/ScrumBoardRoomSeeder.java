package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.MultipleChoiceQuestion;
import org.scrumgame.questions.RiddleQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ScrumBoardRoomSeeder extends BaseSeeder {
    private static final int ROOM_NUMBER = 5;
    private static final String LEVEL_TYPE = "scrumboard";

    private final List<Question> QUESTIONS = generateRandomQuestions();

    public ScrumBoardRoomSeeder(int sessionId) {
        super(sessionId);
    }

    private List<Question> generateRandomQuestions() {
        List<BaseQuestion> baseQuestions = List.of(
                new BaseQuestion(-1, "What is a Scrum Board used for?", "To visualize the progress of tasks", "Think about transparency."),
                new BaseQuestion(-1, "What are typical columns on a Scrum Board?", "To Do, In Progress, Done", "They represent status."),
                new BaseQuestion(-1, "Who updates the Scrum Board?", "The Development Team", "They manage the work."),
                new BaseQuestion(-1, "How often should the Scrum Board be updated?", "Daily", "It reflects the latest status."),
                new BaseQuestion(-1, "What is the benefit of using a physical or digital Scrum Board?", "Improved visibility and collaboration", "It helps teams stay aligned.")
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