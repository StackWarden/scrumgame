package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class BenefitsRoomSeeder extends BaseSeeder {

    private final int ROOM_NUMBER = 1;
    private final String LEVEL_TYPE = "benefits";

    private final List<Question> BENEFITS_QUESTIONS = List.of(
            new Question(-1, "What is one benefit of using Scrum?", "Improved communication", "Think about teamwork."),
            new Question(-1, "How does Scrum encourage adaptability?", "By using short iterations", "Sprints are useful here."),
            new Question(-1, "Why is feedback important in Scrum?", "It allows for continuous improvement", "Think about retrospectives."),
            new Question(-1, "Who inspects the product increment in Scrum?", "The stakeholders", "They review progress at Sprint Review."),
            new Question(-1, "What is one outcome of regular Daily Scrums?", "Better team alignment", "It keeps everyone on the same page.")
    );

    public BenefitsRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seedBenefitsRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : BENEFITS_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Benefits room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}