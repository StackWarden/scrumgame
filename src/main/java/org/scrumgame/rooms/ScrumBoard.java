package org.scrumgame.rooms;

import org.scrumgame.classes.Question;
import org.scrumgame.classes.RoomLevel;
import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;

import java.util.List;

public class ScrumBoard extends RoomLevel {
    public ScrumBoard(Session session, LogService logService) {
        setLogId(session.getCurrentRoomId());
        setQuestions(fetchQuestionsForLevel(session, logService));
        assert !getQuestions().isEmpty();
        setQuestion(getQuestions().getFirst().getQuestions().getFirst());
        setRoomNumber(5);
    }

    public ScrumBoard(List<QuestionLog> questions) {
        prepareRoom(questions, 5);
    }

    @Override
    public String getPrompt() {
        Question currentQuestion = getQuestion();
        return currentQuestion != null ? currentQuestion.getQuestion() : "All questions answered!";
    }
}
