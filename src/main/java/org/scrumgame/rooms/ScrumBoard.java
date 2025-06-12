package org.scrumgame.rooms;

import org.scrumgame.classes.Level;
import org.scrumgame.classes.Question;
import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.GameLog;
import org.scrumgame.interfaces.RoomLevel;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.QuestionLogStrategy;

import java.util.ArrayList;
import java.util.List;

public class ScrumBoard extends Level implements RoomLevel {

    private final List<QuestionLog> questions = new ArrayList<>();
    private Question currentQuestion;
    private int logId;
    private int roomNumber;

    public ScrumBoard(Session session, LogService logService) {
        this.logId = session.getCurrentRoomId();
        this.questions.addAll(fetchQuestionsForLevel(session, logService));
        assert !this.questions.isEmpty();
        this.currentQuestion = this.questions.getFirst().questions().getFirst();
        setRoomNumber(5);
    }

    public ScrumBoard(List<QuestionLog> questions) {
        this.questions.addAll(
                questions.stream()
                        .filter(q -> !q.isCompleted())
                        .toList()
        );

        if (this.questions.isEmpty()) {
            setCompleted(true);
            setRoomNumber(5);
            return;
        }
        this.currentQuestion = this.questions.getFirst().questions().getFirst();
        setRoomNumber(5);
    }

    @Override
    public String getPrompt() {
        return currentQuestion != null ? currentQuestion.getQuestion() : "All questions answered!";
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (currentQuestion == null) return false;

        boolean correct = currentQuestion.getAnswer().trim().equalsIgnoreCase(answer.trim());
        if (correct) {
            questions.removeFirst();

            if (!questions.isEmpty()) {
                currentQuestion = questions.getFirst().questions().getFirst();
            } else {
                currentQuestion = null;
            }
        }

        return correct;
    }

    @Override
    public String getAnswer() {
        return "";
    }

    @Override
    public boolean isCompleted() {
        return questions.isEmpty();
    }

    @Override
    public Question getQuestion() {
        return currentQuestion;
    }

    @Override
    public int getQuestionLogId() {
        assert !questions.isEmpty();
        return questions.getFirst().getId();
    }

    @Override
    public List<Question> getRemainingQuestions() {
        List<Question> result = new ArrayList<>();
        for (QuestionLog log : questions) {
            result.add(log.questions().getFirst());
        }
        return result;
    }

    @Override
    public int getRoomNumber() {
        return this.roomNumber;
    }

    @Override
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public void setLogId(int logId) {
        this.logId = logId;
    }

    @Override
    public int getLogId() {
        return logId;
    }

    private static List<QuestionLog> fetchQuestionsForLevel(Session session, LogService logService) {
        logService.setStrategy(new QuestionLogStrategy());

        List<? extends GameLog> logs = logService.getLogs(session);

        return logs.stream()
                .filter(log -> log instanceof QuestionLog qLog
                        && qLog.getLevelLogId() == session.getCurrentRoomId())
                .map(log -> (QuestionLog) log)
                .toList();
    }
}
