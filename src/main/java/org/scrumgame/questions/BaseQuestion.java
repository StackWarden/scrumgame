package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.sql.Connection;

public class BaseQuestion extends Question {
    public BaseQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public BaseQuestion(Question question, String answer) {
        super(question.getQuestion(), answer);
    }

    @Override
    public String getQuestion() {
        return this.question;
    }
}
