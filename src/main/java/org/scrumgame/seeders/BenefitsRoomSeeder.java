package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.RiddleQuestion;
import org.scrumgame.questions.MultipleChoiceQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BenefitsRoomSeeder extends BaseSeeder {
    private final int ROOM_NUMBER = 1;
    private final String LEVEL_TYPE = "benefits";

    // Define static questions that are randomly assigned to different question types
    private final List<Question> QUESTIONS = generateRandomQuestions();

    public BenefitsRoomSeeder(int sessionId) {
        super(sessionId);
    }

    private List<Question> generateRandomQuestions() {
        // Basic question templates
        List<BaseQuestion> baseQuestions = List.of(
                new BaseQuestion(-1, "What is one benefit of using Scrum?", "Improved communication", "Think about teamwork."),
                new BaseQuestion(-1, "How does Scrum encourage adaptability?", "By using short iterations", "Sprints are useful here."),
                new BaseQuestion(-1, "Why is feedback important in Scrum?", "It allows for continuous improvement", "Think about retrospectives."),
                new BaseQuestion(-1, "Who inspects the product increment in Scrum?", "The stakeholders", "They review progress at Sprint Review."),
                new BaseQuestion(-1, "What is one outcome of regular Daily Scrums?", "Better team alignment", "It keeps everyone on the same page.")
        );

        // Populate the list with randomized question types
        List<Question> randomQuestions = new ArrayList<>();
        for (BaseQuestion baseQuestion : baseQuestions) {
            // Random selection of type (0 = MultipleChoiceQuestion, 1 = RiddleQuestion)
            int randomType = ThreadLocalRandom.current().nextInt(2);
            if (randomType == 0) {
                // Create a MultipleChoiceQuestion based on the BaseQuestion data
                randomQuestions.add(new MultipleChoiceQuestion(
                        baseQuestion.getId(),
                        baseQuestion.getQuestion(),
                        baseQuestion.getAnswer(),
                        baseQuestion.getHint()
                ));
            } else {
                // Create a RiddleQuestion based on the BaseQuestion data
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