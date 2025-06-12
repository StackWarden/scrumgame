package org.scrumgame.rooms;

import org.scrumgame.classes.Question;
import org.scrumgame.classes.RoomLevel;
import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;

import java.util.List;

public class Retrospective extends RoomLevel {
    public Retrospective(Session session, LogService logService) {
        setLogId(session.getCurrentRoomId());
        setQuestions(fetchQuestionsForLevel(session, logService));
        assert !getQuestions().isEmpty();
        setQuestion(getQuestions().getFirst().getQuestions().getFirst());
        setRoomNumber(4);
    }

    public Retrospective(List<QuestionLog> questions) {
        prepareRoom(questions, 4);
    }

    @Override
    public String getPrompt() {
        Question currentQuestion = getQuestion();
        return currentQuestion != null ? currentQuestion.getQuestion() : "All questions answered!";
    }
}
