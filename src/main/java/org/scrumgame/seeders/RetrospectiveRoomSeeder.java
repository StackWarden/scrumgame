package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.MultipleChoiceQuestion;
import org.scrumgame.questions.RiddleQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RetrospectiveRoomSeeder extends BaseSeeder {
    private static final int ROOM_NUMBER = 4;
    private static final String LEVEL_TYPE = "retrospective";

    private final List<Question> QUESTIONS = generateRandomQuestions();

    public RetrospectiveRoomSeeder(int sessionId) {
        super(sessionId);
    }

    private List<Question> generateRandomQuestions() {
        List<BaseQuestion> baseQuestions = List.of(
                new BaseQuestion(-1, "What is the purpose of a Sprint Retrospective?", "To inspect and improve team processes", "Focus on improvement."),
                new BaseQuestion(-1, "Who participates in the Sprint Retrospective?", "The Scrum Team", "The whole team reflects."),
                new BaseQuestion(-1, "How often should a Sprint Retrospective occur?", "At the end of each Sprint", "It's part of every Sprint."),
                new BaseQuestion(-1, "What is an example of an outcome of a Sprint Retrospective?", "Action items for improvement", "Think about actionable goals."),
                new BaseQuestion(-1, "What format can a Sprint Retrospective take?", "It can be structured or informal", "Flexibility is key.")
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