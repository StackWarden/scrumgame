package org.scrumgame.questions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

public class MultipleChoiceQuestion extends Question {

    @Override
    protected Question Question(Connection connection, int questionId) {
        try {
            String sql = "SELECT type FROM questions WHERE type = Multiple_Choice";
            try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)){
                return Question.fetchQuestionById(connection, questionId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to fetch multiple choice question.", e);
        }
    }

    @Override
    protected void displayQuestion(Question Question) {
        System.out.println("Question: " + getQuestion());

    }

    @Override
    protected void displayAnswer(Question Question) {
        System.out.println("Correct Answer: " + getAnswer());
    }
}