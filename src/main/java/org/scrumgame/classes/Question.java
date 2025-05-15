package org.scrumgame.classes;

public class Question {
    private int id;
    private String question;
    private String answer;

    public Question(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
