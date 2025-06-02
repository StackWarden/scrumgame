package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.sql.Connection;
import java.sql.SQLException;

public class OpenQuestion extends Questions {

    @Override
    protected Question fetchQuestionFromDatabase(Connection connection, int questionId) {
        try {
            return Question.fetchQuestionById(connection, questionId);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch open question.", e);
        }
    }

    @Override
    protected void displayQuestion(Question dbQuestion) {
        System.out.println("Question: " + dbQuestion.getQuestion());

    }

    @Override
    protected void displayAnswer(Question dbQuestion) {
        System.out.println("Correct Answer: " + dbQuestion.getAnswer());
    }
}
