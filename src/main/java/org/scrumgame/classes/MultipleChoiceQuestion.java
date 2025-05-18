package org.scrumgame.classes;

public class MultipleChoiceQuestion extends Question {
    
    public MultipleChoiceQuestion(int id, String question, String answer) {
        super(id, question, answer);
    }

    public MultipleChoiceQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    protected boolean checkAnswer(String givenAnswer) {
        // Multiple choice answers should match exactly
        return answer.equals(givenAnswer);
    }
}