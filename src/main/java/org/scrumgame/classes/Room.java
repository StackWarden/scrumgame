package org.scrumgame.classes;

public class Room extends Level {
    private final Question question;

    public Room(Question question) {
        super();
        this.question = question;
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
        return question;
    }
}
