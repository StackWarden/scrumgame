package org.scrumgame.classes;

import org.scrumgame.database.models.Session;
import org.scrumgame.services.QuestionService;

import java.util.List;

public class Room extends Level {
    private Question question;

    public Room(Question question) {
        super();
        this.question = question;
    }

    public static Room createRoom(Session session) {
        List<Question> questions = QuestionService.generateQuestions(session, 1);
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions available for random room.");
        }
        Question question = questions.getFirst();
        return new Room(question);
    }

    @Override
    public String getPrompt() {
        return question != null ? question.getQuestion() : "Room already cleared.";
    }

    @Override
    public boolean checkAnswer(String answer) {
        return question != null && question.checkAnswer(answer);
    }

    @Override
    public String getAnswer() {
        return question != null ? question.getAnswer() : "";
    }

    @Override
    public Question getQuestion() {
        return question != null ? question : null;
    }
}
