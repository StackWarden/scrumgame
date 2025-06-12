package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class SprintReviewRoomSeeder extends BaseSeeder{

    private static final int ROOM_NUMBER = 6;
    private static final String LEVEL_TYPE = "sprintreview";

    private static final List<Question> SPRINT_REVIEW_QUESTIONS = List.of(
            new Question(-1, "What is the main goal of the Sprint Review?", "To inspect the Increment and adapt the Product Backlog", "It's about reviewing work and planning future work."),
            new Question(-1, "Who attends the Sprint Review?", "The Scrum Team and stakeholders", "Think about collaboration and feedback."),
            new Question(-1, "What is demonstrated during the Sprint Review?", "The Increment", "It’s the usable product result."),
            new Question(-1, "When does the Sprint Review take place?", "At the end of the Sprint", "It's just before the Retrospective."),
            new Question(-1, "What is a possible outcome of the Sprint Review?", "An updated Product Backlog", "It helps determine the next priorities.")
    );

    public SprintReviewRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seedSprintReviewRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : SPRINT_REVIEW_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("SprintReview room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
