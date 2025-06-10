package org.scrumgame.questions;

import java.sql.Connection;
import java.sql.SQLException;

import org.scrumgame.classes.Question;

public abstract class Questions {

    public final void presentQuestion(Connection connection, int questionId) throws SQLException {
        Question dbQuestion = Question.fetchQuestionById(connection, questionId);
        displayQuestion(dbQuestion);
        displayAnswer(dbQuestion);
        useHint(dbQuestion);
    }

    protected abstract void displayQuestion(Question dbQuestion);

    protected abstract void displayAnswer(Question dbQuestion);

    protected void useHint(Question dbQuestion) {
        if (dbQuestion.getHint() != null && !dbQuestion.getHint().isEmpty()) {
            System.out.println("Hint: " + dbQuestion.getHint());
        }
    }
}