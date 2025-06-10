package org.scrumgame.interfaces;
import org.scrumgame.classes.Question;

public interface QuestionInteraction {
    Question getQuestion();
    String getPrompt();
    boolean checkAnswer(String answer);
    boolean isCompleted();
    int getQuestionLogId();
}
