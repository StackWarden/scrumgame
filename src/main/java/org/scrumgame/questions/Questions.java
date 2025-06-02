package org.scrumgame.questions;

import java.sql.Connection;
import org.scrumgame.classes.Question;

public abstract class Questions {

    public final void presentQuestion(Connection connection, int questionId) {
        Question dbQuestion = fetchQuestionFromDatabase(connection, questionId);
        displayQuestion(dbQuestion);
        displayAnswer(dbQuestion);
        useHint(dbQuestion);
    }

    protected abstract Question fetchQuestionFromDatabase(Connection connection, int questionId);

    protected abstract void displayQuestion(Question dbQuestion);

    protected abstract void displayAnswer(Question dbQuestion);

    protected void useHint(Question dbQuestion) {
        if (dbQuestion.getHint() != null && !dbQuestion.getHint().isEmpty()) {
            System.out.println("Hint: " + dbQuestion.getHint());
        }
    }
}