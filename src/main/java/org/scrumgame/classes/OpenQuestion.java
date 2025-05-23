package org.scrumgame.classes;

public class OpenQuestion extends Question {
    
    public OpenQuestion(int id, String question, String answer, String hint) {
        super(id, question, answer, hint);
    }

    public OpenQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    protected boolean checkAnswer(String givenAnswer) {
        return answer.trim().equalsIgnoreCase(givenAnswer.trim());
    }

    @Override
    protected String preProcessAnswer(String givenAnswer) {
        // Remove extra whitespace from open answers
        givenAnswer = givenAnswer.trim();
        return givenAnswer;
    }
}