package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.sql.Connection;

public class TypeLessQuestion extends Question {
    public TypeLessQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public TypeLessQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    protected Question fetchQuestion(Connection connection, int questionId) {

        return null;
    }

    @Override
    protected void displayQuestion(Question question) {

    }

    @Override
    protected void displayAnswer(Question question) {

    }
}