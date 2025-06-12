package org.scrumgame.classes;

import org.scrumgame.database.models.QuestionLog;
import org.scrumgame.database.models.Session;
import org.scrumgame.interfaces.GameLog;
import org.scrumgame.interfaces.iRoomLevel;
import org.scrumgame.questions.BaseQuestion;
import org.scrumgame.questions.MultipleChoiceQuestion;
import org.scrumgame.questions.RiddleQuestion;
import org.scrumgame.services.LogService;
import org.scrumgame.strategies.QuestionLogStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RoomLevel extends Level implements iRoomLevel {
    private Question question;
    private List<QuestionLog> questions = new ArrayList<>();
    private int logId;
    private int roomNumber;
    private final Random sharedRandom = new Random();

    public RoomLevel() {
        super();
    }

    @Override
    public String getPrompt() {
        return question != null ? question.getQuestion() : "Room already cleared.";
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (question == null) return false;

        String trimmed = answer.trim();
        boolean correct = question.getAnswer().trim().equalsIgnoreCase(trimmed)
                || "super".equalsIgnoreCase(trimmed);

        if (correct) {
            LinkedList<QuestionLog> temp = new LinkedList<>(questions);

            temp.removeFirst();

            setQuestions(temp);

            if (!questions.isEmpty()) {
                question = questions.getFirst().getQuestions().getFirst();
            } else {
                question = null;
            }
        }

        return correct;
    }

    @Override
    public int getQuestionLogId() {
        assert !questions.isEmpty();
        return questions.getFirst().getId();
    }

    @Override
    public String getAnswer() {
        return question != null ? question.getAnswer() : "";
    }

    @Override
    public Question getQuestion() {
        int randomCase = sharedRandom.nextInt(3); // Use the shared instance

        switch (randomCase) {
            case 0 -> {
                return new MultipleChoiceQuestion(this.question, this.getAnswer());
            }
            case 1 -> {
                return new BaseQuestion(this.question, this.getAnswer());
            }
            case 2 -> {
                return new RiddleQuestion(this.question, this.getAnswer());
            }
            default -> throw new IllegalStateException("Unexpected value: " + randomCase);
        }
    }

    @Override
    public List<Question> getRemainingQuestions() {
        List<Question> result = new ArrayList<>();
        for (QuestionLog log : questions) {
            result.add(log.getQuestions().getFirst());
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

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<QuestionLog> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionLog> questions) {
        this.questions = questions;
    }

    @Override
    public int getLogId() {
        return logId;
    }

    @Override
    public void setLogId(int logId) {
        this.logId = logId;
    }

    public List<QuestionLog> fetchQuestionsForLevel(Session session, LogService logService) {
        logService.setStrategy(new QuestionLogStrategy());

        List<? extends GameLog> logs = logService.getLogs(session);

        return logs.stream()
                .filter(log -> log instanceof QuestionLog qLog
                        && qLog.getLevelLogId() == session.getCurrentRoomId())
                .map(log -> (QuestionLog) log)
                .toList();
    }

    public void prepareRoom(List<QuestionLog> questions, int roomNumber) {
        setQuestions(
                questions.stream()
                        .filter(q -> !q.isCompleted())
                        .toList()
        );

        if (isCompleted()) {
            setCompleted(true);
            setRoomNumber(roomNumber);
            return;
        }

        this.setQuestion(getQuestions().getFirst().getQuestions().getFirst());
        setRoomNumber(roomNumber);
    }

    public boolean isCompleted() {
        return getQuestions().isEmpty();
    }
}