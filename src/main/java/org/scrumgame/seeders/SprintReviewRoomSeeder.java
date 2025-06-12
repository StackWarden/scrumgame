package org.scrumgame.seeders;

import org.scrumgame.classes.Question;
import org.scrumgame.questions.BaseQuestion;
import java.util.*;

public class SprintReviewRoomSeeder extends BaseSeeder{
    private static final int ROOM_NUMBER = 6;
    private static final String LEVEL_TYPE = "sprintreview";

    private static final List<Question> QUESTIONS = List.of(
            new BaseQuestion(-1, "What is the main goal of the Sprint Review?", "To inspect the Increment and adapt the Product Backlog", "It's about reviewing work and planning future work."),
            new BaseQuestion(-1, "Who attends the Sprint Review?", "The Scrum Team and stakeholders", "Think about collaboration and feedback."),
            new BaseQuestion(-1, "What is demonstrated during the Sprint Review?", "The Increment", "It’s the usable product result."),
            new BaseQuestion(-1, "When does the Sprint Review take place?", "At the end of the Sprint", "It's just before the Retrospective."),
            new BaseQuestion(-1, "What is a possible outcome of the Sprint Review?", "An updated Product Backlog", "It helps determine the next priorities.")
    );

    public SprintReviewRoomSeeder(int sessionId) {
        super(sessionId);
    }

    public void seed(int sessionId) {
        insertDataToDatabase(sessionId, ROOM_NUMBER, LEVEL_TYPE, QUESTIONS);
    }
}
