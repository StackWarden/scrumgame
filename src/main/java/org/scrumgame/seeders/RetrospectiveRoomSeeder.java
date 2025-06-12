package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class RetrospectiveRoomSeeder extends BaseSeeder {

    private static final int ROOM_NUMBER = 4;
    private static final String LEVEL_TYPE = "retrospective";

    private static final List<Question> RETRO_QUESTIONS = List.of(
            new Question(-1, "What is the purpose of a Sprint Retrospective?", "To reflect and improve", "Think about learning from the Sprint."),
            new Question(-1, "Who participates in the Sprint Retrospective?", "The Scrum Team", "Everyone involved in the Sprint."),
            new Question(-1, "When is the Sprint Retrospective held?", "At the end of the Sprint", "Right after the Sprint Review."),
            new Question(-1, "What is a common outcome of a Sprint Retrospective?", "Improvement action items", "Teams discuss what went well and what can be improved."),
            new Question(-1, "How long should the Sprint Retrospective last for a one-month Sprint?", "3 hours", "Scrum Guide gives this number.")
    );

    public RetrospectiveRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seedRetrospectiveRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : RETRO_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Retrospective room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
