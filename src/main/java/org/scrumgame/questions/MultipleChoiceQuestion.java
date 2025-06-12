package org.scrumgame.questions;

import org.scrumgame.classes.Question;

public class MultipleChoiceQuestion extends Question {

    public MultipleChoiceQuestion(int id, String question, String answer, String hint, String type) {
        super(id, question, answer, hint, type);
    }

    public MultipleChoiceQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    public String getQuestion() {
        return MultipleChoice(this.question);
    }

    public String MultipleChoice(String question) {
        
        return question;
    }
}