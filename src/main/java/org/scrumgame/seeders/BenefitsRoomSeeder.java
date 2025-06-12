package org.scrumgame.seeders;

import org.scrumgame.classes.Question;

import java.sql.*;
import java.util.*;

public class BenefitsRoomSeeder extends BaseSeeder {
    private final int ROOM_NUMBER = 1;
    private final String LEVEL_TYPE = "benefits";

    private final List<Question> QUESTIONS = List.of(
            new Question(-1, "What is one benefit of using Scrum?", "Improved communication", "Think about teamwork."),
            new Question(-1, "How does Scrum encourage adaptability?", "By using short iterations", "Sprints are useful here."),
            new Question(-1, "Why is feedback important in Scrum?", "It allows for continuous improvement", "Think about retrospectives."),
            new Question(-1, "Who inspects the product increment in Scrum?", "The stakeholders", "They review progress at Sprint Review."),
            new Question(-1, "What is one outcome of regular Daily Scrums?", "Better team alignment", "It keeps everyone on the same page.")
    );

    public BenefitsRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seed(int sessionId) {
        insertDataToDatabase(sessionId, ROOM_NUMBER, LEVEL_TYPE, QUESTIONS);
    }
}