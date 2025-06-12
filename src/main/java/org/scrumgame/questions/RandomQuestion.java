package org.scrumgame.questions;

import org.scrumgame.classes.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomQuestion {

    private static final List<Class<? extends Question>> questionClasses = new ArrayList<>();

    static {
        questionClasses.add(BaseQuestion.class);
        questionClasses.add(RiddleQuestion.class);
        questionClasses.add(MultipleChoiceQuestion.class);
    }

    public static Question getRandomQuestion() {
        Random random = new Random();
        int choice = random.nextInt(questionClasses.size());
        try {
            // Dynamically create an instance of the selected class
            return questionClasses.get(choice).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a Question instance", e);
        }
    }
}
