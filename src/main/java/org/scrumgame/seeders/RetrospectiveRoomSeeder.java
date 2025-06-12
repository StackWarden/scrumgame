package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;
import org.scrumgame.questions.BaseQuestion;

import java.sql.*;
import java.util.*;

public class RetrospectiveRoomSeeder extends BaseSeeder {
    private static final int ROOM_NUMBER = 4;
    private static final String LEVEL_TYPE = "retrospective";

    private static final List<Question> QUESTIONS = List.of(
            new BaseQuestion(-1, "What is the purpose of a Sprint Retrospective?", "To reflect and improve", "Think about learning from the Sprint."),
            new BaseQuestion(-1, "Who participates in the Sprint Retrospective?", "The Scrum Team", "Everyone involved in the Sprint."),
            new BaseQuestion(-1, "When is the Sprint Retrospective held?", "At the end of the Sprint", "Right after the Sprint Review."),
            new BaseQuestion(-1, "What is a common outcome of a Sprint Retrospective?", "Improvement action items", "Teams discuss what went well and what can be improved."),
            new BaseQuestion(-1, "How long should the Sprint Retrospective last for a one-month Sprint?", "3 hours", "Scrum Guide gives this number.")
    );

    public RetrospectiveRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seed(int sessionId) {
        insertDataToDatabase(sessionId, ROOM_NUMBER, LEVEL_TYPE, QUESTIONS);
    }
}
