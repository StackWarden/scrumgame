package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class DailyScrumRoomSeeder extends BaseSeeder {

    private static final int ROOM_NUMBER = 2;
    private static final String LEVEL_TYPE = "daily_scrum";

    private static final List<Question> DAILY_SCRUM_QUESTIONS = List.of(
            new Question(-1, "Wat is het doel van de Daily Scrum?", "Synchronisatie en planning", "Denk aan samenwerking."),
            new Question(-1, "Wie neemt deel aan de Daily Scrum?", "Het ontwikkelteam", "Wie werkt dagelijks samen?"),
            new Question(-1, "Wat is de maximale duur van een Daily Scrum?", "15 minuten", "Het is kort."),
            new Question(-1, "Wanneer vindt de Daily Scrum plaats?", "Elke werkdag", "Denk aan frequentie."),
            new Question(-1, "Wat bespreekt men tijdens de Daily Scrum?", "Voortgang en obstakels", "Wat helpt het team vooruit?")
    );

    public DailyScrumRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seedDailyScrumRoomForSession(int sessionId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<String, Integer> questionIdMap = new HashMap<>();
            for (Question q : DAILY_SCRUM_QUESTIONS) {
                int id = getOrInsertQuestion(conn, q);
                questionIdMap.put(q.getQuestion(), id);
            }

            int levelLogId = createLevelLog(conn, sessionId, ROOM_NUMBER, LEVEL_TYPE);

            for (int questionId : questionIdMap.values()) {
                insertQuestionLog(conn, sessionId, levelLogId, questionId);
            }

            conn.commit();
            System.out.println("Daily Scrum room successfully seeded for session " + sessionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
