package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ScrumBoardRoomSeeder extends BaseSeeder {
    private static final int ROOM_NUMBER = 5;
    private static final String LEVEL_TYPE = "scrumboard";

    private static final List<Question> QUESTIONS = List.of(
            new Question(-1, "What is a Scrum Board used for?", "To visualize the progress of tasks", "Think about transparency."),
            new Question(-1, "What are typical columns on a Scrum Board?", "To Do, In Progress, Done", "They represent status."),
            new Question(-1, "Who updates the Scrum Board?", "The Development Team", "They manage the work."),
            new Question(-1, "How often should the Scrum Board be updated?", "Daily", "It reflects the latest status."),
            new Question(-1, "What is the benefit of using a physical or digital Scrum Board?", "Improved visibility and collaboration", "It helps teams stay aligned.")
    );

    public ScrumBoardRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seed(int sessionId) {
        insertDataToDatabase(sessionId, ROOM_NUMBER, LEVEL_TYPE, QUESTIONS);
    }
}
