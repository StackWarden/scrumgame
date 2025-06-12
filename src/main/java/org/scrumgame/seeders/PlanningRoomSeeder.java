package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class PlanningRoomSeeder extends BaseSeeder {

    private static final int ROOM_NUMBER = 3;
    private static final String LEVEL_TYPE = "planning";

    private static final List<Question> PLANNING_QUESTIONS = List.of(
            new Question(-1, "What is the purpose of the Sprint Planning meeting?", "To plan the upcoming Sprint", "Think about setting goals."),
            new Question(-1, "Who defines the Sprint Goal?", "The Scrum Team", "It's a team effort."),
            new Question(-1, "What is the input to the Sprint Planning meeting?", "The Product Backlog", "Where do tasks come from?"),
            new Question(-1, "How long should Sprint Planning last for a one-month Sprint?", "8 hours", "Scrum Guide gives this number."),
            new Question(-1, "What is the outcome of Sprint Planning?", "Sprint Backlog", "It's the set of planned work.")
    );

    public PlanningRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seedPlanningRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : PLANNING_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Planning room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
