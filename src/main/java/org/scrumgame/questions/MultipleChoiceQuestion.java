package org.scrumgame.questions;

import org.scrumgame.classes.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultipleChoiceQuestion extends Question {

    public MultipleChoiceQuestion(int id, String question, String answer, String hint, String type) {
        super(id, question, answer, hint, type);
    }

    public MultipleChoiceQuestion(String question, String answer) {
        super(question, answer);
    }

    @Override
    public String getQuestion() {
        String[] otherOptions = {};
        return MultipleChoice(this.question, this.answer, otherOptions);
    }

    public String MultipleChoice(String questionText, String correctAnswer, String[] otherOptions) {

    List<String> options = new ArrayList<>();
    options.add(correctAnswer);
    options.addAll(Arrays.asList(otherOptions));

    Collections.shuffle(options);

    StringBuilder questionWithChoices = new StringBuilder(questionText + "\n");
    char optionLabel = 'A';
    for (String option : options) {
        questionWithChoices.append(optionLabel).append(". ").append(option).append("\n");
        optionLabel++;
    }

    questionWithChoices.append("\nPlease select the correct option (A, B, C or D):");

    return questionWithChoices.toString();
}
}