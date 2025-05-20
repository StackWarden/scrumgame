package org.scrumgame.database.models;

import org.scrumgame.classes.GameLog;
import org.scrumgame.classes.Question;
import org.scrumgame.database.DatabaseConnection;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class RoomLog implements GameLog {
    private int id;
    private int sessionId;
    private Question question;
    private boolean completed;

    public RoomLog(int sessionId, Question question, boolean completed) {
        this.sessionId = sessionId;
        this.question = question;
        this.completed = completed;
    }

    public int getSessionId() {
        return sessionId;
    }

    public List<Question> getQuestions() {
        return Collections.singletonList(question);
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getId() {
        return id;
    }

    public static void createRoom() {
        // try catch to connect to the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // set the query to get a random question
            String correctSql = "SELECT * FROM question ORDER BY RAND() LIMIT 1";
            // prepare the query for execution using the active database connection from the try catch loop
            PreparedStatement correctStmt = conn.prepareStatement(correctSql);
            // run the query and give a result row with data from the database that we can read out and use
            ResultSet correctRs = correctStmt.executeQuery();

            // if there is no questions in the database, print that there are no questions and return so it closes the program
            if (!correctRs.next()) {
                System.out.println("No questions found.");
                return;
            }

            // from the resultset from the query we ran, get the id from the id column's value and set it as correctId
            int correctId = correctRs.getInt("id");
            // from the resultset get the text column's value and set it to text
            String text = correctRs.getString("text");
            // from the correct_answer column get the value from the column and set it to correctAnswer
            String correctAnswer = correctRs.getString("correct_answer");
            // from the hint column get the value from the column and set it to hint
            String hint = correctRs.getString("hint");
            // create a question with type Question which holds the id, text and correctAnswer from the query
            Question question = new Question(correctId, text, correctAnswer, hint);

            // print all the data
            System.out.println("ID: " + question.getId());
            System.out.println("Question: " + question.getQuestion());
            System.out.println("Correct Answer: " + question.getAnswer());

            // Close the statement and resultset from the database so we don't keep using resources and don't get a memory leak
            correctRs.close();
            correctStmt.close();

            // set a query for creating a new room
            String insertSql = "INSERT INTO level_log (session_id, question_id, completed) VALUES (?, ?, ?)";
            // prepare the query so we can edit the data in the query to something dynamic that we did programatically before so we can't statically set it in the query above
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            // for value 1, session_id, set the correct session_id, which is statically 1 now because we don't yet have sessions
            insertStmt.setInt(1, 1);
            // for value 2, question_id, set the correct question id so we can use this later in the program from the databse
            insertStmt.setInt(2, correctId);
            // for value 3, completed, set if the question is answered or not to see where to continue
            insertStmt.setBoolean(3, false);
            // execute the query
            insertStmt.executeUpdate();
            // close the database connection
            insertStmt.close();

        // in case of an error, A catch that displays the error
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
