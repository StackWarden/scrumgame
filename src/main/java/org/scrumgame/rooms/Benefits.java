package org.scrumgame.rooms;

import org.scrumgame.classes.Question;
import org.scrumgame.classes.RoomLevel;
import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.services.LogService;

import java.util.List;

public class Benefits extends RoomLevel {

    public Benefits(Session session, LogService logService) {
        setLogId(session.getCurrentRoomId());
        setQuestions(fetchQuestionsForLevel(session, logService));
        assert !getQuestions().isEmpty();
        setQuestion(getQuestions().getFirst().getQuestions().getFirst());
        setRoomNumber(1);
    }

    public Benefits(List<QuestionLog> questions) {
        prepareRoom(questions, 1);
    }

    @Override
    public String getPrompt() {
        Question question = getQuestion();
        return question != null ? question.getQuestion() : "All questions answered!";
    }
}
